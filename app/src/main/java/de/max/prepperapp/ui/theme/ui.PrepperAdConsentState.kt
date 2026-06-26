package de.max.prepperapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PrepperAdConsentState {
    var canRequestAds by mutableStateOf(false)
        private set

    var isPrivacyOptionsRequired by mutableStateOf(false)
        private set

    fun updateCanRequestAds(value: Boolean) {
        canRequestAds = value
    }

    fun updatePrivacyOptionsRequired(value: Boolean) {
        isPrivacyOptionsRequired = value
    }
}