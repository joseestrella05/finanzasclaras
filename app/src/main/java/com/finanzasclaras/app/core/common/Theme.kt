package com.finanzasclaras.app.core.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green500 = Color(0xFF4CAF50)
val Green700 = Color(0xFF388E3C)
val Green50 = Color(0xFFE8F5E9)
val Red500 = Color(0xFFF44336)
val Red700 = Color(0xFFD32F2F)
val Red50 = Color(0xFFFFEBEE)
val Blue500 = Color(0xFF2196F3)
val Orange500 = Color(0xFFFF9800)
val Yellow500 = Color(0xFFFFEB3B)
val Purple500 = Color(0xFF9C27B0)
val Teal500 = Color(0xFF009688)
val Grey50 = Color(0xFFFAFAFA)
val Grey100 = Color(0xFFF5F5F5)
val Grey200 = Color(0xFFEEEEEE)
val Grey800 = Color(0xFF424242)
val Grey900 = Color(0xFF212121)

private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = Color.White,
    primaryContainer = Green50,
    secondary = Blue500,
    onSecondary = Color.White,
    error = Red500,
    onError = Color.White,
    errorContainer = Red50,
    background = Grey50,
    onBackground = Grey900,
    surface = Color.White,
    onSurface = Grey900,
    surfaceVariant = Grey100,
    onSurfaceVariant = Grey800,
    outline = Grey200
)

private val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Color.White,
    primaryContainer = Green700,
    secondary = Blue500,
    onSecondary = Color.White,
    error = Red500,
    onError = Color.White,
    errorContainer = Red700,
    background = Grey900,
    onBackground = Color.White,
    surface = Grey800,
    onSurface = Color.White,
    surfaceVariant = Grey800,
    onSurfaceVariant = Grey200,
    outline = Grey800
)

@Composable
fun FinanzasClarasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
