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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.max.prepperapp.R

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

    val dogText = if (hasDog) {
        stringResource(id = R.string.overview_dog_yes)
    } else {
        stringResource(id = R.string.overview_dog_no)
    }

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
                    text = stringResource(id = R.string.overview_household_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_days_supply,
                        days
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_household_summary,
                        adults,
                        smallChildren,
                        bigChildren,
                        dogText
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (dayDiaperChildren > 0 || nightDiaperChildren > 0) {
                    Text(
                        text = stringResource(
                            id = R.string.overview_diaper_summary,
                            dayDiaperChildren,
                            nightDiaperChildren
                        ),
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
                    text = stringResource(id = R.string.overview_calculation_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_water,
                        waterLiters
                    )
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_staples,
                        formatPrepperOneDecimal(pastaOrRiceKg)
                    )
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_tomato_sauce,
                        tomatoSauceGlasses
                    )
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_canned_meals,
                        cannedMeals
                    )
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_gas_cartridges,
                        gasCartridges
                    )
                )
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
                    text = stringResource(id = R.string.overview_progress_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_progress_done,
                        checkedItemCount,
                        totalItemCount
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = stringResource(
                        id = R.string.overview_progress_percent,
                        progressPercent
                    ),
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
                Text(text = stringResource(id = R.string.overview_open_profile))
            }

            Button(
                onClick = onOpenList,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(id = R.string.overview_open_list))
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