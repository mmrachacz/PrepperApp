package de.max.prepperapp.data

import android.content.Context
import android.content.SharedPreferences
import de.max.prepperapp.domain.ShoppingListItem
import org.json.JSONArray
import org.json.JSONObject

class PrepperStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun loadAdults(): Int {
        return prefs.getInt(PREF_ADULTS, 2)
    }

    fun loadSmallChildren(): Int {
        return prefs.getInt(PREF_SMALL_CHILDREN, 1)
    }

    fun loadBigChildren(): Int {
        return prefs.getInt(PREF_BIG_CHILDREN, 1)
    }

    fun loadNeedsDiapers(): Boolean {
        if (prefs.contains(PREF_NEEDS_DIAPERS)) {
            return prefs.getBoolean(PREF_NEEDS_DIAPERS, false)
        }

        val existingDayDiaperChildren = prefs.getInt(PREF_DAY_DIAPER_CHILDREN, 0)
        val existingNightDiaperChildren = prefs.getInt(PREF_NIGHT_DIAPER_CHILDREN, 0)

        return existingDayDiaperChildren > 0 || existingNightDiaperChildren > 0
    }

    fun loadDayDiaperChildren(): Int {
        return prefs.getInt(PREF_DAY_DIAPER_CHILDREN, 0)
    }

    fun loadNightDiaperChildren(): Int {
        return prefs.getInt(PREF_NIGHT_DIAPER_CHILDREN, 0)
    }

    fun loadDays(): Int {
        return prefs.getInt(PREF_DAYS, 30)
    }

    fun loadHasDog(): Boolean {
        return prefs.getBoolean(PREF_HAS_DOG, true)
    }

    fun loadPreferBudget(): Boolean {
        return prefs.getBoolean(PREF_PREFER_BUDGET, true)
    }

    fun loadPreferVegetarian(): Boolean {
        return prefs.getBoolean(PREF_PREFER_VEGETARIAN, false)
    }

    fun loadPreferLowCooking(): Boolean {
        return prefs.getBoolean(PREF_PREFER_LOW_COOKING, false)
    }

    fun loadUseDarkTheme(): Boolean {
        return prefs.getBoolean(PREF_USE_DARK_THEME, false)
    }

    fun loadCheckedItemKeys(): Set<String> {
        return loadStringSet(PREF_CHECKED_KEYS)
    }

    fun loadHiddenItemKeys(): Set<String> {
        return loadStringSet(PREF_HIDDEN_KEYS)
    }

    fun loadCustomItems(): List<ShoppingListItem> {
        val rawValue = prefs.getString(PREF_CUSTOM_ITEMS, null) ?: return emptyList()

        return try {
            val jsonArray = JSONArray(rawValue)

            buildList {
                for (index in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(index)

                    add(
                        ShoppingListItem(
                            id = jsonObject.optString("id"),
                            name = jsonObject.optString("name"),
                            category = jsonObject.optString("category", "Eigene Artikel"),
                            quantity = jsonObject.optDouble("quantity", 1.0),
                            unit = jsonObject.optString("unit", "Stück")
                        )
                    )
                }
            }.filter { item ->
                item.id.isNotBlank() && item.name.isNotBlank()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveState(
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
        checkedItemKeys: Set<String>,
        hiddenItemKeys: Set<String>,
        customItems: List<ShoppingListItem>
    ) {
        prefs.edit()
            .putInt(PREF_ADULTS, adults)
            .putInt(PREF_SMALL_CHILDREN, smallChildren)
            .putInt(PREF_BIG_CHILDREN, bigChildren)
            .putBoolean(PREF_NEEDS_DIAPERS, needsDiapers)
            .putInt(PREF_DAY_DIAPER_CHILDREN, dayDiaperChildren)
            .putInt(PREF_NIGHT_DIAPER_CHILDREN, nightDiaperChildren)
            .putInt(PREF_DAYS, days)
            .putBoolean(PREF_HAS_DOG, hasDog)
            .putBoolean(PREF_PREFER_BUDGET, preferBudget)
            .putBoolean(PREF_PREFER_VEGETARIAN, preferVegetarian)
            .putBoolean(PREF_PREFER_LOW_COOKING, preferLowCooking)
            .putBoolean(PREF_USE_DARK_THEME, useDarkTheme)
            .putString(PREF_CHECKED_KEYS, checkedItemKeys.toStoredString())
            .putString(PREF_HIDDEN_KEYS, hiddenItemKeys.toStoredString())
            .putString(PREF_CUSTOM_ITEMS, customItems.toJsonString())
            .apply()
    }

    private fun loadStringSet(key: String): Set<String> {
        val rawValue = prefs.getString(key, null) ?: return emptySet()

        return rawValue
            .split(KEY_SEPARATOR)
            .filter { it.isNotBlank() }
            .toSet()
    }

    private fun Set<String>.toStoredString(): String {
        return sorted().joinToString(separator = KEY_SEPARATOR)
    }

    private fun List<ShoppingListItem>.toJsonString(): String {
        val jsonArray = JSONArray()

        forEach { item ->
            val jsonObject = JSONObject()
                .put("id", item.id)
                .put("name", item.name)
                .put("category", item.category)
                .put("quantity", item.quantity)
                .put("unit", item.unit)

            jsonArray.put(jsonObject)
        }

        return jsonArray.toString()
    }

    private companion object {
        const val PREFS_NAME = "prepper_app_prefs"

        const val PREF_ADULTS = "adults"
        const val PREF_SMALL_CHILDREN = "small_children"
        const val PREF_BIG_CHILDREN = "big_children"
        const val PREF_NEEDS_DIAPERS = "needs_diapers"
        const val PREF_DAY_DIAPER_CHILDREN = "day_diaper_children"
        const val PREF_NIGHT_DIAPER_CHILDREN = "night_diaper_children"
        const val PREF_DAYS = "days"
        const val PREF_HAS_DOG = "has_dog"

        const val PREF_PREFER_BUDGET = "prefer_budget"
        const val PREF_PREFER_VEGETARIAN = "prefer_vegetarian"
        const val PREF_PREFER_LOW_COOKING = "prefer_low_cooking"
        const val PREF_USE_DARK_THEME = "use_dark_theme"

        const val PREF_CHECKED_KEYS = "checked_keys"
        const val PREF_HIDDEN_KEYS = "hidden_keys"
        const val PREF_CUSTOM_ITEMS = "custom_items"

        const val KEY_SEPARATOR = ";;"
    }
}