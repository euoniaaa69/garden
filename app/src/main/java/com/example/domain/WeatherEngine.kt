package com.example.domain

import com.example.model.WeatherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random

/**
 * Weather simulation engine.
 * Supports both continuous soothing natural weather cycles and manual user mood selection.
 */
class WeatherEngine {

    private val _currentWeather = MutableStateFlow(WeatherState.CLEAR)
    val currentWeather: StateFlow<WeatherState> = _currentWeather.asStateFlow()

    private val random = Random()
    private var lastCycleTime = System.currentTimeMillis()
    private var weatherMode = "auto" // "auto" or specific weather id

    fun setWeatherMode(mode: String) {
        weatherMode = mode
        if (mode != "auto") {
            _currentWeather.value = WeatherState.fromId(mode)
        }
    }

    /**
     * Checks if weather should naturally transition in auto mode (e.g. every 10-15 minutes).
     */
    fun tickWeatherSimulation(currentTime: Long = System.currentTimeMillis()) {
        if (weatherMode != "auto") return

        // Change simulated weather every 12 minutes
        val elapsed = currentTime - lastCycleTime
        if (elapsed > 12 * 60 * 1000L) {
            lastCycleTime = currentTime
            val roll = random.nextFloat()
            val nextWeather = when {
                roll < 0.45f -> WeatherState.CLEAR
                roll < 0.70f -> WeatherState.CLOUDY
                roll < 0.85f -> WeatherState.LIGHT_RAIN
                roll < 0.95f -> WeatherState.RAIN
                else -> WeatherState.FOG
            }
            _currentWeather.value = nextWeather
        }
    }

    fun setManualWeather(weather: WeatherState) {
        weatherMode = weather.id
        _currentWeather.value = weather
    }

    fun setAutoCycle() {
        weatherMode = "auto"
    }

    fun isAutoMode(): Boolean = weatherMode == "auto"
}
