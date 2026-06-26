package de.max.prepperapp.ui

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object PrepperInterstitialAdManager {

    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val MIN_TIME_BETWEEN_INTERSTITIALS_MS = 3 * 60 * 1000L

    private var interstitialAd: InterstitialAd? = null
    private var isLoading: Boolean = false
    private var lastShownRealtime: Long = 0L

    fun load(context: Context) {
        if (!PrepperAdConsentState.canRequestAds) {
            return
        }

        if (isLoading || interstitialAd != null) {
            return
        }

        isLoading = true

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context.applicationContext,
            TEST_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun showIfAvailable(
        context: Context,
        onFinished: () -> Unit
    ) {
        if (!PrepperAdConsentState.canRequestAds) {
            onFinished()
            return
        }

        val activity = context as? Activity

        if (activity == null) {
            load(context)
            onFinished()
            return
        }

        val now = SystemClock.elapsedRealtime()
        val wasRecentlyShown =
            now - lastShownRealtime < MIN_TIME_BETWEEN_INTERSTITIALS_MS

        if (wasRecentlyShown) {
            load(context)
            onFinished()
            return
        }

        val ad = interstitialAd

        if (ad == null) {
            load(context)
            onFinished()
            return
        }

        interstitialAd = null

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                lastShownRealtime = SystemClock.elapsedRealtime()
            }

            override fun onAdDismissedFullScreenContent() {
                load(activity)
                onFinished()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                load(activity)
                onFinished()
            }
        }

        ad.show(activity)
    }
}