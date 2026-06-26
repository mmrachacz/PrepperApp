package de.max.prepperapp.ui

import android.content.Context
import androidx.annotation.StringRes
import de.max.prepperapp.R
import de.max.prepperapp.domain.ShoppingListItem

fun localizePrepperSupplyItems(
    context: Context,
    items: List<ShoppingListItem>
): List<ShoppingListItem> {
    return items.map { item ->
        item.copy(
            name = context.getString(itemNameResId(item)),
            category = context.getString(categoryResId(item.category)),
            unit = context.getString(unitResId(item.unit))
        )
    }
}

@StringRes
private fun itemNameResId(item: ShoppingListItem): Int {
    return when (item.id) {
        "water" -> R.string.supply_item_water
        "pasta" -> R.string.supply_item_pasta
        "rice" -> R.string.supply_item_rice
        "oats" -> R.string.supply_item_oats
        "instant_potatoes" -> R.string.supply_item_instant_potatoes
        "crispbread" -> R.string.supply_item_crispbread
        "tomato_sauce" -> R.string.supply_item_tomato_sauce
        "canned_meals" -> {
            if (item.name.startsWith("Vegetarische")) {
                R.string.supply_item_canned_meals_vegetarian
            } else {
                R.string.supply_item_canned_meals
            }
        }
        "ready_soups" -> R.string.supply_item_ready_soups
        "vegetable_cans" -> R.string.supply_item_vegetable_cans
        "fruit_cans" -> R.string.supply_item_fruit_cans
        "legume_cans" -> R.string.supply_item_legume_cans
        "fish_meat_cans" -> R.string.supply_item_fish_meat_cans
        "peanut_butter" -> R.string.supply_item_peanut_butter
        "long_life_milk" -> R.string.supply_item_long_life_milk
        "jam" -> R.string.supply_item_jam
        "energy_bars" -> R.string.supply_item_energy_bars
        "nuts_trail_mix" -> R.string.supply_item_nuts_trail_mix
        "small_child_fruit_pouches" -> R.string.supply_item_small_child_fruit_pouches
        "small_child_rusk" -> R.string.supply_item_small_child_rusk
        "big_child_bars" -> R.string.supply_item_big_child_bars
        "child_cookies" -> R.string.supply_item_child_cookies
        "chocolate" -> R.string.supply_item_chocolate
        "cocoa" -> R.string.supply_item_cocoa
        "salty_snacks" -> R.string.supply_item_salty_snacks
        "salt" -> R.string.supply_item_salt
        "broth" -> R.string.supply_item_broth
        "sugar" -> R.string.supply_item_sugar
        "oil" -> R.string.supply_item_oil
        "gas_cooker" -> R.string.supply_item_gas_cooker
        "can_opener" -> R.string.supply_item_can_opener
        "multitool" -> R.string.supply_item_multitool
        "gas_cartridges" -> R.string.supply_item_gas_cartridges
        "lighter_matches" -> R.string.supply_item_lighter_matches
        "battery_radio" -> R.string.supply_item_battery_radio
        "printed_contacts" -> R.string.supply_item_printed_contacts
        "phone_charging_cables" -> R.string.supply_item_phone_charging_cables
        "batteries_aa" -> R.string.supply_item_batteries_aa
        "batteries_aaa" -> R.string.supply_item_batteries_aaa
        "candles" -> R.string.supply_item_candles
        "flashlight" -> R.string.supply_item_flashlight
        "headlamp" -> R.string.supply_item_headlamp
        "power_bank" -> R.string.supply_item_power_bank
        "co_detector" -> R.string.supply_item_co_detector
        "toilet_paper" -> R.string.supply_item_toilet_paper
        "trash_bags" -> R.string.supply_item_trash_bags
        "kitchen_rolls" -> R.string.supply_item_kitchen_rolls
        "soap" -> R.string.supply_item_soap
        "household_gloves" -> R.string.supply_item_household_gloves
        "laundry_detergent" -> R.string.supply_item_laundry_detergent
        "wet_wipes" -> R.string.supply_item_wet_wipes
        "disinfectant" -> R.string.supply_item_disinfectant
        "day_diapers" -> R.string.supply_item_day_diapers
        "night_diapers" -> R.string.supply_item_night_diapers
        "diaper_trash_bags" -> R.string.supply_item_diaper_trash_bags
        "first_aid_kit" -> R.string.supply_item_first_aid_kit
        "painkillers" -> R.string.supply_item_painkillers
        "bandages" -> R.string.supply_item_bandages
        "wound_disinfectant" -> R.string.supply_item_wound_disinfectant
        "disposable_gloves" -> R.string.supply_item_disposable_gloves
        "thermometer" -> R.string.supply_item_thermometer
        "personal_medications" -> R.string.supply_item_personal_medications
        "child_fever_medicine" -> R.string.supply_item_child_fever_medicine
        "electrolytes" -> R.string.supply_item_electrolytes
        "emergency_backpacks" -> R.string.supply_item_emergency_backpacks
        "document_folder" -> R.string.supply_item_document_folder
        "document_copies" -> R.string.supply_item_document_copies
        "cash_reserve" -> R.string.supply_item_cash_reserve
        "emergency_food_to_go" -> R.string.supply_item_emergency_food_to_go
        "drinking_bottles" -> R.string.supply_item_drinking_bottles
        "blankets_sleeping_bags" -> R.string.supply_item_blankets_sleeping_bags
        "rain_ponchos" -> R.string.supply_item_rain_ponchos
        "clothing_sets" -> R.string.supply_item_clothing_sets
        "car_half_tank" -> R.string.supply_item_car_half_tank
        "fuel_canister_check" -> R.string.supply_item_fuel_canister_check
        "car_usb_adapter" -> R.string.supply_item_car_usb_adapter
        "warning_vests" -> R.string.supply_item_warning_vests
        "road_map_offline_maps" -> R.string.supply_item_road_map_offline_maps
        "books_puzzle_books" -> R.string.supply_item_books_puzzle_books
        "card_game" -> R.string.supply_item_card_game
        "child_activity_sets" -> R.string.supply_item_child_activity_sets
        "pencils" -> R.string.supply_item_pencils
        "comfort_items" -> R.string.supply_item_comfort_items
        "dog_food" -> R.string.supply_item_dog_food
        "dog_waste_bags" -> R.string.supply_item_dog_waste_bags
        "dog_treats" -> R.string.supply_item_dog_treats
        "dog_travel_bowl" -> R.string.supply_item_dog_travel_bowl
        "dog_spare_leash" -> R.string.supply_item_dog_spare_leash
        "dog_documents" -> R.string.supply_item_dog_documents
        else -> R.string.supply_item_water
    }
}

