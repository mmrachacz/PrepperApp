package de.max.prepperapp.domain

import kotlin.math.ceil

data class HouseholdProfile(
    val adults: Int,
    val smallChildren: Int,
    val bigChildren: Int,
    val dayDiaperChildren: Int,
    val nightDiaperChildren: Int,
    val days: Int,
    val hasDog: Boolean,
    val preferBudget: Boolean = true,
    val preferVegetarian: Boolean = false,
    val preferLowCooking: Boolean = false
) {
    val children: Int
        get() = smallChildren + bigChildren

    val humans: Int
        get() = adults + children
}

data class ShoppingListItem(
    val id: String,
    val name: String,
    val category: String,
    val quantity: Double,
    val unit: String,
    val isRequired: Boolean = true,
    val packageSizeGrams: Int? = null
) {
    fun packageCount(): Int? {
        val sizeInGrams = packageSizeGrams ?: return null

        if (quantity <= 0.0) return 0
        if (sizeInGrams <= 0) return null
        if (unit != "kg") return null

        return ceil(quantity * 1000.0 / sizeInGrams).toInt()
    }
}

data class SupplyCalculation(
    val waterLiters: Int,
    val pastaOrRiceKg: Double,
    val tomatoSauceGlasses: Int,
    val cannedMeals: Int,
    val gasCartridges: Int,
    val shoppingList: List<ShoppingListItem>
)

object SupplyCalculator {

