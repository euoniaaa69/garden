package com.example.data

import com.example.model.GardenPlantEntity
import com.example.model.GardenSettingsEntity
import com.example.model.PlantCareLogEntity
import kotlinx.coroutines.flow.Flow

class GardenRepository(
    private val gardenPlantDao: GardenPlantDao,
    private val gardenSettingsDao: GardenSettingsDao,
    private val plantCareLogDao: PlantCareLogDao
) {

    // --- Plant Streams & Operations ---
    val allPlants: Flow<List<GardenPlantEntity>> = gardenPlantDao.getAllPlants()

    fun getActivePlant(slotIndex: Int = 0): Flow<GardenPlantEntity?> {
        return gardenPlantDao.getPlantBySlot(slotIndex)
    }

    suspend fun getPlantById(id: Long): GardenPlantEntity? {
        return gardenPlantDao.getPlantById(id)
    }

    suspend fun plantSeed(plant: GardenPlantEntity): Long {
        val id = gardenPlantDao.insertPlant(plant)
        plantCareLogDao.insertLog(
            PlantCareLogEntity(
                plantId = id,
                eventType = "PLANTED",
                description = "Planted ${plant.customNickname} (${plant.getSpecies().name})",
                hydrationLevel = 1.0f,
                healthStatus = "THRIVING"
            )
        )
        return id
    }

    suspend fun updatePlant(plant: GardenPlantEntity) {
        gardenPlantDao.updatePlant(plant)
    }

    suspend fun waterPlant(
        plantId: Long,
        timestamp: Long = System.currentTimeMillis(),
        hydrationLevel: Float = 1.0f,
        healthStatus: String = "THRIVING",
        healthScore: Float = 1.0f
    ) {
        gardenPlantDao.waterPlant(
            plantId = plantId,
            wateredTime = timestamp,
            hydrationLevel = hydrationLevel,
            healthStatus = healthStatus,
            healthScore = healthScore
        )
        plantCareLogDao.insertLog(
            PlantCareLogEntity(
                plantId = plantId,
                eventType = "WATERED",
                description = "Watered soil with fresh droplets",
                hydrationLevel = hydrationLevel,
                healthStatus = healthStatus,
                timestamp = timestamp
            )
        )
    }

    suspend fun recordStageAdvance(
        plantId: Long,
        newStage: Int,
        stageName: String,
        hydrationLevel: Float,
        healthStatus: String
    ) {
        plantCareLogDao.insertLog(
            PlantCareLogEntity(
                plantId = plantId,
                eventType = "STAGE_ADVANCED",
                description = "Reached Stage $newStage: $stageName",
                hydrationLevel = hydrationLevel,
                healthStatus = healthStatus
            )
        )
    }

    suspend fun removePlant(slotIndex: Int = 0) {
        gardenPlantDao.deletePlantBySlot(slotIndex)
    }

    suspend fun saveGardenSnapshot(
        plant: GardenPlantEntity,
        settings: GardenSettingsEntity,
        liveStage: Int,
        hydration: Float,
        healthScore: Float,
        healthStatus: String
    ): Boolean {
        val updatedPlant = plant.copy(
            currentStage = liveStage,
            hydrationLevel = hydration,
            healthScore = healthScore,
            healthStatus = healthStatus,
            lastWateredTimestamp = plant.lastWateredTimestamp
        )
        gardenPlantDao.updatePlant(updatedPlant)
        gardenSettingsDao.insertOrUpdate(settings)
        plantCareLogDao.insertLog(
            PlantCareLogEntity(
                plantId = if (plant.id > 0) plant.id else 1L,
                eventType = "MANUAL_SAVE",
                description = "Manual garden snapshot saved (Stage $liveStage, ${(hydration * 100).toInt()}% water)",
                hydrationLevel = hydration,
                healthStatus = healthStatus
            )
        )
        return true
    }

    // --- Care Logs Streams ---
    fun getCareLogsForPlant(plantId: Long): Flow<List<PlantCareLogEntity>> {
        return plantCareLogDao.getLogsForPlant(plantId)
    }

    fun getRecentCareLogs(): Flow<List<PlantCareLogEntity>> {
        return plantCareLogDao.getRecentLogs()
    }

    // --- Settings Stream & Operations ---
    val settings: Flow<GardenSettingsEntity?> = gardenSettingsDao.getSettings()

    suspend fun saveSettings(settingsEntity: GardenSettingsEntity) {
        gardenSettingsDao.insertOrUpdate(settingsEntity)
    }
}
