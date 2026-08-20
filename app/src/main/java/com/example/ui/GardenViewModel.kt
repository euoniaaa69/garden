package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GardenDatabase
import com.example.data.GardenRepository
import com.example.domain.AudioEngine
import com.example.domain.DayNightContext
import com.example.domain.DayNightEngine
import com.example.domain.ParticleManager
import com.example.domain.WeatherEngine
import com.example.model.AudioPlayerState
import com.example.model.GardenPlantEntity
import com.example.model.GardenSettingsEntity
import com.example.model.GrowthCalculator
import com.example.model.LivePlantState
import com.example.model.PlantCareLogEntity
import com.example.model.PlantCatalogue
import com.example.model.PlantSpecies
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

data class GardenUiState(
    val activePlant: GardenPlantEntity? = null,
    val species: PlantSpecies = PlantCatalogue.BONSAI,
    val liveGrowthState: LivePlantState = GrowthCalculator.calculateLiveState(
        GardenPlantEntity()
    ),
    val careLogs: List<PlantCareLogEntity> = emptyList(),
    val dayNightContext: DayNightContext = DayNightEngine.calculateCurrentContext(),
    val timeOfDayOverride: TimeOfDay? = null,
    val weatherState: WeatherState = WeatherState.CLEAR,
    val isAutoWeather: Boolean = true,
    val settings: GardenSettingsEntity = GardenSettingsEntity(),
    val isRelaxMode: Boolean = false,
    val isWatering: Boolean = false
)

private data class EnvironmentData(
    val dayNightContext: DayNightContext,
    val timeOfDayOverride: TimeOfDay?,
    val weatherState: WeatherState,
    val isRelaxMode: Boolean,
    val currentTime: Long
)

class GardenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GardenRepository
    val audioEngine = AudioEngine()
    val weatherEngine = WeatherEngine()
    val particleManager = ParticleManager()
    val randomEventManager = com.example.domain.RandomEventManager()

    val audioPlayerState: StateFlow<AudioPlayerState> = audioEngine.playerState

    private val _timeOfDayOverride = MutableStateFlow<TimeOfDay?>(null)
    val timeOfDayOverride: StateFlow<TimeOfDay?> = _timeOfDayOverride.asStateFlow()

    private val _dayNightContext = MutableStateFlow(DayNightEngine.calculateCurrentContext())
    val dayNightContext: StateFlow<DayNightContext> = _dayNightContext.asStateFlow()

    private val _isRelaxMode = MutableStateFlow(false)
    val isRelaxMode: StateFlow<Boolean> = _isRelaxMode.asStateFlow()

    private val _tickerTime = MutableStateFlow(System.currentTimeMillis())

    private var lastRecordedStage: Int = 1
    private var hasRestoredAudioSettings = false

    init {
        val db = GardenDatabase.getDatabase(application)
        repository = GardenRepository(
            gardenPlantDao = db.gardenPlantDao(),
            gardenSettingsDao = db.gardenSettingsDao(),
            plantCareLogDao = db.plantCareLogDao()
        )

        // Launch procedural audio engine
        audioEngine.start()

        // Restore persistent audio settings on start
        viewModelScope.launch {
            val savedSettings = repository.settings.firstOrNull() ?: GardenSettingsEntity()
            audioEngine.musicVolume = savedSettings.musicVolume
            audioEngine.ambientVolume = savedSettings.ambientVolume
            audioEngine.effectsVolume = savedSettings.effectsVolume
            audioEngine.musicManager.restoreState(
                playlistId = savedSettings.lastPlaylistId,
                trackId = savedSettings.lastTrackId,
                autoMusic = savedSettings.isAutoMusic,
                shuffle = savedSettings.isShuffle,
                playing = savedSettings.isMusicPlaying
            )
            audioEngine.syncPlayerState()
            hasRestoredAudioSettings = true
        }

        // Periodic ticker for day/night, live growth calculation, weather, and audio transitions
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                _tickerTime.value = now
                val localTime = LocalTime.now()
                val dn = DayNightEngine.calculateCurrentContext(localTime, _timeOfDayOverride.value)
                _dayNightContext.value = dn

                // Update audio engine environmental inputs and trigger auto music transitions
                audioEngine.onEnvironmentUpdated(
                    weather = weatherEngine.currentWeather.value,
                    timeOfDay = dn.timeOfDay
                )

                weatherEngine.tickWeatherSimulation(now)

                delay(1000) // 1-second pulse for smooth, responsive live updates
            }
        }
    }

    val plantsFlow: Flow<List<GardenPlantEntity>> = repository.allPlants
    val settingsFlow: Flow<GardenSettingsEntity?> = repository.settings
    val careLogsFlow: Flow<List<PlantCareLogEntity>> = repository.getRecentCareLogs()

    private val environmentFlow: Flow<EnvironmentData> = combine(
        _dayNightContext,
        _timeOfDayOverride,
        weatherEngine.currentWeather,
        _isRelaxMode,
        _tickerTime
    ) { dn, todOverride, weather, relax, time ->
        EnvironmentData(dn, todOverride, weather, relax, time)
    }

    val uiState: StateFlow<GardenUiState> = combine(
        plantsFlow,
        settingsFlow,
        careLogsFlow,
        environmentFlow
    ) { plants, settings, logs, env ->
        val activePlant: GardenPlantEntity = plants.firstOrNull() ?: GardenPlantEntity(
            slotIndex = 0,
            speciesId = "bonsai",
            customNickname = "Serenity Pine",
            plantedTimestamp = env.currentTime - (1000 * 60 * 30), // 30 mins ago
            lastWateredTimestamp = env.currentTime,
            hydrationLevel = 1.0f,
            healthScore = 1.0f,
            healthStatus = "THRIVING",
            currentStage = 1
        )

        // Ensure active plant exists in Room database
        if (plants.isEmpty()) {
            viewModelScope.launch {
                repository.plantSeed(activePlant)
            }
        }

        val currentSettings = settings ?: GardenSettingsEntity()
        val currentSpecies = activePlant.getSpecies()

        // Sync procedural audio engine volumes
        audioEngine.musicVolume = currentSettings.musicVolume
        audioEngine.ambientVolume = currentSettings.ambientVolume
        audioEngine.effectsVolume = currentSettings.effectsVolume

        val liveState = GrowthCalculator.calculateLiveState(
            plant = activePlant,
            currentTimeMillis = env.currentTime,
            timeScaleMultiplier = currentSettings.timeScaleMultiplier
        )

        // Check if plant advanced to a new growth stage and record in Room care log
        if (liveState.stage > lastRecordedStage && activePlant.id > 0) {
            lastRecordedStage = liveState.stage
            viewModelScope.launch {
                repository.recordStageAdvance(
                    plantId = activePlant.id,
                    newStage = liveState.stage,
                    stageName = liveState.stageName,
                    hydrationLevel = liveState.hydrationLevel,
                    healthStatus = liveState.healthStatus.name
                )
            }
        }

        GardenUiState(
            activePlant = activePlant,
            species = currentSpecies,
            liveGrowthState = liveState,
            careLogs = logs,
            dayNightContext = env.dayNightContext,
            timeOfDayOverride = env.timeOfDayOverride,
            weatherState = env.weatherState,
            isAutoWeather = weatherEngine.isAutoMode(),
            settings = currentSettings,
            isRelaxMode = env.isRelaxMode,
            isWatering = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GardenUiState()
    )

    fun waterPlant(touchX: Float = 0f, touchY: Float = 0f) {
        val currentPlant = uiState.value.activePlant ?: return
        val now = System.currentTimeMillis()

        // Emit watering burst particles & audio feedback
        particleManager.triggerWateringEffect(touchX, touchY)
        audioEngine.playWaterSplashEffect()

        val updatedPlant = currentPlant.copy(
            lastWateredTimestamp = now,
            totalWaterCount = currentPlant.totalWaterCount + 1,
            hydrationLevel = 1.0f,
            healthScore = 1.0f,
            healthStatus = "THRIVING"
        )

        viewModelScope.launch {
            repository.updatePlant(updatedPlant)
            if (currentPlant.id > 0) {
                repository.waterPlant(
                    plantId = currentPlant.id,
                    timestamp = now,
                    hydrationLevel = 1.0f,
                    healthStatus = "THRIVING"
                )
            }
        }
    }

    fun setTimeOfDayOverride(timeOfDay: TimeOfDay?) {
        _timeOfDayOverride.value = timeOfDay
        val dn = DayNightEngine.calculateCurrentContext(LocalTime.now(), timeOfDay)
        _dayNightContext.value = dn
        audioEngine.onEnvironmentUpdated(weatherEngine.currentWeather.value, dn.timeOfDay)
    }

    fun plantNewSpecies(species: PlantSpecies) {
        viewModelScope.launch {
            val newPlant = GardenPlantEntity(
                slotIndex = 0,
                speciesId = species.id,
                customNickname = species.name,
                plantedTimestamp = System.currentTimeMillis(),
                lastWateredTimestamp = System.currentTimeMillis(),
                totalWaterCount = 1,
                hydrationLevel = 1.0f,
                healthScore = 1.0f,
                healthStatus = "THRIVING",
                currentStage = 1
            )
            lastRecordedStage = 1
            repository.removePlant(0)
            repository.plantSeed(newPlant)
        }
    }

    fun setWeather(weather: WeatherState) {
        weatherEngine.setManualWeather(weather)
        updateSettings { it.copy(weatherMode = weather.id) }
        audioEngine.onEnvironmentUpdated(weather, _dayNightContext.value.timeOfDay)
    }

    fun setAutoWeather() {
        weatherEngine.setAutoCycle()
        updateSettings { it.copy(weatherMode = "auto") }
        audioEngine.onEnvironmentUpdated(weatherEngine.currentWeather.value, _dayNightContext.value.timeOfDay)
    }

    // =========================================================================
    // Music & Playlist Audio Controls
    // =========================================================================

    fun toggleMusicPlayPause() {
        audioEngine.musicManager.togglePlayPause()
        audioEngine.syncPlayerState()
        updateSettings {
            it.copy(
                isMusicPlaying = audioEngine.musicManager.isPlaying.get(),
                lastPlaylistId = audioEngine.musicManager.activePlaylist.id,
                lastTrackId = audioEngine.musicManager.currentTrack.id
            )
        }
    }

    fun nextMusicTrack() {
        audioEngine.musicManager.nextTrack()
        audioEngine.syncPlayerState()
        updateSettings {
            it.copy(
                lastPlaylistId = audioEngine.musicManager.activePlaylist.id,
                lastTrackId = audioEngine.musicManager.currentTrack.id
            )
        }
    }

    fun prevMusicTrack() {
        audioEngine.musicManager.prevTrack()
        audioEngine.syncPlayerState()
        updateSettings {
            it.copy(
                lastPlaylistId = audioEngine.musicManager.activePlaylist.id,
                lastTrackId = audioEngine.musicManager.currentTrack.id
            )
        }
    }

    fun selectMusicPlaylist(playlistId: String) {
        audioEngine.musicManager.selectPlaylist(playlistId, startPlaying = true)
        audioEngine.syncPlayerState()
        updateSettings {
            it.copy(
                lastPlaylistId = playlistId,
                lastTrackId = audioEngine.musicManager.currentTrack.id,
                isMusicPlaying = true
            )
        }
    }

    fun setMusicAutoMode(enabled: Boolean) {
        audioEngine.musicManager.isAutoMusic = enabled
        audioEngine.onEnvironmentUpdated(weatherEngine.currentWeather.value, _dayNightContext.value.timeOfDay)
        audioEngine.syncPlayerState()
        updateSettings { it.copy(isAutoMusic = enabled) }
    }

    fun setMusicShuffle(enabled: Boolean) {
        audioEngine.musicManager.isShuffle = enabled
        audioEngine.syncPlayerState()
        updateSettings { it.copy(isShuffle = enabled) }
    }

    fun setMusicVolume(volume: Float) {
        audioEngine.musicVolume = volume
        audioEngine.syncPlayerState()
        updateSettings { it.copy(musicVolume = volume) }
    }

    fun setAmbientVolume(volume: Float) {
        audioEngine.ambientVolume = volume
        audioEngine.syncPlayerState()
        updateSettings { it.copy(ambientVolume = volume) }
    }

    fun setEffectsVolume(volume: Float) {
        audioEngine.effectsVolume = volume
        updateSettings { it.copy(effectsVolume = volume) }
    }

    fun setChordPreset(preset: Int) {
        updateSettings { it.copy(lofiChordPreset = preset) }
    }

    fun setPerformanceMode(enabled: Boolean) {
        updateSettings { it.copy(performanceMode = enabled) }
    }

    fun setTimeScaleMultiplier(multiplier: Float) {
        updateSettings { it.copy(timeScaleMultiplier = multiplier) }
    }

    fun setLanguage(languageCode: String) {
        updateSettings { it.copy(languageCode = languageCode) }
    }

    fun toggleRelaxMode() {
        _isRelaxMode.value = !_isRelaxMode.value
    }

    fun exitRelaxMode() {
        _isRelaxMode.value = false
    }

    fun resetGardenPlot() {
        plantNewSpecies(PlantCatalogue.BONSAI)
    }

    /**
     * Persists the live garden snapshot (current growth stage, hydration, settings, care log)
     * directly into the Room SQLite database.
     */
    fun saveGardenProgress(onSaved: (String) -> Unit = {}) {
        viewModelScope.launch {
            val state = uiState.value
            val currentPlant = state.activePlant ?: GardenPlantEntity(
                slotIndex = 0,
                speciesId = state.species.id,
                customNickname = state.species.name,
                plantedTimestamp = System.currentTimeMillis() - (1000 * 60 * 30),
                lastWateredTimestamp = System.currentTimeMillis()
            )

            val live = state.liveGrowthState
            val currentAudioSettings = state.settings.copy(
                lastPlaylistId = audioEngine.musicManager.activePlaylist.id,
                lastTrackId = audioEngine.musicManager.currentTrack.id,
                isAutoMusic = audioEngine.musicManager.isAutoMusic,
                isShuffle = audioEngine.musicManager.isShuffle,
                isMusicPlaying = audioEngine.musicManager.isPlaying.get(),
                musicVolume = audioEngine.musicVolume,
                ambientVolume = audioEngine.ambientVolume
            )

            repository.saveGardenSnapshot(
                plant = currentPlant,
                settings = currentAudioSettings,
                liveStage = live.stage,
                hydration = live.hydrationLevel,
                healthScore = live.healthScore,
                healthStatus = live.healthStatus.name
            )

            // Play gentle chime effect
            audioEngine.playNotificationChime()

            onSaved("Garden snapshot saved to Room DB! 🌱 (Stage ${live.stage}, ${(live.hydrationLevel * 100).toInt()}% Water)")
        }
    }

    private fun updateSettings(transform: (GardenSettingsEntity) -> GardenSettingsEntity) {
        viewModelScope.launch {
            val current = uiState.value.settings
            val updated = transform(current)
            repository.saveSettings(updated)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
    }
}
