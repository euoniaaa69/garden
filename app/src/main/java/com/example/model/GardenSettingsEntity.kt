package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User persistent preferences for relaxation, audio mixing, weather, music player, and performance.
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
    val lofiChordPreset: Int = 0, // Legacy preset
    val languageCode: String = "en", // "en" for English, "id" for Indonesian
    val lastPlaylistId: String = "lofi",
    val lastTrackId: String = "lofi_1",
    val isAutoMusic: Boolean = true,
    val isShuffle: Boolean = false,
    val isMusicPlaying: Boolean = true
)
