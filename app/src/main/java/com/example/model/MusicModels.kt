package com.example.model

enum class PlaylistStatus {
    AVAILABLE,
    COMING_SOON
}

data class MusicTrack(
    val id: String,
    val title: String,
    val durationSeconds: Int = 180, // e.g. 3 minutes
    val chords: List<FloatArray>,
    val melodyNotes: FloatArray,
    val moodDescription: String = "",
    val bpm: Float = 60f
)

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val status: PlaylistStatus,
    val tracks: List<MusicTrack> = emptyList(),
    val subtitle: String? = null,
    val coverAccentHex: Long = 0xFF6366F1
)

data class AudioPlayerState(
    val currentPlaylistId: String = "lofi",
    val currentTrackId: String = "lofi_1",
    val isPlaying: Boolean = true,
    val playbackPositionSeconds: Float = 0f,
    val totalDurationSeconds: Float = 180f,
    val isAutoMusic: Boolean = true,
    val isShuffle: Boolean = false,
    val musicVolume: Float = 0.65f,
    val ambientVolume: Float = 0.70f,
    val currentTrackTitle: String = "Morning Sunlight",
    val currentPlaylistName: String = "Lofi"
)
