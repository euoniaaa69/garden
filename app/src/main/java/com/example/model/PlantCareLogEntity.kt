package com.example.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Historical log entry of plant care events, hydration shifts, and health status over time.
 */
@Entity(
    tableName = "plant_care_logs",
    foreignKeys = [
        ForeignKey(
            entity = GardenPlantEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plantId"]), Index(value = ["timestamp"])]
)
data class PlantCareLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val eventType: String, // "PLANTED", "WATERED", "STAGE_ADVANCED", "HEALTH_STATUS_CHANGED", "BLOOMED"
    val description: String,
    val hydrationLevel: Float,
    val healthStatus: String,
    val timestamp: Long = System.currentTimeMillis()
)
