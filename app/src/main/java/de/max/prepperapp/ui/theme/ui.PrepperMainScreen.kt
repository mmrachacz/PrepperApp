package de.max.prepperapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.max.prepperapp.R
import de.max.prepperapp.data.PrepperStorage
import de.max.prepperapp.domain.HouseholdProfile
import de.max.prepperapp.domain.ShoppingListItem
import de.max.prepperapp.domain.SupplyCalculator
import de.max.prepperapp.ui.theme.PrepperAppTheme
import androidx.compose.foundation.background

enum class PrepperScreen {
    OVERVIEW,
    PROFILE,
    LIST
}

@Composable
fun PrepperMainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val storage = remember(context) {
        PrepperStorage(context)
    }

    var currentScreen by rememberSaveable {
        mutableStateOf(PrepperScreen.OVERVIEW)
    }

    var adults by rememberSaveable {
        mutableIntStateOf(storage.loadAdults())
    }

    var smallChildren by rememberSaveable {
        mutableIntStateOf(storage.loadSmallChildren())
    }

    var bigChildren by rememberSaveable {
        mutableIntStateOf(storage.loadBigChildren())
    }

    var needsDiapers by rememberSaveable {
        mutableStateOf(storage.loadNeedsDiapers())
    }

    var dayDiaperChildren by rememberSaveable {
        mutableIntStateOf(storage.loadDayDiaperChildren())
    }

    var nightDiaperChildren by rememberSaveable {
        mutableIntStateOf(storage.loadNightDiaperChildren())
    }

    var days by rememberSaveable {
        mutableIntStateOf(storage.loadDays())
    }

    var hasDog by rememberSaveable {
        mutableStateOf(storage.loadHasDog())
    }

    var preferBudget by rememberSaveable {
        mutableStateOf(storage.loadPreferBudget())
    }

    var preferVegetarian by rememberSaveable {
        mutableStateOf(storage.loadPreferVegetarian())
    }

    var preferLowCooking by rememberSaveable {
        mutableStateOf(storage.loadPreferLowCooking())
    }

    var useDarkTheme by rememberSaveable {
        mutableStateOf(storage.loadUseDarkTheme())
    }

    var checkedItemKeys by remember {
        mutableStateOf(storage.loadCheckedItemKeys())
    }

    var hiddenItemKeys by remember {
        mutableStateOf(storage.loadHiddenItemKeys())
    }

    var customItems by remember {
        mutableStateOf(storage.loadCustomItems())
    }

    val totalChildren = smallChildren + bigChildren

    LaunchedEffect(totalChildren, dayDiaperChildren, nightDiaperChildren) {
        val correctedDayDiaperChildren = dayDiaperChildren
            .coerceIn(0, totalChildren)

        val correctedNightDiaperChildren = nightDiaperChildren
            .coerceIn(0, (totalChildren - correctedDayDiaperChildren).coerceAtLeast(0))

        if (correctedDayDiaperChildren != dayDiaperChildren) {
            dayDiaperChildren = correctedDayDiaperChildren
        }

        if (correctedNightDiaperChildren != nightDiaperChildren) {
            nightDiaperChildren = correctedNightDiaperChildren
        }
    }

    val effectiveDayDiaperChildren = if (needsDiapers) {
        dayDiaperChildren.coerceIn(0, totalChildren)
    } else {
        0
    }

    val effectiveNightDiaperChildren = if (needsDiapers) {
        nightDiaperChildren.coerceIn(
            0,
            (totalChildren - effectiveDayDiaperChildren).coerceAtLeast(0)
        )
    } else {
        0
    }

    LaunchedEffect(
        adults,
        smallChildren,
        bigChildren,
        needsDiapers,
        dayDiaperChildren,
        nightDiaperChildren,
        days,
        hasDog,
        preferBudget,
        preferVegetarian,
        preferLowCooking,
        useDarkTheme,
        checkedItemKeys,
        hiddenItemKeys,
        customItems
    ) {
        storage.saveState(
            adults = adults,
            smallChildren = smallChildren,
            bigChildren = bigChildren,
            needsDiapers = needsDiapers,
            dayDiaperChildren = dayDiaperChildren,
            nightDiaperChildren = nightDiaperChildren,
            days = days,
            hasDog = hasDog,
            preferBudget = preferBudget,
            preferVegetarian = preferVegetarian,
            preferLowCooking = preferLowCooking,
            useDarkTheme = useDarkTheme,
            checkedItemKeys = checkedItemKeys,
            hiddenItemKeys = hiddenItemKeys,
            customItems = customItems
        )
    }

    val profile = HouseholdProfile(
        adults = adults,
        smallChildren = smallChildren,
        bigChildren = bigChildren,
        dayDiaperChildren = effectiveDayDiaperChildren,
        nightDiaperChildren = effectiveNightDiaperChildren,
        days = days,
        hasDog = hasDog,
        preferBudget = preferBudget,
        preferVegetarian = preferVegetarian,
        preferLowCooking = preferLowCooking
    )

    val calculation = SupplyCalculator.calculate(profile)

    val localizedShoppingItems = localizePrepperSupplyItems(
        context = context,
        items = calculation.shoppingList
    )

    val allShoppingItems = localizedShoppingItems + customItems

    val visibleShoppingItems = allShoppingItems.filterNot { item ->
        hiddenItemKeys.contains(item.prepperKey())
    }

    val visibleCheckedItemKeys = checkedItemKeys.filter { checkedKey ->
        visibleShoppingItems.any { item ->
            item.prepperKey() == checkedKey
        }
    }.toSet()

    val shareText = buildPrepperShareText(
        context = context,
        profile = profile,
        items = visibleShoppingItems,
        checkedItemKeys = visibleCheckedItemKeys
    )

    PrepperAppTheme(
        darkTheme = useDarkTheme
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PrepperTopHeader(
                    useDarkTheme = useDarkTheme
                )

                PrepperScreenSelector(
                    currentScreen = currentScreen,
                    onScreenSelected = { selectedScreen ->
                        currentScreen = selectedScreen
                    }
                )

                when (currentScreen) {
                    PrepperScreen.OVERVIEW -> {
                        PrepperOverviewScreen(
                            adults = adults,
                            smallChildren = smallChildren,
                            bigChildren = bigChildren,
                            dayDiaperChildren = effectiveDayDiaperChildren,
                            nightDiaperChildren = effectiveNightDiaperChildren,
                            days = days,
                            hasDog = hasDog,
                            checkedItemCount = visibleShoppingItems.count { item ->
                                visibleCheckedItemKeys.contains(item.prepperKey())
                            },
                            totalItemCount = visibleShoppingItems.size,
                            waterLiters = calculation.waterLiters,
                            pastaOrRiceKg = calculation.pastaOrRiceKg,
                            tomatoSauceGlasses = calculation.tomatoSauceGlasses,
                            cannedMeals = calculation.cannedMeals,
                            gasCartridges = calculation.gasCartridges,
                            onOpenProfile = {
                                currentScreen = PrepperScreen.PROFILE
                            },
                            onOpenList = {
                                currentScreen = PrepperScreen.LIST
                            }
                        )
                    }

                    PrepperScreen.PROFILE -> {
                        PrepperProfileScreen(
                            adults = adults,
                            smallChildren = smallChildren,
                            bigChildren = bigChildren,
                            needsDiapers = needsDiapers,
                            dayDiaperChildren = dayDiaperChildren,
                            nightDiaperChildren = nightDiaperChildren,
                            days = days,
                            hasDog = hasDog,
                            preferBudget = preferBudget,
                            preferVegetarian = preferVegetarian,
                            preferLowCooking = preferLowCooking,
                            useDarkTheme = useDarkTheme,
                            checkedItemCount = visibleCheckedItemKeys.size,
                            hiddenItemCount = hiddenItemKeys.size,
                            customItemCount = customItems.size,
                            onAdultsMinus = { adults-- },
                            onAdultsPlus = { adults++ },
                            onSmallChildrenMinus = { smallChildren-- },
                            onSmallChildrenPlus = { smallChildren++ },
                            onBigChildrenMinus = { bigChildren-- },
                            onBigChildrenPlus = { bigChildren++ },
                            onNeedsDiapersChange = { checked ->
                                needsDiapers = checked
                            },
                            onDayDiaperChildrenMinus = { dayDiaperChildren-- },
                            onDayDiaperChildrenPlus = { dayDiaperChildren++ },
                            onNightDiaperChildrenMinus = { nightDiaperChildren-- },
                            onNightDiaperChildrenPlus = { nightDiaperChildren++ },
                            onDaysMinus = { days-- },
                            onDaysPlus = { days++ },
                            onDaysPresetSelected = { selectedDays ->
                                days = selectedDays
                            },
                            onHasDogChange = { checked ->
                                hasDog = checked
                            },
                            onPreferBudgetChange = { checked ->
                                preferBudget = checked
                            },
                            onPreferVegetarianChange = { checked ->
                                preferVegetarian = checked
                            },
                            onPreferLowCookingChange = { checked ->
                                preferLowCooking = checked
                            },
                            onUseDarkThemeChange = { checked ->
                                useDarkTheme = checked
                            },
                            onResetCheckedItems = {
                                checkedItemKeys = emptySet()
                            },
                            onResetHiddenItems = {
                                hiddenItemKeys = emptySet()
                            },
                            onDeleteCustomItems = {
                                val customItemKeys = customItems.map { item ->
                                    item.prepperKey()
                                }.toSet()

                                customItems = emptyList()
                                checkedItemKeys = checkedItemKeys - customItemKeys
                                hiddenItemKeys = hiddenItemKeys - customItemKeys
                            },
                            onResetProfile = {
                                adults = 2
                                smallChildren = 1
                                bigChildren = 1
                                needsDiapers = false
                                dayDiaperChildren = 0
                                nightDiaperChildren = 0
                                days = 30
                                hasDog = true
                                preferBudget = true
                                preferVegetarian = false
                                preferLowCooking = false
                            },
                            onResetAll = {
                                adults = 2
                                smallChildren = 1
                                bigChildren = 1
                                needsDiapers = false
                                dayDiaperChildren = 0
                                nightDiaperChildren = 0
                                days = 30
                                hasDog = true
                                preferBudget = true
                                preferVegetarian = false
                                preferLowCooking = false
                                useDarkTheme = false
                                checkedItemKeys = emptySet()
                                hiddenItemKeys = emptySet()
                                customItems = emptyList()
                            }
                        )
                    }

                    PrepperScreen.LIST -> {
                        PrepperListScreen(
                            items = visibleShoppingItems,
                            checkedItemKeys = visibleCheckedItemKeys,
                            hiddenItemCount = hiddenItemKeys.size,
                            shareText = shareText,
                            onCheckedChange = { item, checked ->
                                val key = item.prepperKey()

                                checkedItemKeys = if (checked) {
                                    checkedItemKeys + key
                                } else {
                                    checkedItemKeys - key
                                }
                            },
                            onHideItem = { item ->
                                val key = item.prepperKey()
                                hiddenItemKeys = hiddenItemKeys + key
                                checkedItemKeys = checkedItemKeys - key
                            },
                            onDeleteCustomItem = { item ->
                                val key = item.prepperKey()

                                customItems = customItems.filterNot { customItem ->
                                    customItem.prepperKey() == key
                                }

                                checkedItemKeys = checkedItemKeys - key
                                hiddenItemKeys = hiddenItemKeys - key
                            },
                            onAddCustomItem = { name, category, quantity, unit ->
                                val newItem = ShoppingListItem(
                                    id = "custom_${System.currentTimeMillis()}",
                                    name = name,
                                    category = category.ifBlank { "Eigene Artikel" },
                                    quantity = quantity,
                                    unit = unit.ifBlank { "Stück" }
                                )

                                customItems = customItems + newItem
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrepperTopHeader(
    useDarkTheme: Boolean
) {
    val headerImage = if (useDarkTheme) {
        R.drawable.prepper_header_dark
    } else {
        R.drawable.prepper_header_light
    }

    val titleColor = if (useDarkTheme) {
        Color(0xFFFFF2DB)
    } else {
        MaterialTheme.colorScheme.primary
    }

    val subtitleColor = if (useDarkTheme) {
        Color(0xFFE6DCCB)
    } else {
        Color(0xFF4F5A4E)
    }

    val chipContainerColor = if (useDarkTheme) {
        Color(0xFF293020).copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f)
    }

    val chipContentColor = if (useDarkTheme) {
        Color(0xFFFFDDB1)
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val imageOverlayColor = if (useDarkTheme) {
        Color(0xFF11150F).copy(alpha = 0.34f)
    } else {
        Color.White.copy(alpha = 0.42f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = headerImage),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterEnd
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(imageOverlayColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineLarge,
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(id = R.string.header_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = subtitleColor
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(0.82f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrepperHeaderChip(
                        text = stringResource(id = R.string.header_chip_family),
                        containerColor = chipContainerColor,
                        contentColor = chipContentColor
                    )

                    PrepperHeaderChip(
                        text = stringResource(id = R.string.header_chip_supplies),
                        containerColor = chipContainerColor,
                        contentColor = chipContentColor
                    )

                    PrepperHeaderChip(
                        text = stringResource(id = R.string.header_chip_checklist),
                        containerColor = chipContainerColor,
                        contentColor = chipContentColor
                    )
                }
            }
        }
    }
}

@Composable
fun PrepperHeaderChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrepperScreenSelector(
    currentScreen: PrepperScreen,
    onScreenSelected: (PrepperScreen) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PrepperNavigationButton(
                text = stringResource(id = R.string.navigation_overview),
                selected = currentScreen == PrepperScreen.OVERVIEW,
                onClick = {
                    onScreenSelected(PrepperScreen.OVERVIEW)
                },
                modifier = Modifier.weight(1f)
            )

            PrepperNavigationButton(
                text = stringResource(id = R.string.navigation_profile),
                selected = currentScreen == PrepperScreen.PROFILE,
                onClick = {
                    onScreenSelected(PrepperScreen.PROFILE)
                },
                modifier = Modifier.weight(1f)
            )

            PrepperNavigationButton(
                text = stringResource(id = R.string.navigation_list),
                selected = currentScreen == PrepperScreen.LIST,
                onClick = {
                    onScreenSelected(PrepperScreen.LIST)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PrepperNavigationButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable {
            onClick()
        },
        shape = MaterialTheme.shapes.large,
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrepperMainScreenPreview() {
    PrepperMainScreen()
}