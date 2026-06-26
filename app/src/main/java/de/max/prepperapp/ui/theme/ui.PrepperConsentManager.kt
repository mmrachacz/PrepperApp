package de.max.prepperapp.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object PrepperConsentManager {

    private const val TAG = "PrepperConsent"

    private var isMobileAdsInitialized = false

    fun gatherConsentAndInitializeAds(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        val paramsBuilder = ConsentRequestParameters.Builder()

        /*
         * Nur im Debug-Build:
         * - Consent-Status zurücksetzen
         * - EU/EWR-Geografie erzwingen
         *
         * Wichtig: Das ist nur für Tests.
         */
        if (activity.isDebugBuild()) {
            consentInformation.reset()

            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(
                    ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
                )
                .build()

            paramsBuilder.setConsentDebugSettings(debugSettings)

            Log.d(TAG, "Debug mode: consent reset + EEA geography forced")
        }

        val params = paramsBuilder.build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                updateConsentState(consentInformation)

                Log.d(
                    TAG,
                    "Consent info updated. canRequestAds=${consentInformation.canRequestAds()}, " +
                            "privacyOptions=${consentInformation.privacyOptionsRequirementStatus}"
                )

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.e(
                            TAG,
                            "Consent form error: ${formError.errorCode} - ${formError.message}"
                        )

                        Toast.makeText(
                            activity,
                            "Consent-Fehler: ${formError.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.d(TAG, "Consent form completed or not required")
                    }

                    updateConsentState(consentInformation)

                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds(activity)
                    }
                }

                if (consentInformation.canRequestAds()) {
                    initializeMobileAds(activity)
                }
            },
            { requestConsentError ->
                updateConsentState(consentInformation)

                Log.e(
                    TAG,
                    "Consent info update error: ${requestConsentError.errorCode} - ${requestConsentError.message}"
                )

                Toast.makeText(
                    activity,
                    "Consent-Update-Fehler: ${requestConsentError.message}",
                    Toast.LENGTH_LONG
                ).show()

                if (consentInformation.canRequestAds()) {
                    initializeMobileAds(activity)
                }
            }
        )
    }

    fun showPrivacyOptions(context: Context) {
        val activity = context.findActivity() ?: return

        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.e(
                    TAG,
                    "Privacy options error: ${formError.errorCode} - ${formError.message}"
                )

                Toast.makeText(
                    activity,
                    "Privacy-Optionen-Fehler: ${formError.message}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d(TAG, "Privacy options form completed")
            }

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

            Log.d(TAG, "Mobile Ads initialized")
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

    private fun Activity.isDebugBuild(): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}