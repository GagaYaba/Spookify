package com.ynov.spookify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    background = ScreenBackground,
    onBackground = InkPrimary,
    surface = CardSurface,
    onSurface = InkPrimary,
    surfaceVariant = TealContainer,
    onSurfaceVariant = InkSecondary,
    outline = OutlineSoft,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = TealDark,
    onPrimary = ScreenBackgroundDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = InkPrimaryDark,
    background = ScreenBackgroundDark,
    onBackground = InkPrimaryDark,
    surface = CardSurfaceDark,
    onSurface = InkPrimaryDark,
    surfaceVariant = TealContainerDark,
    onSurfaceVariant = InkSecondaryDark,
    outline = OutlineSoftDark,
)

@Composable
fun SpookifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
