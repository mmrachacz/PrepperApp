package de.max.prepperapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import de.max.prepperapp.ui.PrepperAdBanner
import de.max.prepperapp.ui.PrepperConsentManager
import de.max.prepperapp.ui.PrepperMainScreen
import de.max.prepperapp.ui.theme.PrepperAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrepperAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        PrepperAdBanner()
                    }
                ) { innerPadding ->
                    PrepperMainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        PrepperConsentManager.gatherConsentAndInitializeAds(this)
    }
}