package com.example.domain.audio

import com.example.model.TimeOfDay
import com.example.model.WeatherState
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural environmental ambience synthesizer handling dynamic rain, wind breezes,
 * nighttime crickets, and morning atmosphere in complete isolation from the music stream.
 */
class AmbienceManager {

    private val random = Random()
    private val sampleRate = 22050

    // Filter states
    private var rainLowPass = 0.0f
    private var rainBandPass = 0.0f
    private var windModPhase = 0.0
    private var cricketPhase = 0.0

    // Morning bird state
    private var birdTriggerPhase = 0
    private var nextBirdInSamples = sampleRate * 4

    fun synthesizeAmbientSample(
        sampleIndex: Long,
        weather: WeatherState,
        timeOfDay: TimeOfDay,
        ambientVolume: Float
    ): Double {
        if (ambientVolume < 0.005f) return 0.0

        val rainIntensity = when (weather) {
            WeatherState.LIGHT_RAIN -> 0.35f
            WeatherState.RAIN -> 0.85f
            else -> 0.0f
        }

        val isNight = timeOfDay.isNight
        val isMorning = timeOfDay == TimeOfDay.MORNING || timeOfDay == TimeOfDay.DAWN
        val t = sampleIndex / sampleRate.toDouble()
        var ambientSample = 0.0

        // 1. Pink-filtered Rain sound
        if (rainIntensity > 0.04f) {
            val whiteNoise = (random.nextFloat() * 2.0f - 1.0f)
            rainLowPass += 0.18f * (whiteNoise - rainLowPass)
            rainBandPass += 0.06f * (rainLowPass - rainBandPass)
            val rainSound = (rainLowPass * 0.4f + rainBandPass * 0.6f) * rainIntensity * 0.42f
            ambientSample += rainSound
        }

        // 2. Dynamic Wind Breeze
        windModPhase += 0.0003
        val windMod = (sin(windModPhase) * 0.5 + 0.5) * 0.12
        val windNoise = (random.nextFloat() * 2.0f - 1.0f) * windMod.toFloat()
        ambientSample += windNoise

        // 3. Nighttime Crickets
        if (isNight && rainIntensity < 0.3f) {
            cricketPhase += 0.0015
            val chirpMod = sin(cricketPhase * 18.0)
            if (chirpMod > 0.6) {
                val cricketTone = sin(2.0 * PI * 4200.0 * t) * (chirpMod - 0.6) * 0.05
                ambientSample += cricketTone
            }
        }

        // 4. Morning Birds
        if (isMorning && rainIntensity < 0.1f) {
            nextBirdInSamples--
            if (nextBirdInSamples <= 0) {
                birdTriggerPhase = (sampleRate * 0.25).toInt()
                nextBirdInSamples = sampleRate * (5 + random.nextInt(8))
            }

            if (birdTriggerPhase > 0) {
                val p = birdTriggerPhase.toDouble() / (sampleRate * 0.25)
                val birdFreq = 2800.0 + 800.0 * sin(p * 12.0)
                val birdTone = sin(2.0 * PI * birdFreq * t) * (p * 0.04)
                ambientSample += birdTone
                birdTriggerPhase--
            }
        }

        return ambientSample * ambientVolume
    }
}
