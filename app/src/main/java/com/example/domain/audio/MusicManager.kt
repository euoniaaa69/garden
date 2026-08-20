package com.example.domain.audio

import com.example.model.MusicTrack
import com.example.model.Playlist
import com.example.model.PlaylistStatus
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Manages music playlists, active tracks, loop progression, shuffle queue,
 * and smooth crossfades between tracks and playlists.
 */
class MusicManager {

    private val random = Random()

    var activePlaylist: Playlist = MusicCatalogue.LOFI_PLAYLIST
        private set

    var currentTrackIndex: Int = 0
        private set

    val isPlaying = AtomicBoolean(true)
    var isShuffle: Boolean = false
    var isAutoMusic: Boolean = true

    // Playback timing
    var playbackPositionSamples: Long = 0L
    private val sampleRate = 22050

    // Smooth Crossfade State
    private var isCrossfading = false
    private var crossfadeProgress = 1.0f // 1.0 = fully current, < 1.0 = transition
    private var outgoingTrack: MusicTrack? = null
    private var outgoingTrackSampleIndex = 0L
    private val crossfadeDurationSamples = sampleRate * 3 // 3 second gentle crossfade

    // Dynamic chord progression state
    private var currentChordIndex = 0
    private var chordSampleCounter = 0
    private val chordLengthSamples = sampleRate * 4 // ~4 seconds per chord

    // Melody decoration state
    private var activeMelodyFreq = 0f
    private var melodySampleCounter = 0
    private val melodyDurationSamples = (sampleRate * 1.6).toInt()
    private var nextMelodyInSamples = sampleRate * 2

    val currentTrack: MusicTrack
        get() {
            val tracks = activePlaylist.tracks
            if (tracks.isEmpty()) return MusicCatalogue.LOFI_PLAYLIST.tracks[0]
            return tracks[currentTrackIndex.coerceIn(0, tracks.lastIndex)]
        }

    val currentProgressRatio: Float
        get() {
            val track = currentTrack
            val totalSamples = track.durationSeconds * sampleRate
            if (totalSamples <= 0) return 0f
            return (playbackPositionSamples.toFloat() / totalSamples.toFloat()).coerceIn(0f, 1f)
        }

    val currentPlaybackSeconds: Float
        get() = (playbackPositionSamples.toDouble() / sampleRate.toDouble()).toFloat()

    @Synchronized
    fun selectPlaylist(playlistId: String, startPlaying: Boolean = true) {
        val target = MusicCatalogue.getPlaylistById(playlistId)
        if (target.status == PlaylistStatus.COMING_SOON || target.tracks.isEmpty()) {
            return
        }

        if (activePlaylist.id == target.id) {
            if (startPlaying) isPlaying.set(true)
            return
        }

        // Trigger smooth crossfade
        startCrossfadeTo(target, 0, startPlaying)
    }

    @Synchronized
    fun selectTrack(trackIndex: Int) {
        val tracks = activePlaylist.tracks
        if (tracks.isEmpty()) return
        val validIndex = trackIndex.coerceIn(0, tracks.lastIndex)
        if (validIndex == currentTrackIndex) return

        startCrossfadeTo(activePlaylist, validIndex, true)
    }

    @Synchronized
    fun nextTrack() {
        val tracks = activePlaylist.tracks
        if (tracks.isEmpty()) return

        val nextIndex = if (isShuffle && tracks.size > 1) {
            var r = random.nextInt(tracks.size)
            while (r == currentTrackIndex) {
                r = random.nextInt(tracks.size)
            }
            r
        } else {
            (currentTrackIndex + 1) % tracks.size
        }

        startCrossfadeTo(activePlaylist, nextIndex, isPlaying.get())
    }

    @Synchronized
    fun prevTrack() {
        val tracks = activePlaylist.tracks
        if (tracks.isEmpty()) return

        // If played more than 3 seconds, restart current track
        if (playbackPositionSamples > sampleRate * 3) {
            playbackPositionSamples = 0L
            chordSampleCounter = 0
            currentChordIndex = 0
            return
        }

        val prevIndex = if (isShuffle && tracks.size > 1) {
            var r = random.nextInt(tracks.size)
            while (r == currentTrackIndex) {
                r = random.nextInt(tracks.size)
            }
            r
        } else {
            if (currentTrackIndex - 1 < 0) tracks.lastIndex else currentTrackIndex - 1
        }

        startCrossfadeTo(activePlaylist, prevIndex, isPlaying.get())
    }

    @Synchronized
    fun play() {
        isPlaying.set(true)
    }

    @Synchronized
    fun pause() {
        isPlaying.set(false)
    }

