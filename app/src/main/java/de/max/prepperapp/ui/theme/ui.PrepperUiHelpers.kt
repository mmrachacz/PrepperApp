package de.max.prepperapp.ui

import de.max.prepperapp.domain.ShoppingListItem
import java.util.Locale

fun ShoppingListItem.prepperKey(): String {
    return id
}

fun formatPrepperShoppingQuantity(item: ShoppingListItem): String {
    val baseQuantity = "${formatPrepperQuantity(item.quantity)} ${item.unit}"
    val packageCount = item.packageCount()
    val packageSizeGrams = item.packageSizeGrams

    return if (packageCount != null && packageSizeGrams != null && packageCount > 0) {
        "$baseQuantity · $packageCount × ${formatPrepperPackageSize(packageSizeGrams)}"
    } else {
        baseQuantity
    }
}

fun formatPrepperPackageSize(packageSizeGrams: Int): String {
    return if (packageSizeGrams >= 1000 && packageSizeGrams % 1000 == 0) {
        "${packageSizeGrams / 1000} kg"
    } else {
        "$packageSizeGrams g"
    }
}

fun formatPrepperOneDecimal(value: Double): String {
    return String.format(Locale.GERMANY, "%.1f", value)
}

fun formatPrepperQuantity(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        String.format(Locale.GERMANY, "%.1f", value)
    }
}