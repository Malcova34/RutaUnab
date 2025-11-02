package com.rutaunab.app.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFEA604),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEB92C),
    onPrimaryContainer = Color(0xFF1F2937),
    secondary = Color(0xFFFEB92C),
    onSecondary = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1F2937),
    surface = Color.White,
    onSurface = Color(0xFF1F2937)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFEA604),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEB92C),
    onPrimaryContainer = Color(0xFF1F2937),
    secondary = Color(0xFFFEB92C),
    onSecondary = Color.White,
    background = Color(0xFF1F2937),
    onBackground = Color.White,
    surface = Color(0xFF1F2937),
    onSurface = Color.White
)

@Composable
fun RutaUnabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

