package de.max.prepperapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.max.prepperapp.R

@Composable
fun PrepperProfileScreen(
    adults: Int,
    smallChildren: Int,
    bigChildren: Int,
    needsDiapers: Boolean,
    dayDiaperChildren: Int,
    nightDiaperChildren: Int,
    days: Int,
    hasDog: Boolean,
    preferBudget: Boolean,
    preferVegetarian: Boolean,
    preferLowCooking: Boolean,
    useDarkTheme: Boolean,
    checkedItemCount: Int,
    hiddenItemCount: Int,
    customItemCount: Int,
    onAdultsMinus: () -> Unit,
    onAdultsPlus: () -> Unit,
    onSmallChildrenMinus: () -> Unit,
    onSmallChildrenPlus: () -> Unit,
    onBigChildrenMinus: () -> Unit,
    onBigChildrenPlus: () -> Unit,
    onNeedsDiapersChange: (Boolean) -> Unit,
    onDayDiaperChildrenMinus: () -> Unit,
    onDayDiaperChildrenPlus: () -> Unit,
    onNightDiaperChildrenMinus: () -> Unit,
    onNightDiaperChildrenPlus: () -> Unit,
    onDaysMinus: () -> Unit,
    onDaysPlus: () -> Unit,
    onDaysPresetSelected: (Int) -> Unit,
    onHasDogChange: (Boolean) -> Unit,
    onPreferBudgetChange: (Boolean) -> Unit,
    onPreferVegetarianChange: (Boolean) -> Unit,
    onPreferLowCookingChange: (Boolean) -> Unit,
    onUseDarkThemeChange: (Boolean) -> Unit,
    onResetCheckedItems: () -> Unit,
    onResetHiddenItems: () -> Unit,
    onDeleteCustomItems: () -> Unit,
    onResetProfile: () -> Unit,
    onResetAll: () -> Unit
) {
    val totalChildren = smallChildren + bigChildren
    val maxDayDiaperChildren = (totalChildren - nightDiaperChildren).coerceAtLeast(0)
    val maxNightDiaperChildren = (totalChildren - dayDiaperChildren).coerceAtLeast(0)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrepperSectionCard(
            title = stringResource(id = R.string.profile_section_title),
            subtitle = stringResource(id = R.string.profile_section_subtitle)
        ) {
            PrepperCounterRow(
                label = stringResource(id = R.string.profile_adults),
                value = adults,
                minValue = 1,
                onMinus = onAdultsMinus,
                onPlus = onAdultsPlus
            )

            PrepperSoftDivider()

            PrepperCounterRow(
                label = stringResource(id = R.string.profile_small_children),
                value = smallChildren,
                minValue = 0,
                onMinus = onSmallChildrenMinus,
                onPlus = onSmallChildrenPlus
            )

            PrepperSoftDivider()

            PrepperCounterRow(
                label = stringResource(id = R.string.profile_big_children),
                value = bigChildren,
                minValue = 0,
                onMinus = onBigChildrenMinus,
                onPlus = onBigChildrenPlus
            )

            PrepperSoftDivider()

            PrepperCounterRow(
                label = stringResource(id = R.string.profile_days),
                value = days,
                minValue = 1,
                onMinus = onDaysMinus,
                onPlus = onDaysPlus
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrepperPresetButton(
                    text = stringResource(id = R.string.profile_days_preset, 7),
                    selected = days == 7,
                    onClick = {
                        onDaysPresetSelected(7)
                    },
                    modifier = Modifier.weight(1f)
                )

                PrepperPresetButton(
                    text = stringResource(id = R.string.profile_days_preset, 14),
                    selected = days == 14,
                    onClick = {
                        onDaysPresetSelected(14)
                    },
                    modifier = Modifier.weight(1f)
                )

                PrepperPresetButton(
                    text = stringResource(id = R.string.profile_days_preset, 30),
                    selected = days == 30,
                    onClick = {
                        onDaysPresetSelected(30)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            PrepperSoftDivider()

            PrepperSwitchRow(
                label = stringResource(id = R.string.profile_has_dog),
                checked = hasDog,
                onCheckedChange = onHasDogChange
            )
        }

        if (totalChildren > 0) {
            PrepperDiaperSettingsCard(
                needsDiapers = needsDiapers,
                dayDiaperChildren = dayDiaperChildren,
                nightDiaperChildren = nightDiaperChildren,
                maxDayDiaperChildren = maxDayDiaperChildren,
                maxNightDiaperChildren = maxNightDiaperChildren,
                onNeedsDiapersChange = onNeedsDiapersChange,
                onDayDiaperChildrenMinus = onDayDiaperChildrenMinus,
                onDayDiaperChildrenPlus = onDayDiaperChildrenPlus,
                onNightDiaperChildrenMinus = onNightDiaperChildrenMinus,
                onNightDiaperChildrenPlus = onNightDiaperChildrenPlus
            )
        }

        PrepperSectionCard(
            title = stringResource(id = R.string.profile_preferences_title),
            subtitle = stringResource(id = R.string.profile_preferences_subtitle)
        ) {
            PrepperSwitchRow(
                label = stringResource(id = R.string.profile_prefer_budget),
                checked = preferBudget,
                onCheckedChange = onPreferBudgetChange
            )

            PrepperSoftDivider()

            PrepperSwitchRow(
                label = stringResource(id = R.string.profile_prefer_vegetarian),
                checked = preferVegetarian,
                onCheckedChange = onPreferVegetarianChange
            )

            PrepperSoftDivider()

            PrepperSwitchRow(
                label = stringResource(id = R.string.profile_prefer_low_cooking),
                checked = preferLowCooking,
                onCheckedChange = onPreferLowCookingChange
            )
        }

        PrepperAppearanceCard(
            useDarkTheme = useDarkTheme,
            onUseDarkThemeChange = onUseDarkThemeChange
        )

        PrepperResetSettingsCard(
            checkedItemCount = checkedItemCount,
            hiddenItemCount = hiddenItemCount,
            customItemCount = customItemCount,
            onResetCheckedItems = onResetCheckedItems,
            onResetHiddenItems = onResetHiddenItems,
            onDeleteCustomItems = onDeleteCustomItems,
            onResetProfile = onResetProfile,
            onResetAll = onResetAll
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.profile_hint_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = stringResource(id = R.string.profile_hint_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun PrepperSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun PrepperSoftDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    )
}

@Composable
fun PrepperAppearanceCard(
    useDarkTheme: Boolean,
    onUseDarkThemeChange: (Boolean) -> Unit
) {
    PrepperSectionCard(
        title = stringResource(id = R.string.profile_appearance_title),
        subtitle = stringResource(id = R.string.profile_appearance_subtitle)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrepperPresetButton(
                text = stringResource(id = R.string.profile_theme_light),
                selected = !useDarkTheme,
                onClick = {
                    onUseDarkThemeChange(false)
                },
                modifier = Modifier.weight(1f)
            )

            PrepperPresetButton(
                text = stringResource(id = R.string.profile_theme_dark),
                selected = useDarkTheme,
                onClick = {
                    onUseDarkThemeChange(true)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PrepperResetSettingsCard(
    checkedItemCount: Int,
    hiddenItemCount: Int,
    customItemCount: Int,
    onResetCheckedItems: () -> Unit,
    onResetHiddenItems: () -> Unit,
    onDeleteCustomItems: () -> Unit,
    onResetProfile: () -> Unit,
    onResetAll: () -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    PrepperSectionCard(
        title = stringResource(id = R.string.profile_reset_title),
        subtitle = stringResource(
            id = R.string.profile_reset_subtitle,
            checkedItemCount,
            hiddenItemCount,
            customItemCount
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (expanded) {
                    stringResource(id = R.string.profile_reset_hide_options)
                } else {
                    stringResource(id = R.string.profile_reset_show_options)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (expanded) "−" else "+",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (expanded) {
            OutlinedButton(
                onClick = onResetCheckedItems,
                enabled = checkedItemCount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.profile_reset_checked_items))
            }

            OutlinedButton(
                onClick = onResetHiddenItems,
                enabled = hiddenItemCount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.profile_reset_hidden_items))
            }

            OutlinedButton(
                onClick = onDeleteCustomItems,
                enabled = customItemCount > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.profile_reset_custom_items))
            }

            OutlinedButton(
                onClick = onResetProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.profile_reset_profile))
            }

            Button(
                onClick = onResetAll,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.profile_reset_all))
            }

            Text(
                text = stringResource(id = R.string.profile_reset_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PrepperDiaperSettingsCard(
    needsDiapers: Boolean,
    dayDiaperChildren: Int,
    nightDiaperChildren: Int,
    maxDayDiaperChildren: Int,
    maxNightDiaperChildren: Int,
    onNeedsDiapersChange: (Boolean) -> Unit,
    onDayDiaperChildrenMinus: () -> Unit,
    onDayDiaperChildrenPlus: () -> Unit,
    onNightDiaperChildrenMinus: () -> Unit,
    onNightDiaperChildrenPlus: () -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(needsDiapers)
    }

    val subtitle = if (needsDiapers) {
        if (expanded) {
            stringResource(id = R.string.profile_diapers_subtitle_expanded)
        } else {
            stringResource(id = R.string.profile_diapers_subtitle_collapsed)
        }
    } else {
        stringResource(id = R.string.profile_diapers_subtitle_disabled)
    }

    PrepperSectionCard(
        title = stringResource(id = R.string.profile_diapers_title),
        subtitle = subtitle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = needsDiapers) {
                    expanded = !expanded
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrepperSwitchRow(
                label = stringResource(id = R.string.profile_diapers_switch),
                checked = needsDiapers,
                onCheckedChange = { checked ->
                    onNeedsDiapersChange(checked)
                    expanded = checked
                },
                modifier = Modifier.weight(1f)
            )

            if (needsDiapers) {
                Text(
                    text = if (expanded) "−" else "+",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (needsDiapers && expanded) {
            Text(
                text = stringResource(id = R.string.profile_diapers_explanation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PrepperCounterRow(
                label = stringResource(id = R.string.profile_day_diaper_children),
                value = dayDiaperChildren,
                minValue = 0,
                maxValue = maxDayDiaperChildren,
                onMinus = onDayDiaperChildrenMinus,
                onPlus = onDayDiaperChildrenPlus
            )

            PrepperSoftDivider()

            PrepperCounterRow(
                label = stringResource(id = R.string.profile_night_diaper_children),
                value = nightDiaperChildren,
                minValue = 0,
                maxValue = maxNightDiaperChildren,
                onMinus = onNightDiaperChildrenMinus,
                onPlus = onNightDiaperChildrenPlus
            )
        }
    }
}

@Composable
fun PrepperSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun PrepperPresetButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 44.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.heightIn(min = 44.dp),
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun PrepperCounterRow(
    label: String,
    value: Int,
    minValue: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    maxValue: Int? = null
) {
    val plusEnabled = maxValue == null || value < maxValue

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrepperCounterButton(
                text = "−",
                enabled = value > minValue,
                onClick = onMinus
            )

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp)
            )

            PrepperCounterButton(
                text = "+",
                enabled = plusEnabled,
                onClick = onPlus,
                highlighted = true
            )
        }
    }
}

@Composable
fun PrepperCounterButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    highlighted: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.width(48.dp),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
            } else {
                Color.Transparent
            },
            contentColor = if (highlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        ),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}