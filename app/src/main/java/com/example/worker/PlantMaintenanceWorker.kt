package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.GardenDatabase
import com.example.model.GardenSettingsEntity
import com.example.model.GrowthCalculator
import com.example.model.PlantCareLogEntity
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

/**
 * Background CoroutineWorker that updates plant hydration, growth stages, and health
 * status directly within the Room database based on real-time wall clock passage.
 */
class PlantMaintenanceWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = GardenDatabase.getDatabase(applicationContext)
            val plantDao = db.gardenPlantDao()
            val settingsDao = db.gardenSettingsDao()
            val careLogDao = db.plantCareLogDao()

            val plants = plantDao.getAllPlants().firstOrNull() ?: emptyList()
            if (plants.isEmpty()) {
                return Result.success()
            }

            val settings = settingsDao.getSettings().firstOrNull() ?: GardenSettingsEntity()
            val now = System.currentTimeMillis()

            for (plant in plants) {
                val liveState = GrowthCalculator.calculateLiveState(
                    plant = plant,
                    currentTimeMillis = now,
                    timeScaleMultiplier = settings.timeScaleMultiplier
                )

                val previousStage = plant.currentStage
                val newStage = liveState.stage
                val newHydration = liveState.hydrationLevel
                val newHealthStatus = liveState.healthStatus.name
                val newHealthScore = liveState.healthScore

                // Update the plant entity in Room database
                val updatedPlant = plant.copy(
                    hydrationLevel = newHydration,
                    healthScore = newHealthScore,
                    healthStatus = newHealthStatus,
                    currentStage = newStage
                )
                plantDao.updatePlant(updatedPlant)

                // If plant advanced to a new growth phase during background time, record milestone log
                if (newStage > previousStage) {
                    careLogDao.insertLog(
                        PlantCareLogEntity(
                            plantId = plant.id,
                            eventType = "STAGE_ADVANCED",
                            description = "Reached Stage $newStage: ${liveState.stageName} (Background)",
                            hydrationLevel = newHydration,
                            healthStatus = newHealthStatus,
                            timestamp = now
                        )
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

/**
 * Helper scheduler for managing background WorkManager tasks for ambient plant updates.
 */
object PlantWorkScheduler {

    private const val PERIODIC_WORK_TAG = "ambient_garden_plant_sync"
    private const val IMMEDIATE_WORK_TAG = "ambient_garden_immediate_sync"

    /**
     * Schedules periodic background sync (every 15 minutes) to calculate hydration decay
     * and growth progression based on real-time elapsed time.
     */
    fun schedulePeriodicPlantSync(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<PlantMaintenanceWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES // 5-minute flex interval
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    /**
     * Triggers an immediate one-time sync pass.
     */
    fun runImmediatePlantSync(context: Context) {
        val oneTimeWork = OneTimeWorkRequestBuilder<PlantMaintenanceWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_TAG,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
    }
}