    @Synchronized
    fun togglePlayPause() {
        isPlaying.set(!isPlaying.get())
    }

    @Synchronized
    fun restoreState(playlistId: String, trackId: String, autoMusic: Boolean, shuffle: Boolean, playing: Boolean) {
        isAutoMusic = autoMusic
        isShuffle = shuffle
        isPlaying.set(playing)

        val targetPlaylist = MusicCatalogue.getPlaylistById(playlistId)
        if (targetPlaylist.status == PlaylistStatus.AVAILABLE && targetPlaylist.tracks.isNotEmpty()) {
            activePlaylist = targetPlaylist
            val foundIdx = targetPlaylist.tracks.indexOfFirst { it.id == trackId }
            currentTrackIndex = if (foundIdx >= 0) foundIdx else 0
        }
    }

    private fun startCrossfadeTo(playlist: Playlist, trackIndex: Int, play: Boolean) {
        outgoingTrack = currentTrack
        outgoingTrackSampleIndex = playbackPositionSamples
        isCrossfading = true
        crossfadeProgress = 0f

        activePlaylist = playlist
        currentTrackIndex = trackIndex
        playbackPositionSamples = 0L
        currentChordIndex = 0
        chordSampleCounter = 0
        activeMelodyFreq = 0f
        melodySampleCounter = 0
        if (play) isPlaying.set(true)
    }

    /**
     * Synthesizes audio samples for the active music track with mellow Rhodes / EPiano harmonics,
     * smooth envelopes, and crossfade interpolation.
     */
    fun synthesizeMusicSample(sampleIndex: Long, musicVolume: Float): Double {
        if (!isPlaying.get() || musicVolume < 0.005f) {
            return 0.0
        }

        val track = currentTrack
        val chords = track.chords
        if (chords.isEmpty()) return 0.0

        val currentChord = chords[currentChordIndex % chords.size]
        val t = sampleIndex / sampleRate.toDouble()

        // 1. Primary Chord Synthesis
        val chordProgress = chordSampleCounter.toFloat() / chordLengthSamples
        val envelope = if (chordProgress < 0.12f) {
            (chordProgress / 0.12f).toDouble()
        } else {
            exp(-2.0 * (chordProgress - 0.12).toDouble())
        }

        var sample = 0.0
        for (f in currentChord) {
            val phase = 2.0 * PI * f * t
            // Warm Rhodes tone: Fundamental + 2nd harmonic + soft 3rd harmonic
            val tone = sin(phase) + 0.32 * sin(phase * 2.0) + 0.10 * sin(phase * 3.0)
            sample += tone * 0.14
        }
        sample *= envelope

        // 2. Pentatonic Melodic Sparkles
        if (activeMelodyFreq > 0f && melodySampleCounter < melodyDurationSamples) {
            val mProg = melodySampleCounter.toDouble() / melodyDurationSamples.toDouble()
            val mEnv = if (mProg < 0.08) mProg / 0.08 else exp(-2.8 * (mProg - 0.08))
            val mPhase = 2.0 * PI * activeMelodyFreq * t
            val mTone = (sin(mPhase) + 0.22 * sin(mPhase * 2.0)) * mEnv * 0.16
            sample += mTone
            melodySampleCounter++
        } else if (melodySampleCounter >= melodyDurationSamples) {
            activeMelodyFreq = 0f
        }

        // 3. Handle Crossfade if in transition
        if (isCrossfading) {
            crossfadeProgress += 1.0f / crossfadeDurationSamples
            if (crossfadeProgress >= 1.0f) {
                crossfadeProgress = 1.0f
                isCrossfading = false
                outgoingTrack = null
            }
            sample *= crossfadeProgress
        }

        // Advance timing
        playbackPositionSamples++
        chordSampleCounter++

        if (chordSampleCounter >= chordLengthSamples) {
            chordSampleCounter = 0
            currentChordIndex = (currentChordIndex + 1) % chords.size
        }

        // Auto Advance / Loop check
        val trackTotalSamples = track.durationSeconds * sampleRate
        if (playbackPositionSamples >= trackTotalSamples) {
            nextTrack()
        }

        // Melodic note timing
        nextMelodyInSamples--
        if (nextMelodyInSamples <= 0 && activeMelodyFreq == 0f && track.melodyNotes.isNotEmpty()) {
            activeMelodyFreq = track.melodyNotes[random.nextInt(track.melodyNotes.size)]
            melodySampleCounter = 0
            nextMelodyInSamples = sampleRate * (3 + random.nextInt(4))
        }

        return sample * musicVolume
    }
}
