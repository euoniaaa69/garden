package com.example.domain

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.domain.audio.AmbienceManager
import com.example.domain.audio.MusicCatalogue
import com.example.domain.audio.MusicManager
import com.example.model.AudioPlayerState
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, modular audio engine coordinating real-time Lo-Fi music playback,
 * environmental ambience, interactive sound effects, and seamless weather/time integration.
 *
 * Uses a single lightweight PCM-16 AudioTrack stream with zero runtime memory allocations.
 */
class AudioEngine {

    val musicManager = MusicManager()
    val ambienceManager = AmbienceManager()

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
    @Volatile var musicVolume: Float = 0.65f
    @Volatile var ambientVolume: Float = 0.70f
    @Volatile var effectsVolume: Float = 0.80f

    // Live state inputs
    @Volatile var currentWeather: WeatherState = WeatherState.CLEAR
    @Volatile var currentTimeOfDay: TimeOfDay = TimeOfDay.AFTERNOON

    // Interactive SFX Triggers
    private var waterSplashTrigger = false
    private var waterSplashPhase = 0f
    private var chimeTrigger = false
    private var chimePhase = 0f

    private val random = Random()

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

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

            audioThread = Thread({ runAudioSynthesisLoop() }, "GardenAudioStreamThread")
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

    /**
     * Called on weather or time of day updates to perform gentle, automatic playlist
     * crossfades when Auto Music mode is active.
     */
    fun onEnvironmentUpdated(weather: WeatherState, timeOfDay: TimeOfDay) {
        currentWeather = weather
        currentTimeOfDay = timeOfDay

        if (musicManager.isAutoMusic) {
            val desiredPlaylistId = when {
                weather == WeatherState.RAIN || weather == WeatherState.LIGHT_RAIN -> "rainy_day"
                timeOfDay.isNight -> "night"
                else -> "lofi"
            }

            if (musicManager.activePlaylist.id != desiredPlaylistId) {
                musicManager.selectPlaylist(desiredPlaylistId, startPlaying = musicManager.isPlaying.get())
            }
        }
        syncPlayerState()
    }

    fun syncPlayerState() {
        val track = musicManager.currentTrack
        _playerState.value = AudioPlayerState(
            currentPlaylistId = musicManager.activePlaylist.id,
            currentTrackId = track.id,
            isPlaying = musicManager.isPlaying.get(),
            playbackPositionSeconds = musicManager.currentPlaybackSeconds,
            totalDurationSeconds = track.durationSeconds.toFloat(),
            isAutoMusic = musicManager.isAutoMusic,
            isShuffle = musicManager.isShuffle,
            musicVolume = musicVolume,
            ambientVolume = ambientVolume,
            currentTrackTitle = track.title,
            currentPlaylistName = musicManager.activePlaylist.name
        )
    }

    private fun runAudioSynthesisLoop() {
        val shortBuffer = ShortArray(1024)
        var sampleIndex = 0L
        var stateSyncCounter = 0

        while (isRunning.get()) {
            for (i in shortBuffer.indices) {
                // 1. Lo-Fi Music Stream
                val musicSample = musicManager.synthesizeMusicSample(sampleIndex, musicVolume)

                // 2. Environmental Ambience Stream
                val ambientSample = ambienceManager.synthesizeAmbientSample(
                    sampleIndex,
                    currentWeather,
                    currentTimeOfDay,
                    ambientVolume
                )

                // 3. Sound Effects (Water Splash & Save Chime)
                var fxSample = 0.0
                if (waterSplashTrigger) {
                    val splashLen = sampleRate * 0.6f
                    val p = waterSplashPhase / splashLen
                    if (p < 1.0f) {
                        val splashEnv = (1.0f - p) * (1.0f - p)
                        val dropletFreq = 600.0 + 800.0 * (1.0 - p)
                        val splashTone = (sin(2.0 * PI * dropletFreq * (sampleIndex / sampleRate.toDouble())) + (random.nextFloat() - 0.5f) * 0.6f) * splashEnv
                        fxSample += splashTone * 0.4 * effectsVolume
                        waterSplashPhase++
                    } else {
                        waterSplashTrigger = false
                    }
                }

                if (chimeTrigger) {
                    val chimeLen = sampleRate * 1.2f
                    val p = chimePhase / chimeLen
                    if (p < 1.0f) {
                        val chimeEnv = exp(-3.5 * p)
                        val t = sampleIndex / sampleRate.toDouble()
                        val tone1 = sin(2.0 * PI * 523.25 * t)
                        val tone2 = sin(2.0 * PI * 659.25 * t) * 0.7
                        val tone3 = sin(2.0 * PI * 1046.50 * t) * 0.3
                        fxSample += (tone1 + tone2 + tone3) * 0.35 * chimeEnv * effectsVolume
                        chimePhase++
                    } else {
                        chimeTrigger = false
                    }
                }

                // Master mix with soft saturation
                val totalSample = (musicSample + ambientSample + fxSample).coerceIn(-1.0, 1.0)
                shortBuffer[i] = (totalSample * 32767.0).toInt().toShort()

                sampleIndex++
            }

            audioTrack?.write(shortBuffer, 0, shortBuffer.size)

            // Periodically sync UI state (every ~15 buffer cycles ≈ ~0.7 seconds)
            stateSyncCounter++
            if (stateSyncCounter >= 15) {
                stateSyncCounter = 0
                syncPlayerState()
            }
        }
    }
}
