package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Ambient weather states that visually and acoustically transform the garden.
 */
enum class WeatherState(
    val id: String,
    val label: String,
    val description: String,
    val rainDropCount: Int,
    val cloudDensity: Int,
    val fogAlpha: Float,
    val skyTint: Color,
    val lightingMultiplier: Float
) {
    CLEAR(
        id = "clear",
        label = "Clear Skies",
        description = "Gentle sunbeams and quiet breeze.",
        rainDropCount = 0,
        cloudDensity = 3,
        fogAlpha = 0f,
        skyTint = Color.Transparent,
        lightingMultiplier = 1.0f
    ),
    CLOUDY(
        id = "cloudy",
        label = "Drifting Clouds",
        description = "Soft overcast shading and cool shadows.",
        rainDropCount = 0,
        cloudDensity = 8,
        fogAlpha = 0.05f,
        skyTint = Color(0x229E9E9E),
        lightingMultiplier = 0.85f
    ),
    LIGHT_RAIN(
        id = "light_rain",
        label = "Gentle Drizzle",
        description = "Calming pitter-patter of tiny raindrops.",
        rainDropCount = 40,
        cloudDensity = 10,
        fogAlpha = 0.12f,
        skyTint = Color(0x33455A64),
        lightingMultiplier = 0.75f
    ),
    RAIN(
        id = "rain",
        label = "Soothing Rain",
        description = "Deep nourishing rainfall and water ripples.",
        rainDropCount = 95,
        cloudDensity = 12,
        fogAlpha = 0.22f,
        skyTint = Color(0x4D263238),
        lightingMultiplier = 0.65f
    ),
    FOG(
        id = "fog",
        label = "Morning Mist",
        description = "Dreamy ethereal fog rolling over the garden.",
        rainDropCount = 0,
        cloudDensity = 6,
        fogAlpha = 0.40f,
        skyTint = Color(0x33B0BEC5),
        lightingMultiplier = 0.70f
    );

    companion object {
        fun fromId(id: String): WeatherState {
            return entries.find { it.id == id } ?: CLEAR
        }
    }
}
