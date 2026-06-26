package de.max.prepperapp.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.max.prepperapp.R
import de.max.prepperapp.domain.ShoppingListItem

@Composable
fun PrepperListScreen(
    items: List<ShoppingListItem>,
    checkedItemKeys: Set<String>,
    hiddenItemCount: Int,
    shareText: String,
    onCheckedChange: (ShoppingListItem, Boolean) -> Unit,
    onHideItem: (ShoppingListItem) -> Unit,
    onDeleteCustomItem: (ShoppingListItem) -> Unit,
    onAddCustomItem: (String, String, Double, String) -> Unit
) {
    val context = LocalContext.current

    val shareSubject = stringResource(id = R.string.list_share_subject)
    val shareChooserTitle = stringResource(id = R.string.list_share_chooser)

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    var showOnlyOpenItems by rememberSaveable {
        mutableStateOf(false)
    }

    val customItemsCategory = stringResource(id = R.string.list_custom_items_category)
    val newCategoryOption = stringResource(id = R.string.list_new_category)

    val categoryOptions = buildList {
        items
            .map { item -> item.category }
            .distinct()
            .forEach { category ->
                add(category)
            }

        if (!contains(customItemsCategory)) {
            add(customItemsCategory)
        }

        add(newCategoryOption)
    }

    val trimmedSearchText = searchText.trim()

    val filteredItems = items.filter { item ->
        val matchesSearch = trimmedSearchText.isBlank() ||
                item.name.contains(trimmedSearchText, ignoreCase = true) ||
                item.category.contains(trimmedSearchText, ignoreCase = true) ||
                item.unit.contains(trimmedSearchText, ignoreCase = true)

        val matchesOpenFilter = !showOnlyOpenItems ||
                !checkedItemKeys.contains(item.prepperKey())

        matchesSearch && matchesOpenFilter
    }

    val checkedItemCount = items.count { item ->
        checkedItemKeys.contains(item.prepperKey())
    }

    val isFiltered = trimmedSearchText.isNotBlank() || showOnlyOpenItems

    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PrepperListHintCard()

        PrepperAddCustomItemCard(
            categoryOptions = categoryOptions,
            onAddCustomItem = onAddCustomItem
        )

        PrepperListFilterCard(
            searchText = searchText,
            showOnlyOpenItems = showOnlyOpenItems,
            allItemCount = items.size,
            filteredItemCount = filteredItems.size,
            checkedItemCount = checkedItemCount,
            onSearchTextChange = { value ->
                searchText = value
            },
            onShowOnlyOpenItemsChange = { checked ->
                showOnlyOpenItems = checked
            }
        )

        PrepperShoppingListCard(
            items = filteredItems,
            checkedItemKeys = checkedItemKeys,
            hiddenItemCount = hiddenItemCount,
            allItemCount = items.size,
            checkedItemCount = checkedItemCount,
            isFiltered = isFiltered,
            onCheckedChange = onCheckedChange,
            onHideItem = onHideItem,
            onDeleteCustomItem = onDeleteCustomItem
        )

        Button(
            onClick = {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }

                val shareIntent = Intent.createChooser(
                    sendIntent,
                    shareChooserTitle
                )

                context.startActivity(shareIntent)
            },
            enabled = items.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.list_share_button))
        }
    }
}

