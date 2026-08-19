package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted state of a planted garden slot in the Room database schema.
 * Tracks growth stages, hydration levels, and health status over time.
 */
@Entity(tableName = "garden_plants")
data class GardenPlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val slotIndex: Int = 0,
    val speciesId: String = "bonsai",
    val customNickname: String = "Serenity Pine",
    val plantedTimestamp: Long = System.currentTimeMillis(),
    val lastWateredTimestamp: Long = System.currentTimeMillis(),
    val totalWaterCount: Int = 1,
    val hydrationLevel: Float = 1.0f,
    val healthScore: Float = 1.0f,
    val healthStatus: String = "THRIVING",
    val currentStage: Int = 1,
    val isFavorite: Boolean = false
) {
    fun getSpecies(): PlantSpecies = PlantCatalogue.getSpeciesById(speciesId)

    fun getHealthStatusEnum(): PlantHealthStatus {
        return try {
            PlantHealthStatus.valueOf(healthStatus)
        } catch (_: Exception) {
            PlantHealthStatus.THRIVING
        }
    }
}
