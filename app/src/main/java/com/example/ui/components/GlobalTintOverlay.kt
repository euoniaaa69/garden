package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.domain.DayNightContext

/**
 * Global Screen Tinting Overlay that dynamically grades the entire device screen
 * with ambient light, subtle sun/moonbeams, and atmospheric temperature transitions
 * based on the real-time time of day (Morning, Sunset, Night).
 */
@Composable
fun GlobalTintOverlay(
    dayNightContext: DayNightContext,
    modifier: Modifier = Modifier
) {
    // Smooth animated color transitions between time of day states
    val animatedTint by animateColorAsState(
        targetValue = dayNightContext.globalScreenTint,
        animationSpec = tween(durationMillis = 1200),
        label = "GlobalScreenTintAnim"
    )

    val animatedShaft by animateColorAsState(
        targetValue = dayNightContext.sunShaftColor,
        animationSpec = tween(durationMillis = 1200),
        label = "SunShaftAnim"
    )

    val animatedHorizonGlow by animateColorAsState(
        targetValue = dayNightContext.horizonGlowColor.copy(alpha = 0.12f),
        animationSpec = tween(durationMillis = 1200),
        label = "HorizonGlowAnim"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        // 1. Fullscreen Atmospheric Color Grade Wash
        drawRect(color = animatedTint, size = size)

        // 2. Directional Celestial Sun/Moon Ray / Light Shaft from celestial position
        val celX = dayNightContext.sunMoonX * width
        val celY = dayNightContext.sunMoonY * height * 0.55f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    animatedShaft,
                    animatedShaft.copy(alpha = animatedShaft.alpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(celX, celY),
                radius = width * 0.85f
            ),
            radius = width * 0.85f,
            center = Offset(celX, celY)
        )

        // 3. Horizon Atmospheric Warming / Cooling Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    animatedHorizonGlow,
                    animatedHorizonGlow.copy(alpha = animatedHorizonGlow.alpha * 1.5f),
                    Color.Transparent
                ),
                startY = height * 0.35f,
                endY = height * 0.85f
            ),
            size = size
        )

        // 4. Subtle Cinematic Vignette for Night/Sunset depth
        val vignetteAlpha = when {
            dayNightContext.timeOfDay.isNight -> 0.38f
            dayNightContext.timeOfDay.label == "Sunset" || dayNightContext.timeOfDay.label == "Golden Hour" -> 0.18f
            else -> 0.08f
        }

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x00000000),
                    Color.Black.copy(alpha = vignetteAlpha * 0.5f),
                    Color.Black.copy(alpha = vignetteAlpha)
                ),
                center = Offset(width * 0.5f, height * 0.5f),
                radius = width * 0.85f
            ),
            size = size
        )
    }
}