@Composable
fun PrepperListHintCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.list_hint_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.list_hint_checked),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.list_hint_hide),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.list_hint_delete_custom),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PrepperAddCustomItemCard(
    categoryOptions: List<String>,
    onAddCustomItem: (String, String, Double, String) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    var name by rememberSaveable {
        mutableStateOf("")
    }

    var quantityText by rememberSaveable {
        mutableStateOf("1")
    }

    val customUnitOption = stringResource(id = R.string.list_custom_unit_option)
    val defaultUnit = "stk"

    var selectedUnit by rememberSaveable {
        mutableStateOf(defaultUnit)
    }

    var customUnit by rememberSaveable {
        mutableStateOf("")
    }

    val customItemsCategory = stringResource(id = R.string.list_custom_items_category)
    val newCategoryOption = stringResource(id = R.string.list_new_category)

    var selectedCategory by rememberSaveable {
        mutableStateOf(customItemsCategory)
    }

    var customCategory by rememberSaveable {
        mutableStateOf("")
    }

    val quantity = quantityText
        .replace(",", ".")
        .toDoubleOrNull()

    val finalCategory = if (selectedCategory == newCategoryOption) {
        customCategory.trim()
    } else {
        selectedCategory.trim()
    }

    val finalUnit = if (selectedUnit == customUnitOption) {
        customUnit.trim()
    } else {
        selectedUnit.trim()
    }

    val canAddItem =
        name.isNotBlank() &&
                finalCategory.isNotBlank() &&
                finalUnit.isNotBlank() &&
                quantity != null &&
                quantity > 0.0

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.list_add_custom_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (expanded) {
                            stringResource(id = R.string.list_add_custom_expanded)
                        } else {
                            stringResource(id = R.string.list_add_custom_collapsed)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = if (expanded) "−" else "+",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (expanded) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { value ->
                        name = value
                    },
                    label = {
                        Text(text = stringResource(id = R.string.list_item_name))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { value ->
                            quantityText = value
                        },
                        label = {
                            Text(text = stringResource(id = R.string.list_quantity))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    PrepperDropdownField(
                        label = stringResource(id = R.string.list_unit),
                        selectedValue = selectedUnit,
                        options = listOf(
                            "g",
                            "kg",
                            "ml",
                            "l",
                            "stk",
                            stringResource(id = R.string.list_unit_packs),
                            customUnitOption
                        ),
                        onSelectedValueChange = { value ->
                            selectedUnit = value
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedUnit == customUnitOption) {
                    OutlinedTextField(
                        value = customUnit,
                        onValueChange = { value ->
                            customUnit = value
                        },
                        label = {
                            Text(text = stringResource(id = R.string.list_custom_unit))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PrepperDropdownField(
                    label = stringResource(id = R.string.list_category),
                    selectedValue = selectedCategory,
                    options = categoryOptions,
                    onSelectedValueChange = { value ->
                        selectedCategory = value
                    }
                )

                if (selectedCategory == newCategoryOption) {
                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { value ->
                            customCategory = value
                        },
                        label = {
                            Text(text = stringResource(id = R.string.list_new_category))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        val safeQuantity = quantity ?: return@Button

                        onAddCustomItem(
                            name.trim(),
                            finalCategory,
                            safeQuantity,
                            finalUnit
                        )

                        name = ""
                        quantityText = "1"
                        expanded = false
                    },
                    enabled = canAddItem,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.list_add_item_button))
                }
            }
        }
    }
}

@Composable
fun PrepperListFilterCard(
    searchText: String,
    showOnlyOpenItems: Boolean,
    allItemCount: Int,
    filteredItemCount: Int,
    checkedItemCount: Int,
    onSearchTextChange: (String) -> Unit,
    onShowOnlyOpenItemsChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.list_filter_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                label = {
                    Text(text = stringResource(id = R.string.list_search_label))
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.list_show_only_open),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = stringResource(
                            id = R.string.list_filter_status,
                            filteredItemCount,
                            allItemCount,
                            checkedItemCount
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Switch(
                    checked = showOnlyOpenItems,
                    onCheckedChange = onShowOnlyOpenItemsChange
                )
            }
        }
    }
}

