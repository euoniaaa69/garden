package com.example.model

import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * Result data holder representing the computed live growth state, hydration level,
 * and health status of a plant over time.
 */
data class LivePlantState(
    val stage: Int, // 1 to 5
    val stageName: String,
    val stageDescription: String,
    val progressInStage: Float, // 0.0f to 1.0f
    val overallProgress: Float, // 0.0f to 1.0f
    val isMature: Boolean,
    val hydrationLevel: Float, // 0.0f to 1.0f
    val isThirsty: Boolean,
    val healthStatus: PlantHealthStatus,
    val healthScore: Float, // 0.0f to 1.0f
    val timeElapsedFormatted: String,
    val timeUntilNextStageFormatted: String,
    val totalMaturityEtaFormatted: String
)

object GrowthCalculator {

    /**
     * Calculates the real-time growth state, hydration level, and health status
     * of a plant entity based on elapsed wall-clock time and care history.
     */
    fun calculateLiveState(
        plant: GardenPlantEntity,
        currentTimeMillis: Long = System.currentTimeMillis(),
        timeScaleMultiplier: Float = 1.0f
    ): LivePlantState {
        val species = plant.getSpecies()

        // 1. Hydration decay calculation:
        // Soil stays moist for ~idealWaterIntervalHours (e.g. 24-48h).
        val waterElapsed = max(0L, currentTimeMillis - plant.lastWateredTimestamp)
        val fullDryDuration = species.idealWaterIntervalHours * 60 * 60 * 1000L
        val hydrationLevel = max(0.08f, 1.0f - (waterElapsed.toFloat() / fullDryDuration.toFloat()))
        val isThirsty = hydrationLevel < 0.35f

        // 2. Health Status & Score evaluation:
        val healthStatus = PlantHealthStatus.fromHydration(hydrationLevel)
        val healthScore = min(1.0f, max(0.1f, hydrationLevel * 0.9f + (if (plant.totalWaterCount > 0) 0.1f else 0f)))

        // 3. Growth rate pacing modulated by health condition:
        val growthHealthFactor = healthStatus.growthMultiplier
        val rawElapsed = max(0L, currentTimeMillis - plant.plantedTimestamp)
        val scaledElapsed = (rawElapsed * timeScaleMultiplier * growthHealthFactor).toLong()

        val totalDuration = species.growthDurationMillis
        val overallProgress = min(1.0f, scaledElapsed.toFloat() / totalDuration.toFloat())
        val isMature = overallProgress >= 1.0f

        // 4. Five distinct growth stages:
        // Stage 1 (0% - 20%): Seed
        // Stage 2 (20% - 40%): Sprout
        // Stage 3 (40% - 65%): Young Plant
        // Stage 4 (65% - 90%): Budding / Early Fruit / Blossom
        // Stage 5 (90% - 100%+): Mature Full Bloom
        val stage: Int
        val progressInStage: Float

        when {
            overallProgress >= 0.90f -> {
                stage = 5
                progressInStage = min(1.0f, (overallProgress - 0.90f) / 0.10f)
            }
            overallProgress >= 0.65f -> {
                stage = 4
                progressInStage = (overallProgress - 0.65f) / 0.25f
            }
            overallProgress >= 0.40f -> {
                stage = 3
                progressInStage = (overallProgress - 0.40f) / 0.25f
            }
            overallProgress >= 0.20f -> {
                stage = 2
                progressInStage = (overallProgress - 0.20f) / 0.20f
            }
            else -> {
                stage = 1
                progressInStage = overallProgress / 0.20f
            }
        }

        // Formatted strings for peaceful status readouts
        val elapsedFormatted = formatDuration(rawElapsed)

        val nextStageThresholdProgress = when (stage) {
            1 -> 0.20f
            2 -> 0.40f
            3 -> 0.65f
            4 -> 0.90f
            else -> 1.0f
        }

        val remainingRatio = max(0f, nextStageThresholdProgress - overallProgress)
        val remainingMillis = ((remainingRatio * totalDuration) / max(0.01f, timeScaleMultiplier * growthHealthFactor)).toLong()
        val timeUntilNextFormatted = if (isMature) "Fully Blossomed" else formatDuration(remainingMillis)

        val totalRemainingRatio = max(0f, 1.0f - overallProgress)
        val totalRemainingMillis = ((totalRemainingRatio * totalDuration) / max(0.01f, timeScaleMultiplier * growthHealthFactor)).toLong()
        val totalMaturityEtaFormatted = if (isMature) "Mature" else formatDuration(totalRemainingMillis)

        val stageName = species.stageNames.getOrElse(stage - 1) { "Growth" }
        val stageDescription = species.stageDescriptions.getOrElse(stage - 1) { "" }

        return LivePlantState(
            stage = stage,
            stageName = stageName,
            stageDescription = stageDescription,
            progressInStage = progressInStage,
            overallProgress = overallProgress,
            isMature = isMature,
            hydrationLevel = hydrationLevel,
            isThirsty = isThirsty,
            healthStatus = healthStatus,
            healthScore = healthScore,
            timeElapsedFormatted = elapsedFormatted,
            timeUntilNextStageFormatted = timeUntilNextFormatted,
            totalMaturityEtaFormatted = totalMaturityEtaFormatted
        )
    }

    private fun formatDuration(millis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return when {
            days > 0 -> String.format(Locale.getDefault(), "%dd %dh", days, hours)
            hours > 0 -> String.format(Locale.getDefault(), "%dh %dm", hours, minutes)
            minutes > 0 -> String.format(Locale.getDefault(), "%dm %ds", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%ds", max(1L, seconds))
        }
    }
}
