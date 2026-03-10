package com.example.serviceradar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = White,
    primaryContainer = NavyUltraLight,
    onPrimaryContainer = NavyPrimary,
    secondary = NavyAccent,
    onSecondary = White,
    secondaryContainer = NavyUltraLight,
    onSecondaryContainer = NavyAccent,
    tertiary = NavyLight,
    onTertiary = White,
    background = LightGray,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextLight,
    error = ErrorRed,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyPrimary,
    onPrimary = DarkTextPrimary,
    primaryContainer = Color(0xFF283593),
    onPrimaryContainer = DarkTextPrimary,
    secondary = NavyAccent,
    onSecondary = DarkTextPrimary,
    secondaryContainer = Color(0xFF3949AB),
    onSecondaryContainer = DarkTextPrimary,
    tertiary = NavyLight,
    onTertiary = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    error = ErrorRed,
    onError = DarkBackground,
)

@Composable
fun ServiceRadarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}