    fun calculate(profile: HouseholdProfile): SupplyCalculation {
        val safeAdults = profile.adults.coerceAtLeast(1)
        val safeSmallChildren = profile.smallChildren.coerceAtLeast(0)
        val safeBigChildren = profile.bigChildren.coerceAtLeast(0)
        val safeChildren = safeSmallChildren + safeBigChildren
        val safeDays = profile.days.coerceAtLeast(1)
        val hasDog = profile.hasDog

        val preferBudget = profile.preferBudget
        val preferVegetarian = profile.preferVegetarian
        val preferLowCooking = profile.preferLowCooking

        val safeDayDiaperChildren = profile.dayDiaperChildren
            .coerceIn(0, safeChildren)

        val safeNightDiaperChildren = profile.nightDiaperChildren
            .coerceIn(0, (safeChildren - safeDayDiaperChildren).coerceAtLeast(0))

        val humans = safeAdults + safeChildren

        val foodUnits =
            safeAdults * 1.0 +
                    safeBigChildren * 1.0 +
                    safeSmallChildren * 0.75

        val waterForHumansLiters = ceil(humans * safeDays * 2.0).toInt()
        val waterForDogLiters = if (hasDog) {
            ceil(safeDays * 2.0).toInt()
        } else {
            0
        }

        val totalWaterLiters = waterForHumansLiters + waterForDogLiters

        val stapleMultiplier = when {
            preferLowCooking -> 0.85
            preferBudget -> 1.12
            else -> 1.0
        }

        val pastaKg = roundUpOneDecimal(foodUnits * safeDays / 10.0 * 0.55 * stapleMultiplier)
        val riceKg = roundUpOneDecimal(foodUnits * safeDays / 10.0 * 0.45 * stapleMultiplier)
        val oatsKg = roundUpOneDecimal(foodUnits * safeDays / 10.0 * 0.55 * stapleMultiplier)
        val instantPotatoKg = roundUpOneDecimal(foodUnits * safeDays / 10.0 * 0.35 * stapleMultiplier)

        val pastaOrRiceKg = pastaKg + riceKg + oatsKg + instantPotatoKg

        val crispbreadPacks = ceilToInt(foodUnits * safeDays / 10.0 * 2.0)

        val tomatoSauceGlasses = ceilToInt(foodUnits * safeDays / 10.0 * 3.0)

        val cannedMealsPerTenDays = when {
            preferLowCooking -> 7.0
            preferBudget -> 2.0
            else -> 3.0
        }

        val cannedMeals = ceilToInt(foodUnits * safeDays / 10.0 * cannedMealsPerTenDays)

        val readySoupCans = if (preferLowCooking) {
            ceilToInt(foodUnits * safeDays / 10.0 * 4.0)
        } else {
            0
        }

        val vegetableCans = if (preferVegetarian) {
            ceilToInt(foodUnits * safeDays / 10.0 * 8.0)
        } else {
            ceilToInt(foodUnits * safeDays / 10.0 * 7.0)
        }

        val fruitCans = ceilToInt(foodUnits * safeDays / 10.0 * 4.0)

        val legumeCans = if (preferVegetarian) {
            ceilToInt(foodUnits * safeDays / 10.0 * 5.0)
        } else {
            ceilToInt(foodUnits * safeDays / 10.0 * 3.0)
        }

        val fishOrMeatCans = if (preferVegetarian) {
            0
        } else {
            ceilToInt(foodUnits * safeDays / 10.0 * 4.0)
        }

        val longLifeMilkLiters = ceilToInt(foodUnits * safeDays / 10.0 * 2.0)
        val peanutButterGlasses = ceilToInt(foodUnits * safeDays / 10.0 * 0.8).coerceAtLeast(1)
        val jamGlasses = ceilToInt(foodUnits * safeDays / 10.0 * 0.6).coerceAtLeast(1)

        val energyBars = if (preferLowCooking) {
            ceilToInt(foodUnits * safeDays / 10.0 * 5.0)
        } else {
            0
        }

        val nutsPacks = if (!preferBudget) {
            ceilToInt(foodUnits * safeDays / 10.0 * 2.0)
        } else {
            0
        }

        val smallChildFruitPouches = ceilToInt(safeSmallChildren * safeDays * 0.75)
        val smallChildRuskPacks = if (safeSmallChildren > 0) {
            ceilToInt(safeSmallChildren * safeDays / 10.0).coerceAtLeast(1)
        } else {
            0
        }

        val bigChildBars = ceilToInt(safeBigChildren * safeDays * 0.5)
        val childCookiePacks = if (safeChildren > 0) {
            ceilToInt(safeChildren * safeDays / 10.0).coerceAtLeast(1)
        } else {
            0
        }

        val chocolateBars = if (safeChildren > 0) {
            ceilToInt(safeChildren * safeDays / 7.0).coerceAtLeast(1)
        } else {
            0
        }

        val saltySnackPacks = if (safeChildren > 0 && !preferBudget) {
            ceilToInt(safeChildren * safeDays / 10.0).coerceAtLeast(1)
        } else {
            0
        }

        val cocoaPacks = if (safeChildren > 0) {
            ceilToInt(safeChildren * safeDays / 45.0).coerceAtLeast(1)
        } else {
            0
        }

        val saltPacks = 1
        val brothPacks = ceilToInt(foodUnits * safeDays / 30.0).coerceAtLeast(1)
        val sugarKg = roundUpOneDecimal(foodUnits * safeDays / 10.0 * 0.25)
        val oilLiters = ceilToInt(foodUnits * safeDays / 10.0 * 0.25)

        val toiletPaperRolls = ceilToInt(humans * safeDays / 5.0)
        val trashBags = ceilToInt(safeDays / 1.5)
        val soapBars = ceilToInt(humans * safeDays / 25.0).coerceAtLeast(1)
        val disinfectantBottles = if (safeDays <= 14) 1 else 2
        val kitchenRolls = ceilToInt(safeDays / 7.0).coerceAtLeast(1)
        val householdGlovesPacks = 1
        val laundryDetergentPacks = if (safeDays >= 14) 1 else 0

        val dayDiapers = safeDayDiaperChildren * safeDays * 5
        val nightDiapers = safeNightDiaperChildren * safeDays
        val totalDiapers = dayDiapers + nightDiapers

        val wetWipesPacks = when {
            totalDiapers > 0 -> ceilToInt(totalDiapers / 50.0).coerceAtLeast(1)
            safeSmallChildren > 0 -> ceilToInt(safeSmallChildren * safeDays / 10.0).coerceAtLeast(1)
            else -> 0
        }

        val diaperTrashBags = if (totalDiapers > 0) {
            ceilToInt(totalDiapers / 20.0).coerceAtLeast(1)
        } else {
            0
        }

        val gasCartridges = when {
            preferLowCooking && safeDays <= 7 -> 2
            preferLowCooking && safeDays <= 14 -> 3
            preferLowCooking && safeDays <= 30 -> 6
            safeDays <= 7 -> 3
            safeDays <= 14 -> 5
            safeDays <= 30 -> 10
            else -> ceilToInt(safeDays / 30.0) * 10
        }

        val gasCookerCount = 1
        val canOpenerCount = 1
        val multitoolCount = 1
        val lighterCount = 3
        val batteriesAaPacks = if (safeDays <= 14) 1 else 2
        val batteriesAaaPacks = if (safeDays <= 14) 1 else 2
        val candles = if (safeDays <= 14) 12 else 24
        val flashlightCount = if (humans <= 2) 1 else 2
        val headlampCount = if (humans >= 3) 1 else 0
        val powerBankCount = if (humans <= 2) 1 else 2
        val batteryOrCrankRadioCount = 1
        val coDetectorCount = 1

        val firstAidKitCount = 1
        val painkillerPacks = 1
        val bandagePacks = 1
        val woundDisinfectantBottles = 1
        val disposableGlovesPacks = 1
        val thermometerCount = 1
        val personalMedicationSupply = 1
        val childFeverMedicinePacks = if (safeChildren > 0) 1 else 0
        val electrolytesPacks = if (safeChildren > 0) 1 else 0

        val emergencyBackpacks = humans
        val documentFolderCount = 1
        val cashReserveCount = 1
        val documentCopiesCount = 1
        val printedContactListCount = 1
        val emergencyFoodToGoPortions = humans * 2
        val drinkingBottles = humans
        val blanketsOrSleepingBags = humans
        val rainPonchos = humans
        val clothingSets = humans

        val carHalfTankTask = 1
        val fuelCanisterCheck = 1
        val carUsbAdapterCount = 1
        val warningVests = humans.coerceAtLeast(2)
        val roadMapOrOfflineMapCount = 1

        val booksOrPuzzleBooks = (safeAdults + safeBigChildren).coerceAtLeast(1)
        val cardGameCount = 1
        val childActivitySets = safeChildren
        val pencilsPacks = if (safeChildren > 0) 1 else 0
        val comfortItems = if (safeChildren > 0) safeChildren else 0

        val dogFoodKg = if (hasDog) {
            roundUpOneDecimal(safeDays * 0.5)
        } else {
            0.0
        }

        val dogWasteBags = if (hasDog) safeDays * 3 else 0
        val dogTreatPacks = if (hasDog) ceilToInt(safeDays / 14.0) else 0
        val dogTravelBowlCount = if (hasDog) 1 else 0
        val dogLeashSpareCount = if (hasDog) 1 else 0
        val dogDocumentsCheck = if (hasDog) 1 else 0

        val shoppingList = buildList {
            addShoppingItem("water", "Trinkwasser", "Wasser", totalWaterLiters.toDouble(), "Liter")

            addShoppingItem("pasta", "Nudeln", "Grundnahrungsmittel", pastaKg, "kg", packageSizeGrams = 500)
            addShoppingItem("rice", "Reis", "Grundnahrungsmittel", riceKg, "kg", packageSizeGrams = 1000)
            addShoppingItem("oats", "Haferflocken", "Grundnahrungsmittel", oatsKg, "kg", packageSizeGrams = 500)
            addShoppingItem("instant_potatoes", "Kartoffelpüree-Pulver", "Grundnahrungsmittel", instantPotatoKg, "kg", packageSizeGrams = 500)
            addShoppingItem("crispbread", "Knäckebrot/Zwieback", "Grundnahrungsmittel", crispbreadPacks.toDouble(), "Packungen")

            addShoppingItem("tomato_sauce", "Tomatensoße", "Mahlzeiten", tomatoSauceGlasses.toDouble(), "Gläser")
            addShoppingItem(
                id = "canned_meals",
                name = if (preferVegetarian) "Vegetarische Konserven/Fertiggerichte" else "Konserven/Fertiggerichte",
                category = "Mahlzeiten",
                quantity = cannedMeals.toDouble(),
                unit = "Stück"
            )

            if (readySoupCans > 0) {
                addShoppingItem("ready_soups", "Fertigsuppen/Eintöpfe", "Mahlzeiten", readySoupCans.toDouble(), "Dosen")
            }

            addShoppingItem("vegetable_cans", "Gemüsekonserven", "Gemüse & Obst", vegetableCans.toDouble(), "Dosen")
            addShoppingItem("fruit_cans", "Obstkonserven", "Gemüse & Obst", fruitCans.toDouble(), "Dosen")

            addShoppingItem("legume_cans", "Bohnen/Linsen/Kichererbsen", "Proteine", legumeCans.toDouble(), "Dosen")

            if (fishOrMeatCans > 0) {
                addShoppingItem("fish_meat_cans", "Fisch-/Fleischkonserven", "Proteine", fishOrMeatCans.toDouble(), "Dosen")
            }

            addShoppingItem("peanut_butter", "Erdnussbutter", "Proteine", peanutButterGlasses.toDouble(), "Gläser")
            addShoppingItem("long_life_milk", "H-Milch", "Frühstück & Snacks", longLifeMilkLiters.toDouble(), "Liter")
            addShoppingItem("jam", "Marmelade/Honig", "Frühstück & Snacks", jamGlasses.toDouble(), "Gläser")

            if (energyBars > 0) {
                addShoppingItem("energy_bars", "Müsli-/Energieriegel", "Frühstück & Snacks", energyBars.toDouble(), "Stück")
            }

            if (nutsPacks > 0) {
                addShoppingItem("nuts_trail_mix", "Nüsse/Studentenfutter", "Frühstück & Snacks", nutsPacks.toDouble(), "Packungen")
            }

            if (safeSmallChildren > 0) {
                addShoppingItem("small_child_fruit_pouches", "Fruchtmus/Quetschies", "Kinder", smallChildFruitPouches.toDouble(), "Stück")
                addShoppingItem("small_child_rusk", "Zwieback/Babykekse", "Kinder", smallChildRuskPacks.toDouble(), "Packungen")
            }

            if (safeBigChildren > 0) {
                addShoppingItem("big_child_bars", "Müsli-/Kinderriegel", "Kinder", bigChildBars.toDouble(), "Stück")
            }

            if (safeChildren > 0) {
                addShoppingItem("child_cookies", "Kekse", "Kinder", childCookiePacks.toDouble(), "Packungen")
                addShoppingItem("chocolate", "Schokolade", "Kinder", chocolateBars.toDouble(), "Tafeln/Riegel")
                addShoppingItem("cocoa", "Kakao", "Kinder", cocoaPacks.toDouble(), "Packungen")
            }

            if (saltySnackPacks > 0) {
                addShoppingItem("salty_snacks", "Salzbrezeln/Chips", "Kinder", saltySnackPacks.toDouble(), "Packungen")
            }

            addShoppingItem("salt", "Salz", "Kochen & Basics", saltPacks.toDouble(), "Packung")
            addShoppingItem("broth", "Brühe/Gemüsebrühe", "Kochen & Basics", brothPacks.toDouble(), "Packungen")
            addShoppingItem("sugar", "Zucker", "Kochen & Basics", sugarKg, "kg", packageSizeGrams = 1000)
            addShoppingItem("oil", "Speiseöl", "Kochen & Basics", oilLiters.toDouble(), "Liter")

            addShoppingItem("gas_cooker", "Gaskocher/Campingkocher", "Kochen & Werkzeuge", gasCookerCount.toDouble(), "Stück")
            addShoppingItem("can_opener", "Dosenöffner", "Kochen & Werkzeuge", canOpenerCount.toDouble(), "Stück")
            addShoppingItem("multitool", "Multitool/Taschenmesser", "Kochen & Werkzeuge", multitoolCount.toDouble(), "Stück")

            addShoppingItem("gas_cartridges", "Gaskartuschen", "Kochen & Energie", gasCartridges.toDouble(), "Stück")
            addShoppingItem("lighter_matches", "Feuerzeug/Streichhölzer", "Kochen & Energie", lighterCount.toDouble(), "Stück")

            addShoppingItem("battery_radio", "Batterie-/Kurbelradio", "Information & Kommunikation", batteryOrCrankRadioCount.toDouble(), "Stück")
            addShoppingItem("printed_contacts", "Wichtige Telefonnummern ausgedruckt", "Information & Kommunikation", printedContactListCount.toDouble(), "Liste")
            addShoppingItem("phone_charging_cables", "Ladekabel/USB-Kabel", "Information & Kommunikation", 2.0, "Stück")

            addShoppingItem("batteries_aa", "AA-Batterien", "Licht & Energie", batteriesAaPacks.toDouble(), "Packungen")
            addShoppingItem("batteries_aaa", "AAA-Batterien", "Licht & Energie", batteriesAaaPacks.toDouble(), "Packungen")
            addShoppingItem("candles", "Kerzen", "Licht & Energie", candles.toDouble(), "Stück")
            addShoppingItem("flashlight", "Taschenlampen", "Licht & Energie", flashlightCount.toDouble(), "Stück")

            if (headlampCount > 0) {
                addShoppingItem("headlamp", "Stirnlampe", "Licht & Energie", headlampCount.toDouble(), "Stück")
            }

            addShoppingItem("power_bank", "Powerbanks", "Licht & Energie", powerBankCount.toDouble(), "Stück")
            addShoppingItem("co_detector", "CO-Warnmelder bei Gas-/Campingkocher-Nutzung", "Sicherheit", coDetectorCount.toDouble(), "Stück")

            addShoppingItem("toilet_paper", "Klopapier", "Hygiene", toiletPaperRolls.toDouble(), "Rollen")
            addShoppingItem("trash_bags", "Müllbeutel", "Hygiene", trashBags.toDouble(), "Stück")
            addShoppingItem("kitchen_rolls", "Küchenrolle", "Hygiene", kitchenRolls.toDouble(), "Rollen")
            addShoppingItem("soap", "Seife", "Hygiene", soapBars.toDouble(), "Stück")
            addShoppingItem("household_gloves", "Haushalts-/Putzhandschuhe", "Hygiene", householdGlovesPacks.toDouble(), "Packung")

            if (laundryDetergentPacks > 0) {
                addShoppingItem("laundry_detergent", "Waschmittel klein/Handwaschmittel", "Hygiene", laundryDetergentPacks.toDouble(), "Packung")
            }

            if (wetWipesPacks > 0) {
                addShoppingItem("wet_wipes", "Feuchttücher", "Hygiene", wetWipesPacks.toDouble(), "Packungen")
            }

            addShoppingItem("disinfectant", "Desinfektionsmittel", "Hygiene", disinfectantBottles.toDouble(), "Flaschen")

            if (dayDiapers > 0) {
                addShoppingItem("day_diapers", "Windeln Tagesbedarf", "Windeln & Pflege", dayDiapers.toDouble(), "Stück")
            }

            if (nightDiapers > 0) {
                addShoppingItem("night_diapers", "Nachtwindeln", "Windeln & Pflege", nightDiapers.toDouble(), "Stück")
            }

            if (diaperTrashBags > 0) {
                addShoppingItem("diaper_trash_bags", "Windel-Müllbeutel", "Windeln & Pflege", diaperTrashBags.toDouble(), "Packungen")
            }

            addShoppingItem("first_aid_kit", "Erste-Hilfe-Set", "Erste Hilfe", firstAidKitCount.toDouble(), "Stück")
            addShoppingItem("painkillers", "Schmerzmittel", "Erste Hilfe", painkillerPacks.toDouble(), "Packung")
            addShoppingItem("bandages", "Pflaster/Verbandsmaterial", "Erste Hilfe", bandagePacks.toDouble(), "Packung")
            addShoppingItem("wound_disinfectant", "Wunddesinfektion", "Erste Hilfe", woundDisinfectantBottles.toDouble(), "Flasche")
            addShoppingItem("disposable_gloves", "Einmalhandschuhe", "Erste Hilfe", disposableGlovesPacks.toDouble(), "Packung")
            addShoppingItem("thermometer", "Fieberthermometer", "Erste Hilfe", thermometerCount.toDouble(), "Stück")
            addShoppingItem("personal_medications", "Persönliche Medikamente prüfen", "Erste Hilfe", personalMedicationSupply.toDouble(), "Vorrat")

            if (childFeverMedicinePacks > 0) {
                addShoppingItem("child_fever_medicine", "Kinder-Fieber-/Schmerzmittel", "Erste Hilfe", childFeverMedicinePacks.toDouble(), "Packung")
            }

            if (electrolytesPacks > 0) {
                addShoppingItem("electrolytes", "Elektrolytlösung/Oralpädon", "Erste Hilfe", electrolytesPacks.toDouble(), "Packung")
            }

            addShoppingItem("emergency_backpacks", "Notfallrucksäcke", "Notgepäck & Evakuierung", emergencyBackpacks.toDouble(), "Stück")
            addShoppingItem("document_folder", "Dokumentenmappe", "Notgepäck & Evakuierung", documentFolderCount.toDouble(), "Stück")
            addShoppingItem("document_copies", "Kopien wichtiger Dokumente", "Notgepäck & Evakuierung", documentCopiesCount.toDouble(), "Satz")
            addShoppingItem("cash_reserve", "Bargeld in kleinen Scheinen", "Notgepäck & Evakuierung", cashReserveCount.toDouble(), "Vorrat")
            addShoppingItem("emergency_food_to_go", "Verpflegung für unterwegs", "Notgepäck & Evakuierung", emergencyFoodToGoPortions.toDouble(), "Tagesportionen")
            addShoppingItem("drinking_bottles", "Trinkflaschen für unterwegs", "Notgepäck & Evakuierung", drinkingBottles.toDouble(), "Stück")
            addShoppingItem("blankets_sleeping_bags", "Decken/Schlafsäcke", "Notgepäck & Evakuierung", blanketsOrSleepingBags.toDouble(), "Stück")
            addShoppingItem("rain_ponchos", "Regenschutz/Ponchos", "Notgepäck & Evakuierung", rainPonchos.toDouble(), "Stück")
            addShoppingItem("clothing_sets", "Wechselkleidung", "Notgepäck & Evakuierung", clothingSets.toDouble(), "Sätze")

            addShoppingItem("car_half_tank", "Auto mindestens halbvoll halten", "Fahrzeug & Evakuierung", carHalfTankTask.toDouble(), "Aufgabe")
            addShoppingItem("fuel_canister_check", "Zugelassenen Reservekanister prüfen", "Fahrzeug & Evakuierung", fuelCanisterCheck.toDouble(), "optional")
            addShoppingItem("car_usb_adapter", "12-V-USB-Ladeadapter fürs Auto", "Fahrzeug & Evakuierung", carUsbAdapterCount.toDouble(), "Stück")
            addShoppingItem("warning_vests", "Warnwesten für Familie", "Fahrzeug & Evakuierung", warningVests.toDouble(), "Stück")
            addShoppingItem("road_map_offline_maps", "Straßenkarte/Offline-Karten vorbereiten", "Fahrzeug & Evakuierung", roadMapOrOfflineMapCount.toDouble(), "Check")

            addShoppingItem("books_puzzle_books", "Bücher/Kreuzworträtsel/Rätselhefte", "Beschäftigung", booksOrPuzzleBooks.toDouble(), "Stück")
            addShoppingItem("card_game", "Kartenspiel/kleines Reisespiel", "Beschäftigung", cardGameCount.toDouble(), "Stück")

            if (childActivitySets > 0) {
                addShoppingItem("child_activity_sets", "Kinderbücher/kleine Spiele", "Beschäftigung", childActivitySets.toDouble(), "Stück")
                addShoppingItem("pencils", "Stifte/Buntstifte", "Beschäftigung", pencilsPacks.toDouble(), "Packung")
                addShoppingItem("comfort_items", "Kuscheltier/Tröster je Kind einplanen", "Beschäftigung", comfortItems.toDouble(), "Stück")
            }

            if (hasDog) {
                addShoppingItem("dog_food", "Hundefutter", "Hund", dogFoodKg, "kg")
                addShoppingItem("dog_waste_bags", "Kotbeutel", "Hund", dogWasteBags.toDouble(), "Stück")
                addShoppingItem("dog_treats", "Leckerlis", "Hund", dogTreatPacks.toDouble(), "Packungen")
                addShoppingItem("dog_travel_bowl", "Faltbarer Hunde-Reisenapf", "Hund", dogTravelBowlCount.toDouble(), "Stück")
                addShoppingItem("dog_spare_leash", "Ersatzleine", "Hund", dogLeashSpareCount.toDouble(), "Stück")
                addShoppingItem("dog_documents", "Impfausweis/Unterlagen Hund prüfen", "Hund", dogDocumentsCheck.toDouble(), "Check")
            }
        }

        return SupplyCalculation(
            waterLiters = totalWaterLiters,
            pastaOrRiceKg = pastaOrRiceKg,
            tomatoSauceGlasses = tomatoSauceGlasses,
            cannedMeals = cannedMeals,
            gasCartridges = gasCartridges,
            shoppingList = shoppingList
        )
    }

    private fun MutableList<ShoppingListItem>.addShoppingItem(
        id: String,
        name: String,
        category: String,
        quantity: Double,
        unit: String,
        isRequired: Boolean = true,
        packageSizeGrams: Int? = null
    ) {
        if (quantity <= 0.0) return

        add(
            ShoppingListItem(
                id = id,
                name = name,
                category = category,
                quantity = quantity,
                unit = unit,
                isRequired = isRequired,
                packageSizeGrams = packageSizeGrams
            )
        )
    }

    private fun ceilToInt(value: Double): Int {
        return ceil(value).toInt()
    }

    private fun roundUpOneDecimal(value: Double): Double {
        return ceil(value * 10.0) / 10.0
    }
}