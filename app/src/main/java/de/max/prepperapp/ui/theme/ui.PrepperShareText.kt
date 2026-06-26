package de.max.prepperapp.ui

import android.content.Context
import de.max.prepperapp.R
import de.max.prepperapp.domain.HouseholdProfile
import de.max.prepperapp.domain.ShoppingListItem

fun buildPrepperShareText(
    context: Context,
    profile: HouseholdProfile,
    items: List<ShoppingListItem>,
    checkedItemKeys: Set<String>
): String {
    val builder = StringBuilder()

    val dogText = if (profile.hasDog) {
        context.getString(R.string.share_dog_yes)
    } else {
        context.getString(R.string.share_dog_no)
    }

    builder.appendLine(context.getString(R.string.share_title))

    builder.appendLine(
        context.getString(
            R.string.share_profile_line,
            profile.days,
            profile.adults,
            profile.smallChildren,
            profile.bigChildren,
            dogText
        )
    )

    if (profile.dayDiaperChildren > 0 || profile.nightDiaperChildren > 0) {
        builder.appendLine(
            context.getString(
                R.string.share_diaper_line,
                profile.dayDiaperChildren,
                profile.nightDiaperChildren
            )
        )
    }

    val preferences = buildList {
        if (profile.preferBudget) {
            add(context.getString(R.string.share_preference_budget))
        }

        if (profile.preferVegetarian) {
            add(context.getString(R.string.share_preference_vegetarian))
        }

        if (profile.preferLowCooking) {
            add(context.getString(R.string.share_preference_low_cooking))
        }
    }

    if (preferences.isNotEmpty()) {
        builder.appendLine(
            context.getString(
                R.string.share_preferences_label,
                preferences.joinToString(", ")
            )
        )
    }

    builder.appendLine()
    builder.appendLine(context.getString(R.string.share_list_title))
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