package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User persistent preferences for relaxation, audio mixing, weather, and performance.
 */
@Entity(tableName = "garden_settings")
data class GardenSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val musicVolume: Float = 0.65f,
    val ambientVolume: Float = 0.70f,
    val effectsVolume: Float = 0.80f,
    val isRelaxMode: Boolean = false,
    val weatherMode: String = "auto", // "auto", "clear", "cloudy", "light_rain", "rain", "fog"
    val performanceMode: Boolean = false, // Lower particle count for smoother battery/FPS
    val timeScaleMultiplier: Float = 1.0f, // 1.0 = true real-time, can be boosted for demonstration
    val lofiChordPreset: Int = 0 // 0 = Calm Chords, 1 = Moonlight Lullaby, 2 = Sunbeam Melody
)
