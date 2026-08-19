package com.example.domain

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Built-in real-time procedural audio engine generating relaxing lo-fi chord progressions,
 * calming rain ambience, wind breezes, nighttime crickets, and interactive watering sound effects.
 *
 * Runs seamlessly offline with zero external audio dependencies.
 */
class AudioEngine {

    private val sampleRate = 22050
    private val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(sampleRate / 4)

    private var audioTrack: AudioTrack? = null
    private var audioThread: Thread? = null
    private val isRunning = AtomicBoolean(false)

    // Master volume levels (0.0f to 1.0f)
    @Volatile var musicVolume: Float = 0.60f
    @Volatile var ambientVolume: Float = 0.65f
    @Volatile var effectsVolume: Float = 0.75f

    // Live state inputs
    @Volatile var currentWeather: WeatherState = WeatherState.CLEAR
    @Volatile var currentTimeOfDay: TimeOfDay = TimeOfDay.AFTERNOON
    @Volatile var chordPresetIndex: Int = 0

    // Triggerable sound effects
    private var waterSplashTrigger = false
    private var waterSplashPhase = 0f
    private var chimeTrigger = false
    private var chimePhase = 0f

    private val random = Random()

    // Lo-Fi Chord progressions (Frequencies in Hz)
    // Preset 0: Serene Garden (Cmaj9 - Am9 - Fmaj7 - G13)
    private val preset0Chords = listOf(
        floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 293.66f), // C3, E3, G3, B3, D4
        floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // A2, C3, E3, G3, B3
        floatArrayOf(87.31f, 130.81f, 174.61f, 220.00f, 261.63f),  // F2, C3, F3, A3, C4
        floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f)   // G2, D3, G3, B3, D4
    )

    // Preset 1: Moonlight Lullaby (Dm9 - Em7 - Fmaj7 - Cmaj7)
    private val preset1Chords = listOf(
        floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f),
        floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 392.00f),
        floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 349.23f),
        floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f)
    )

    // Pentatonic decorative melody notes
    private val melodyNotes = floatArrayOf(
        261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f, 587.33f
    )

    @Synchronized
    fun start() {
        if (isRunning.get()) return
        isRunning.set(true)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            audioThread = Thread({ runAudioSynthesisLoop() }, "AmbientAudioThread")
            audioThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            isRunning.set(false)
        }
    }

    @Synchronized
    fun stop() {
        isRunning.set(false)
        try {
            audioThread?.interrupt()
            audioThread = null
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playWaterSplashEffect() {
        waterSplashTrigger = true
        waterSplashPhase = 0f
    }

    fun playNotificationChime() {
        chimeTrigger = true
        chimePhase = 0f
    }

    private fun runAudioSynthesisLoop() {
        val shortBuffer = ShortArray(1024)
        var sampleIndex = 0L

        // Lo-fi chord timing state
        var currentChordIndex = 0
        var chordSampleCounter = 0
        val chordLengthSamples = sampleRate * 4 // ~4 seconds per chord

        // Melody note state
        var activeMelodyFreq = 0f
        var melodySampleCounter = 0
        val melodyDurationSamples = (sampleRate * 1.5).toInt()
        var nextMelodyInSamples = sampleRate * 2

        // Rain/Wind noise filters
        var rainLowPass = 0.0f
        var rainBandPass = 0.0f
        var windModPhase = 0.0

        // Crickets timing
        var cricketPhase = 0.0

        while (isRunning.get()) {
            val chords = if (chordPresetIndex == 1) preset1Chords else preset0Chords
            val currentChord = chords[currentChordIndex % chords.size]

            val rainIntensity = when (currentWeather) {
                WeatherState.LIGHT_RAIN -> 0.35f
                WeatherState.RAIN -> 0.85f
                else -> 0.0f
            }

            val isNightTime = currentTimeOfDay.isNight

            for (i in shortBuffer.indices) {
                val t = sampleIndex / sampleRate.toDouble()

                // -------------------------------------------------------------
                // 1. Lo-Fi Chord Synthesis (Warm mellow Rhodes / EPiano timbre)
                // -------------------------------------------------------------
                var musicSample = 0.0
                if (musicVolume > 0.01f) {
                    val chordProgress = chordSampleCounter.toFloat() / chordLengthSamples
                    // Smooth Attack & Decay envelope
                    val envelope: Double = if (chordProgress < 0.15f) {
                        (chordProgress / 0.15f).toDouble()
                    } else {
                        exp(-2.2 * (chordProgress - 0.15).toDouble())
                    }

                    for (f in currentChord) {
                        val phase = 2.0 * PI * f * t
                        // Fundamental + warm 2nd harmonic + soft 3rd harmonic
                        val tone = sin(phase) + 0.35 * sin(phase * 2.0) + 0.12 * sin(phase * 3.0)
                        musicSample += tone * 0.15
                    }
                    musicSample *= envelope * musicVolume

                    // Subtle decorative melody note
                    if (activeMelodyFreq > 0f && melodySampleCounter < melodyDurationSamples) {
                        val mProg = melodySampleCounter.toDouble() / melodyDurationSamples.toDouble()
                        val mEnv: Double = if (mProg < 0.08) mProg / 0.08 else exp(-3.0 * (mProg - 0.08))
                        val mPhase = 2.0 * PI * activeMelodyFreq * t
                        val mTone = (sin(mPhase) + 0.25 * sin(mPhase * 2.0)) * mEnv * 0.18 * musicVolume
                        musicSample += mTone
                        melodySampleCounter++
                    } else if (melodySampleCounter >= melodyDurationSamples) {
                        activeMelodyFreq = 0f
                    }
                }

                // -------------------------------------------------------------
                // 2. Ambience Synthesis (Rain, Wind, Crickets)
                // -------------------------------------------------------------
                var ambientSample = 0.0
                if (ambientVolume > 0.01f) {
                    // Rain pink-filtered noise
                    if (rainIntensity > 0.05f) {
                        val whiteNoise = (random.nextFloat() * 2.0f - 1.0f)
                        rainLowPass += 0.18f * (whiteNoise - rainLowPass)
                        rainBandPass += 0.06f * (rainLowPass - rainBandPass)
                        val rainSound = (rainLowPass * 0.4f + rainBandPass * 0.6f) * rainIntensity * 0.40f
                        ambientSample += rainSound
                    }

                    // Gentle Wind noise
                    windModPhase += 0.0003
                    val windMod = (sin(windModPhase) * 0.5 + 0.5) * 0.12
                    val windNoise = (random.nextFloat() * 2.0f - 1.0f) * windMod.toFloat()
                    ambientSample += windNoise

                    // Night Crickets (chirps when night and not heavy rain)
                    if (isNightTime && rainIntensity < 0.3f) {
                        cricketPhase += 0.0015
                        val chirpMod = sin(cricketPhase * 18.0)
                        if (chirpMod > 0.6) {
                            val cricketTone = sin(2.0 * PI * 4200.0 * t) * (chirpMod - 0.6) * 0.05
                            ambientSample += cricketTone
                        }
                    }

                    ambientSample *= ambientVolume
                }

                // -------------------------------------------------------------
                // 3. Sound Effects (Water Splash & Save Chime)
                // -------------------------------------------------------------
                var fxSample = 0.0
                if (waterSplashTrigger) {
                    val splashLen = sampleRate * 0.6f
                    if (waterSplashPhase < splashLen) {
                        val p = waterSplashPhase / splashLen
                        val splashEnv = (1.0f - p) * (1.0f - p)
                        val dropletFreq = 600.0 + 800.0 * (1.0 - p)
                        val splashTone = (sin(2.0 * PI * dropletFreq * t) + (random.nextFloat() - 0.5f) * 0.6f) * splashEnv
                        fxSample += splashTone * 0.4 * effectsVolume
                        waterSplashPhase++
                    } else {
                        waterSplashTrigger = false
                    }
                }

                if (chimeTrigger) {
                    val chimeLen = sampleRate * 1.2f
                    if (chimePhase < chimeLen) {
                        val p = chimePhase / chimeLen
                        val chimeEnv = exp(-3.5 * p)
                        // Harmonious two-tone major third bell chime (523.25 Hz C5 and 659.25 Hz E5)
                        val tone1 = sin(2.0 * PI * 523.25 * t)
                        val tone2 = sin(2.0 * PI * 659.25 * t) * 0.7
                        val tone3 = sin(2.0 * PI * 1046.50 * t) * 0.3
                        fxSample += (tone1 + tone2 + tone3) * 0.35 * chimeEnv * effectsVolume
                        chimePhase++
                    } else {
                        chimeTrigger = false
                    }
                }

                // Master mix and soft clipping
                val totalSample = (musicSample + ambientSample + fxSample).coerceIn(-1.0, 1.0)
                shortBuffer[i] = (totalSample * 32767.0).toInt().toShort()

                sampleIndex++
                chordSampleCounter++

                // Step chords
                if (chordSampleCounter >= chordLengthSamples) {
                    chordSampleCounter = 0
                    currentChordIndex = (currentChordIndex + 1) % chords.size
                }

                // Trigger occasional melody note
                nextMelodyInSamples--
                if (nextMelodyInSamples <= 0 && activeMelodyFreq == 0f) {
                    activeMelodyFreq = melodyNotes[random.nextInt(melodyNotes.size)]
                    melodySampleCounter = 0
                    nextMelodyInSamples = sampleRate * (3 + random.nextInt(4))
                }
            }

            audioTrack?.write(shortBuffer, 0, shortBuffer.size)
        }
    }
}
