package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArtisticDarkColorScheme = darkColorScheme(
    primary = ArtisticIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ArtisticIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = ArtisticEmerald,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = ArtisticWaterCyan,
    background = ArtisticObsidian,
    surface = ArtisticDarkCard,
    surfaceVariant = Color(0xFF1E293B),
    onBackground = ArtisticTextPrimary,
    onSurface = ArtisticTextPrimary,
    onSurfaceVariant = ArtisticTextSecondary,
    outline = ArtisticGlassBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArtisticDarkColorScheme,
        typography = Typography,
        content = content
    )
}
