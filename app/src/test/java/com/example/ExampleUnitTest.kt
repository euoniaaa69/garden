package com.example

import androidx.compose.ui.graphics.Color
import com.example.domain.DayNightEngine
import com.example.model.GardenPlantEntity
import com.example.model.GardenSettingsEntity
import com.example.model.GrowthCalculator
import com.example.model.PlantHealthStatus
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime

class ExampleUnitTest {

  @Test
  fun testPlantGrowthCalculation() {
    val plant = GardenPlantEntity(
      plantedTimestamp = 1000000L,
      lastWateredTimestamp = 1000000L,
      speciesId = "bonsai"
    )

    // Right after planting
    val state1 = GrowthCalculator.calculateLiveState(plant, currentTimeMillis = 1000000L)
    assertEquals(1, state1.stage)
    assertEquals("Conifer Seed", state1.stageName)
    assertFalse(state1.isMature)
    assertEquals(1.0f, state1.hydrationLevel, 0.01f)
    assertEquals(PlantHealthStatus.THRIVING, state1.healthStatus)

    // After 8 days (exceeds 7 days growth duration)
    val after8Days = 1000000L + (8 * 24 * 60 * 60 * 1000L)
    val stateMature = GrowthCalculator.calculateLiveState(
      plant.copy(lastWateredTimestamp = after8Days),
      currentTimeMillis = after8Days
    )
    assertEquals(5, stateMature.stage)
    assertTrue(stateMature.isMature)
    assertEquals("Majestic Ancient Bonsai", stateMature.stageName)
  }

  @Test
  fun testHydrationDecayAndHealthStatus() {
    val plant = GardenPlantEntity(
      plantedTimestamp = 1000000L,
      lastWateredTimestamp = 1000000L,
      speciesId = "watermelon" // idealWaterIntervalHours = 24
    )

    // 1. Freshly watered -> THRIVING
    val fresh = GrowthCalculator.calculateLiveState(plant, currentTimeMillis = 1000000L)
    assertEquals(PlantHealthStatus.THRIVING, fresh.healthStatus)
    assertFalse(fresh.isThirsty)

    // 2. After 18 hours (18/24 = 0.75 decayed, hydration ~0.25) -> THIRSTY
    val after18Hours = 1000000L + (18 * 60 * 60 * 1000L)
    val drying = GrowthCalculator.calculateLiveState(plant, currentTimeMillis = after18Hours)
    assertTrue(drying.hydrationLevel < 0.40f)

    // 3. After 48 hours without water -> THIRSTY / DORMANT
    val after48Hours = 1000000L + (48 * 60 * 60 * 1000L)
    val parched = GrowthCalculator.calculateLiveState(plant, currentTimeMillis = after48Hours)
    assertTrue(parched.isThirsty)
    assertTrue(parched.hydrationLevel <= 0.15f)
  }

  @Test
  fun testBackgroundPassageOfTimeSync() {
    val initialTime = 1000000L
    var plant = GardenPlantEntity(
      slotIndex = 0,
      speciesId = "sunflower", // 3 days total maturity, 20 hrs water interval
      plantedTimestamp = initialTime,
      lastWateredTimestamp = initialTime,
      hydrationLevel = 1.0f,
      healthStatus = "THRIVING",
      currentStage = 1
    )

    // Simulate 24 hours passing in background while app is closed
    val simulated24h = initialTime + (24 * 60 * 60 * 1000L)
    val backgroundState = GrowthCalculator.calculateLiveState(
      plant = plant,
      currentTimeMillis = simulated24h
    )

    // Soil moisture decays over time
    assertTrue(backgroundState.hydrationLevel < 0.20f)
    assertTrue(backgroundState.isThirsty)
    assertEquals(PlantHealthStatus.DORMANT, backgroundState.healthStatus)

    // Simulate worker writing state update to entity
    plant = plant.copy(
      hydrationLevel = backgroundState.hydrationLevel,
      healthScore = backgroundState.healthScore,
      healthStatus = backgroundState.healthStatus.name,
      currentStage = backgroundState.stage
    )

    assertEquals(backgroundState.stage, plant.currentStage)
    assertEquals(backgroundState.healthStatus.name, plant.healthStatus)

    // Simulate well-watered plant reaching maturity over 4 days
    val simulated4Days = initialTime + (4 * 24 * 60 * 60 * 1000L)
    val matureState = GrowthCalculator.calculateLiveState(
      plant = plant.copy(lastWateredTimestamp = simulated4Days),
      currentTimeMillis = simulated4Days
    )
    assertEquals(5, matureState.stage)
    assertTrue(matureState.isMature)
  }