@StringRes
private fun categoryResId(category: String): Int {
    return when (category) {
        "Wasser" -> R.string.supply_category_water
        "Grundnahrungsmittel" -> R.string.supply_category_staples
        "Mahlzeiten" -> R.string.supply_category_meals
        "Gemüse & Obst" -> R.string.supply_category_vegetables_fruit
        "Proteine" -> R.string.supply_category_proteins
        "Frühstück & Snacks" -> R.string.supply_category_breakfast_snacks
        "Kinder" -> R.string.supply_category_children
        "Kochen & Basics" -> R.string.supply_category_cooking_basics
        "Kochen & Werkzeuge" -> R.string.supply_category_cooking_tools
        "Kochen & Energie" -> R.string.supply_category_cooking_energy
        "Information & Kommunikation" -> R.string.supply_category_info_communication
        "Licht & Energie" -> R.string.supply_category_light_energy
        "Sicherheit" -> R.string.supply_category_safety
        "Hygiene" -> R.string.supply_category_hygiene
        "Windeln & Pflege" -> R.string.supply_category_diapers_care
        "Erste Hilfe" -> R.string.supply_category_first_aid
        "Notgepäck & Evakuierung" -> R.string.supply_category_emergency_bag
        "Fahrzeug & Evakuierung" -> R.string.supply_category_vehicle_evacuation
        "Beschäftigung" -> R.string.supply_category_occupation
        "Hund" -> R.string.supply_category_dog
        else -> R.string.supply_category_staples
    }
}

@StringRes
private fun unitResId(unit: String): Int {
    return when (unit) {
        "Liter" -> R.string.supply_unit_liter
        "kg" -> R.string.supply_unit_kg
        "Packungen" -> R.string.supply_unit_packs
        "Gläser" -> R.string.supply_unit_jars
        "Stück" -> R.string.supply_unit_pieces
        "Dosen" -> R.string.supply_unit_cans
        "Tafeln/Riegel" -> R.string.supply_unit_bars
        "Packung" -> R.string.supply_unit_pack
        "Liste" -> R.string.supply_unit_list
        "Rollen" -> R.string.supply_unit_rolls
        "Flaschen" -> R.string.supply_unit_bottles
        "Flasche" -> R.string.supply_unit_bottle
        "Vorrat" -> R.string.supply_unit_supply
        "Satz" -> R.string.supply_unit_set
        "Tagesportionen" -> R.string.supply_unit_daily_portions
        "Sätze" -> R.string.supply_unit_sets
        "Aufgabe" -> R.string.supply_unit_task
        "optional" -> R.string.supply_unit_optional
        "Check" -> R.string.supply_unit_check
        else -> R.string.supply_unit_pieces
    }
}