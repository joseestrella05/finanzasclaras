package com.finanzasclaras.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.finanzasclaras.app.core.common.FinanzasClarasTheme
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.presentation.navigation.NavGraph
import com.finanzasclaras.app.presentation.navigation.NavRoutes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by userPreferences.preferences.collectAsState(initial = null)

            prefs?.let { userPrefs ->
                val startDestination = when {
                    !userPrefs.isOnboardingCompleted -> NavRoutes.SPLASH
                    !userPrefs.isLoggedIn -> NavRoutes.LOGIN
                    else -> NavRoutes.DASHBOARD
                }

                FinanzasClarasTheme(darkTheme = userPrefs.darkModeEnabled) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
