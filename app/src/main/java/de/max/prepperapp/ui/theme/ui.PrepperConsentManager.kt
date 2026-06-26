package de.max.prepperapp.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object PrepperConsentManager {

    private var isMobileAdsInitialized = false

    fun gatherConsentAndInitializeAds(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        val params = ConsentRequestParameters
            .Builder()
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                updateConsentState(consentInformation)

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    updateConsentState(consentInformation)

                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds(activity)
                    }
                }

                if (consentInformation.canRequestAds()) {
                    initializeMobileAds(activity)
                }
            },
            {
                updateConsentState(consentInformation)

                if (consentInformation.canRequestAds()) {
                    initializeMobileAds(activity)
                }
            }
        )
    }

    fun showPrivacyOptions(context: Context) {
        val activity = context.findActivity() ?: return

        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

            updateConsentState(consentInformation)

            if (consentInformation.canRequestAds()) {
                initializeMobileAds(activity)
                PrepperInterstitialAdManager.load(activity)
            }
        }
    }

    private fun updateConsentState(
        consentInformation: ConsentInformation
    ) {
        PrepperAdConsentState.updateCanRequestAds(
            consentInformation.canRequestAds()
        )

        PrepperAdConsentState.updatePrivacyOptionsRequired(
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        )
    }

    private fun initializeMobileAds(activity: Activity) {
        if (isMobileAdsInitialized) {
            return
        }

        isMobileAdsInitialized = true

        MobileAds.initialize(activity) {
            PrepperAdConsentState.updateCanRequestAds(true)
            PrepperInterstitialAdManager.load(activity)
        }
    }

    private fun Context.findActivity(): Activity? {
        var currentContext = this

        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) {
                return currentContext
            }

            currentContext = currentContext.baseContext
        }

        return null
    }
}