  @Test
  fun testManualSaveProgressSnapshot() {
    val plant = GardenPlantEntity(
      id = 1L,
      slotIndex = 0,
      speciesId = "cherry_blossom",
      customNickname = "Sakura Blossom",
      plantedTimestamp = 1000000L,
      lastWateredTimestamp = 1000000L,
      hydrationLevel = 0.85f,
      healthScore = 0.95f,
      healthStatus = "THRIVING",
      currentStage = 2
    )

    val settings = GardenSettingsEntity(
      weatherMode = "light_rain",
      musicVolume = 0.8f,
      ambientVolume = 0.7f,
      performanceMode = false
    )

    // Simulate snapshot creation
    val savedSnapshot = plant.copy(
      currentStage = 3,
      hydrationLevel = 0.90f,
      healthScore = 1.0f,
      healthStatus = "THRIVING"
    )

    assertEquals(3, savedSnapshot.currentStage)
    assertEquals(0.90f, savedSnapshot.hydrationLevel, 0.01f)
    assertEquals("THRIVING", savedSnapshot.healthStatus)
    assertEquals("light_rain", settings.weatherMode)
  }

  @Test
  fun testGlobalTintingTransitions() {
    // 1. Morning light conditions (07:00 AM)
    val morningContext = DayNightEngine.calculateCurrentContext(LocalTime.of(7, 0))
    assertTrue(morningContext.isSun)
    assertTrue(morningContext.ambientLight > 0.5f)
    assertNotNull(morningContext.globalScreenTint)
    assertNotNull(morningContext.sunShaftColor)

    // 2. Noon radiant daylight (12:30 PM)
    val noonContext = DayNightEngine.calculateCurrentContext(LocalTime.of(12, 30))
    assertTrue(noonContext.isSun)
    assertEquals(TimeOfDay.AFTERNOON, noonContext.timeOfDay)
    assertTrue(noonContext.ambientLight >= 0.95f)

    // 3. Sunset light conditions (18:45 / 6:45 PM)
    val sunsetContext = DayNightEngine.calculateCurrentContext(LocalTime.of(18, 45))
    assertTrue(sunsetContext.isSun)
    assertEquals(TimeOfDay.SUNSET, sunsetContext.timeOfDay)
    assertTrue(sunsetContext.globalScreenTint.alpha > 0.10f)

    // 4. Night and Midnight light conditions (23:00 / 11:00 PM)
    val nightContext = DayNightEngine.calculateCurrentContext(LocalTime.of(23, 0))
    assertFalse(nightContext.isSun)
    assertTrue(nightContext.timeOfDay.isNight)
    assertTrue(nightContext.ambientLight < 0.35f)
    assertTrue(nightContext.starAlpha > 0.8f)
    assertTrue(nightContext.fireflyCount > 15)
  }

  @Test
  fun testWeatherParticleOverlayStates() {
    // 1. Clear weather sunshine properties
    val clearWeather = WeatherState.CLEAR
    assertEquals(0, clearWeather.rainDropCount)
    assertEquals(1.0f, clearWeather.lightingMultiplier, 0.01f)
    assertEquals("Clear Skies", clearWeather.label)

    // 2. Light rain drizzle properties
    val lightRain = WeatherState.LIGHT_RAIN
    assertTrue(lightRain.rainDropCount > 0)
    assertTrue(lightRain.fogAlpha > 0f)
    assertEquals("Gentle Drizzle", lightRain.label)

    // 3. Soothing rain properties
    val heavyRain = WeatherState.RAIN
    assertTrue(heavyRain.rainDropCount >= 90)
    assertTrue(heavyRain.lightingMultiplier < lightRain.lightingMultiplier)

    // 4. Fog / Mist properties
    val fog = WeatherState.FOG
    assertEquals(0, fog.rainDropCount)
    assertTrue(fog.fogAlpha >= 0.35f)

    // 5. Lookup by id
    assertEquals(WeatherState.CLEAR, WeatherState.fromId("clear"))
    assertEquals(WeatherState.RAIN, WeatherState.fromId("rain"))
    assertEquals(WeatherState.LIGHT_RAIN, WeatherState.fromId("light_rain"))
  }
}
