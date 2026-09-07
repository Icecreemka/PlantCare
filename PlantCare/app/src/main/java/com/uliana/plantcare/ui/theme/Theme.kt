package com.uliana.plantcare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = LeafGreen,
    onPrimary = Color.White,
    primaryContainer = LeafGreenLight,
    secondary = SoilBrown,
    background = Cream,
    surface = Color.White,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = LeafGreenLight,
    onPrimary = LeafGreenDark,
    primaryContainer = LeafGreenDark,
    secondary = SoilBrown,
    background = Color(0xFF1B1F1C),
    surface = Color(0xFF222623),
    error = ErrorRed
)

@Composable
fun PlantCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = PlantCareTypography,
        content = content
    )
}
