package de.max.prepperapp.ui

import de.max.prepperapp.domain.HouseholdProfile
import de.max.prepperapp.domain.ShoppingListItem

fun buildPrepperShareText(
    profile: HouseholdProfile,
    items: List<ShoppingListItem>,
    checkedItemKeys: Set<String>
): String {
    val builder = StringBuilder()

    builder.appendLine("PrepperApp Einkaufsliste")
    builder.appendLine(
        "${profile.days} Tage · " +
                "${profile.adults} Erwachsene · " +
                "${profile.smallChildren} kleine Kinder · " +
                "${profile.bigChildren} große Kinder · " +
                if (profile.hasDog) "1 Hund" else "kein Hund"
    )

    if (profile.dayDiaperChildren > 0 || profile.nightDiaperChildren > 0) {
        builder.appendLine(
            "${profile.dayDiaperChildren} mit Tageswindeln · " +
                    "${profile.nightDiaperChildren} mit Nachtwindeln"
        )
    }

    val preferences = buildList {
        if (profile.preferBudget) add("sparsam")
        if (profile.preferVegetarian) add("vegetarisch")
        if (profile.preferLowCooking) add("wenig Kochen")
    }

    if (preferences.isNotEmpty()) {
        builder.appendLine("Vorlieben: ${preferences.joinToString(", ")}")
    }

    builder.appendLine()
    builder.appendLine("Einkaufsliste")
    builder.appendLine()

    val groupedItems = items.groupBy { item ->
        item.category
    }

    groupedItems.forEach { (category, categoryItems) ->
        builder.appendLine(category)

        categoryItems.forEach { item ->
            val checkedMarker = if (checkedItemKeys.contains(item.prepperKey())) {
                "[x]"
            } else {
                "[ ]"
            }

            builder.appendLine(
                "- $checkedMarker ${item.name}: ${formatPrepperShoppingQuantity(item)}"
            )
        }

        builder.appendLine()
    }

    return builder.toString().trim()
}