@Composable
fun PrepperDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelectedValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ) {
        Button(
            onClick = {
                menuExpanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    id = R.string.list_dropdown_value,
                    label,
                    selectedValue
                )
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option)
                    },
                    onClick = {
                        onSelectedValueChange(option)
                        menuExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PrepperShoppingListCard(
    items: List<ShoppingListItem>,
    checkedItemKeys: Set<String>,
    hiddenItemCount: Int,
    allItemCount: Int,
    checkedItemCount: Int,
    isFiltered: Boolean,
    onCheckedChange: (ShoppingListItem, Boolean) -> Unit,
    onHideItem: (ShoppingListItem) -> Unit,
    onDeleteCustomItem: (ShoppingListItem) -> Unit
) {
    val groupedItems = items.groupBy { it.category }
    val categoryNames = groupedItems.keys.toList()
    val categorySaveKey = categoryNames.joinToString(separator = "|")

    var expandedCategories by rememberSaveable(categorySaveKey) {
        mutableStateOf(categoryNames)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.list_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (isFiltered) {
                    stringResource(
                        id = R.string.list_done_status_filtered,
                        items.size,
                        allItemCount,
                        checkedItemCount
                    )
                } else {
                    stringResource(
                        id = R.string.list_done_status,
                        checkedItemCount,
                        allItemCount
                    )
                },
                style = MaterialTheme.typography.bodyMedium
            )

            if (hiddenItemCount > 0) {
                Text(
                    text = stringResource(
                        id = R.string.list_hidden_status,
                        hiddenItemCount
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        expandedCategories = categoryNames
                    }
                ) {
                    Text(text = stringResource(id = R.string.list_open_all))
                }

                TextButton(
                    onClick = {
                        expandedCategories = emptyList()
                    }
                ) {
                    Text(text = stringResource(id = R.string.list_close_all))
                }
            }

            if (items.isEmpty()) {
                Text(
                    text = if (isFiltered) {
                        stringResource(id = R.string.list_empty_filtered)
                    } else {
                        stringResource(id = R.string.list_empty_all_hidden)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            groupedItems.entries.forEachIndexed { index, entry ->
                val categoryName = entry.key
                val categoryItems = entry.value
                val isExpanded = expandedCategories.contains(categoryName)

                val checkedInCategory = categoryItems.count { item ->
                    checkedItemKeys.contains(item.prepperKey())
                }

                PrepperShoppingCategorySection(
                    category = categoryName,
                    items = categoryItems,
                    checkedInCategory = checkedInCategory,
                    isExpanded = isExpanded,
                    checkedItemKeys = checkedItemKeys,
                    onToggleExpanded = {
                        expandedCategories = if (isExpanded) {
                            expandedCategories - categoryName
                        } else {
                            expandedCategories + categoryName
                        }
                    },
                    onCheckedChange = onCheckedChange,
                    onHideItem = onHideItem,
                    onDeleteCustomItem = onDeleteCustomItem
                )

                if (index < groupedItems.entries.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun PrepperShoppingCategorySection(
    category: String,
    items: List<ShoppingListItem>,
    checkedInCategory: Int,
    isExpanded: Boolean,
    checkedItemKeys: Set<String>,
    onToggleExpanded: () -> Unit,
    onCheckedChange: (ShoppingListItem, Boolean) -> Unit,
    onHideItem: (ShoppingListItem) -> Unit,
    onDeleteCustomItem: (ShoppingListItem) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onToggleExpanded()
                }
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        id = R.string.list_category_done,
                        checkedInCategory,
                        items.size
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = if (isExpanded) "−" else "+",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEach { item ->
                    PrepperShoppingListRow(
                        item = item,
                        isChecked = checkedItemKeys.contains(item.prepperKey()),
                        onCheckedChange = onCheckedChange,
                        onHideItem = onHideItem,
                        onDeleteCustomItem = onDeleteCustomItem
                    )
                }
            }
        }
    }
}

@Composable
fun PrepperShoppingListRow(
    item: ShoppingListItem,
    isChecked: Boolean,
    onCheckedChange: (ShoppingListItem, Boolean) -> Unit,
    onHideItem: (ShoppingListItem) -> Unit,
    onDeleteCustomItem: (ShoppingListItem) -> Unit
) {
    val isCustomItem = item.id.startsWith("custom_")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                onCheckedChange(item, checked)
            }
        )

        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = if (isChecked) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
        )

        Text(
            text = formatPrepperShoppingQuantity(item),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                textDecoration = if (isChecked) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                }
            )
        )

        IconButton(
            onClick = {
                if (isCustomItem) {
                    onDeleteCustomItem(item)
                } else {
                    onHideItem(item)
                }
            }
        ) {
            Text(
                text = "×",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}