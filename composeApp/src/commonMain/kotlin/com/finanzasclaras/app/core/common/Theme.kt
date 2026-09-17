package com.finanzasclaras.app.core.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Emerald500 = Color(0xFF10B981)
val Emerald600 = Color(0xFF059669)
val Emerald50 = Color(0xFFECFDF5)
val Rose500 = Color(0xFFEF4444)
val Rose600 = Color(0xFFDC2626)
val Rose50 = Color(0xFFFEF2F2)
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate950 = Color(0xFF0B0F19)

// Backwards compatibility constants
val Green500 = Emerald500
val Green700 = Emerald600
val Green50 = Emerald50
val Red500 = Rose500
val Red700 = Rose600
val Red50 = Rose50
val Blue500 = Color(0xFF3B82F6)
val Orange500 = Color(0xFFF59E0B)
val Yellow500 = Color(0xFFEAB308)
val Purple500 = Color(0xFF8B5CF6)
val Teal500 = Color(0xFF14B8A6)
val Grey50 = Slate50
val Grey100 = Slate100
val Grey200 = Slate200
val Grey800 = Slate800
val Grey900 = Slate900

private val LightColorScheme = lightColorScheme(
    primary = Emerald600,
    onPrimary = Color.White,
    primaryContainer = Emerald50,
    onPrimaryContainer = Emerald600,
    secondary = Blue500,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEFF6FF),
    onSecondaryContainer = Color(0xFF1D4ED8),
    error = Rose500,
    onError = Color.White,
    errorContainer = Rose50,
    onErrorContainer = Rose600,
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    onPrimary = Slate950,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Emerald50,
    secondary = Color(0xFF60A5FA),
    onSecondary = Slate950,
    error = Rose500,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Rose50,
    background = Slate950,
    onBackground = Color.White,
    surface = Slate900,
    onSurface = Color.White,
    surfaceVariant = Slate800,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
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
