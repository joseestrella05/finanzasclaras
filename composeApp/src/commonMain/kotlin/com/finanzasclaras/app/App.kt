package com.finanzasclaras.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.finanzasclaras.app.core.common.FinanzasClarasTheme
import com.finanzasclaras.app.presentation.navigation.NavGraph
import com.finanzasclaras.app.presentation.navigation.NavRoutes

@Composable
fun App() {
    FinanzasClarasTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                startDestination = NavRoutes.SPLASH
            )
        }
    }
}
