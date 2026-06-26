package de.max.prepperapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrepperOverviewScreen(
    adults: Int,
    smallChildren: Int,
    bigChildren: Int,
    dayDiaperChildren: Int,
    nightDiaperChildren: Int,
    days: Int,
    hasDog: Boolean,
    checkedItemCount: Int,
    totalItemCount: Int,
    waterLiters: Int,
    pastaOrRiceKg: Double,
    tomatoSauceGlasses: Int,
    cannedMeals: Int,
    gasCartridges: Int,
    onOpenProfile: () -> Unit,
    onOpenList: () -> Unit
) {
    val progress = calculatePrepperProgress(
        checkedItemCount = checkedItemCount,
        totalItemCount = totalItemCount
    )

    val progressPercent = (progress * 100).toInt()

    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Haushaltsprofil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$days Tage Notvorrat",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "$adults Erwachsene · $smallChildren kleine Kinder · $bigChildren große Kinder · " +
                            if (hasDog) "1 Hund" else "kein Hund",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (dayDiaperChildren > 0 || nightDiaperChildren > 0) {
                    Text(
                        text = "$dayDiaperChildren mit Tageswindeln · $nightDiaperChildren mit Nachtwindeln",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Erste Vorratsberechnung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(text = "Wasser: ca. $waterLiters Liter")
                Text(text = "Nudeln/Reis/Haferflocken: ca. ${formatPrepperOneDecimal(pastaOrRiceKg)} kg")
                Text(text = "Tomatensoße: ca. $tomatoSauceGlasses Gläser")
                Text(text = "Konserven/Fertiggerichte: ca. $cannedMeals Stück")
                Text(text = "Gaskartuschen: ca. $gasCartridges Stück")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Fortschritt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Erledigt: $checkedItemCount von $totalItemCount Artikeln",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Fortschritt: $progressPercent %",
                    style = MaterialTheme.typography.bodyMedium
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onOpenProfile,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Profil ändern")
            }

            Button(
                onClick = onOpenList,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Liste öffnen")
            }
        }
    }
}

fun calculatePrepperProgress(
    checkedItemCount: Int,
    totalItemCount: Int
): Float {
    if (totalItemCount <= 0) {
        return 0f
    }

    return (checkedItemCount.toFloat() / totalItemCount.toFloat())
        .coerceIn(0f, 1f)
}