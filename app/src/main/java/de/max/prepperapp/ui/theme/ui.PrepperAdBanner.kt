package de.max.prepperapp.ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun PrepperAdBanner(
    modifier: Modifier = Modifier
) {
    if (!PrepperAdConsentState.canRequestAds) {
        return
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current

        /*
         * Die verfügbare Breite des jeweiligen Containers wird in dp ermittelt.
         * Dadurch passt sich das Banner an verschiedene Displaygrößen an.
         */
        val adWidthDp = maxWidth.value
            .toInt()
            .coerceAtLeast(1)

        /*
         * Google Mobile Ads SDK 25 verwendet große adaptive Ankerbanner.
         * Die passende Höhe wird automatisch anhand der verfügbaren Breite
         * und der Geräteausrichtung berechnet.
         */
        val adaptiveAdSize = remember(
            context,
            adWidthDp
        ) {
            AdSize.getLargeAnchoredAdaptiveBannerAdSize(
                context,
                adWidthDp
            )
        }

        /*
         * Das AdView wird nur neu erzeugt, wenn sich Kontext,
         * Anzeigenbreite oder Anzeigengröße ändern.
         */
        val adView = remember(
            context,
            adaptiveAdSize
        ) {
            AdView(context).apply {
                setAdSize(adaptiveAdSize)
                adUnitId = PrepperAdIds.BANNER_AD_UNIT_ID

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                loadAd(
                    AdRequest.Builder().build()
                )
            }
        }

        /*
         * Das AdView wird beim Entfernen aus der Compose-Oberfläche
         * ordnungsgemäß freigegeben.
         */
        DisposableEffect(adView) {
            onDispose {
                adView.destroy()
            }
        }

        /*
         * Die berechnete Anzeigenhöhe wird bereits vor dem Laden reserviert.
         * Dadurch springen App-Inhalte nicht nachträglich nach oben oder unten.
         */
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(adaptiveAdSize.height.dp),
            factory = {
                adView
            }
        )
    }
}