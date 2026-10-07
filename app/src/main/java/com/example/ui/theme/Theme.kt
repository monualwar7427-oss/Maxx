package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TouchGuardDarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberBackground,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = CyberCyan,
    secondary = CyberBlue,
    onSecondary = TextPrimary,
    tertiary = AlertRed,
    onTertiary = TextPrimary,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberBorder,
    error = AlertRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force high-tech dark security aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = TouchGuardDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = CyberBackground.toArgb()
                window.navigationBarColor = CyberBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
