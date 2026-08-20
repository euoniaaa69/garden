package com.example.domain.audio

import com.example.model.MusicTrack
import com.example.model.Playlist
import com.example.model.PlaylistStatus

object MusicCatalogue {

    // -------------------------------------------------------------
    // 1. PLAYLIST A: LOFI (AVAILABLE)
    // -------------------------------------------------------------
    val LOFI_PLAYLIST = Playlist(
        id = "lofi",
        name = "Lofi",
        description = "Relaxing beats for a peaceful day, study, or calm reading.",
        status = PlaylistStatus.AVAILABLE,
        coverAccentHex = 0xFFFACC15,
        tracks = listOf(
            MusicTrack(
                id = "lofi_1",
                title = "Morning Sunlight",
                durationSeconds = 160,
                moodDescription = "Warm Rhodes chords with subtle acoustic presence",
                chords = listOf(
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 293.66f), // Cmaj9
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // Am9
                    floatArrayOf(87.31f, 130.81f, 174.61f, 220.00f, 261.63f),  // Fmaj7
                    floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f)   // G13
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f)
            ),
            MusicTrack(
                id = "lofi_2",
                title = "Warm Coffee Brew",
                durationSeconds = 175,
                moodDescription = "Gentle jazz turnaround with smooth 7th harmonics",
                chords = listOf(
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(98.00f, 146.83f, 185.00f, 220.00f, 293.66f),  // G7(b9)
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 329.63f), // Cmaj9
                    floatArrayOf(110.00f, 138.59f, 164.81f, 207.65f, 246.94f)  // A7alt
                ),
                melodyNotes = floatArrayOf(293.66f, 349.23f, 392.00f, 440.00f, 523.25f, 587.33f)
            ),
            MusicTrack(
                id = "lofi_3",
                title = "Gentle Breeze",
                durationSeconds = 190,
                moodDescription = "Airy acoustic electric piano drifting calmly",
                chords = listOf(
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 392.00f), // Fmaj9
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 349.23f), // Em7
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 293.66f), // Dm7
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f)  // Cmaj7
                ),
                melodyNotes = floatArrayOf(261.63f, 329.63f, 392.00f, 493.88f, 523.25f)
            ),
            MusicTrack(
                id = "lofi_4",
                title = "Golden Hour Drift",
                durationSeconds = 180,
                moodDescription = "Warm sunset progression with mellow undertones",
                chords = listOf(
                    floatArrayOf(116.54f, 146.83f, 174.61f, 220.00f, 261.63f), // Bbmaj9
                    floatArrayOf(130.81f, 164.81f, 196.00f, 233.08f, 293.66f), // C7sus
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 349.23f), // Dm9
                    floatArrayOf(110.00f, 146.83f, 174.61f, 220.00f, 261.63f)  // Am7
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 349.23f, 392.00f, 440.00f, 587.33f)
            ),
            MusicTrack(
                id = "lofi_5",
                title = "Study Session",
                durationSeconds = 200,
                moodDescription = "Steady, focused lo-fi rhythm with soothing notes",
                chords = listOf(
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 293.66f), // Cmaj9
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 392.00f), // Em7
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 349.23f), // Fmaj7
                    floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f)   // G
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 440.00f, 523.25f)
            )
        )
    )

    // -------------------------------------------------------------
    // 2. PLAYLIST B: RAINY DAY (AVAILABLE)
    // -------------------------------------------------------------
    val RAINY_DAY_PLAYLIST = Playlist(
        id = "rainy_day",
        name = "Rainy Day",
        description = "Soft music for rainy weather and reflective solitude.",
        status = PlaylistStatus.AVAILABLE,
        coverAccentHex = 0xFF38BDF8,
        tracks = listOf(
            MusicTrack(
                id = "rain_1",
                title = "Puddle Ripples",
                durationSeconds = 170,
                moodDescription = "Calm minor chords reflecting water droplets",
                chords = listOf(
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // Am9
                    floatArrayOf(87.31f, 130.81f, 164.81f, 220.00f, 261.63f),  // Fmaj7#11
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f), // Cmaj7
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 329.63f)  // Em7
                ),
                melodyNotes = floatArrayOf(220.00f, 261.63f, 329.63f, 392.00f, 440.00f, 493.88f)
            ),
            MusicTrack(
                id = "rain_2",
                title = "Misty Window",
                durationSeconds = 185,
                moodDescription = "Slow reflective tones and gentle acoustic glass harmonies",
                chords = listOf(
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 220.00f), // Am7
                    floatArrayOf(123.47f, 146.83f, 174.61f, 220.00f, 261.63f), // Bm7b5
                    floatArrayOf(164.81f, 207.65f, 246.94f, 293.66f, 329.63f)  // E7b9
                ),
                melodyNotes = floatArrayOf(220.00f, 261.63f, 293.66f, 349.23f, 392.00f, 440.00f)
            ),
            MusicTrack(
                id = "rain_3",
                title = "Drizzle Over Leaves",
                durationSeconds = 165,
                moodDescription = "Atmospheric, muted chords with resonant decays",
                chords = listOf(
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 392.00f), // Fmaj9
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 293.66f), // Cmaj9
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f)   // Gsus4
                ),
                melodyNotes = floatArrayOf(261.63f, 329.63f, 392.00f, 440.00f, 523.25f)
            ),
            MusicTrack(
                id = "rain_4",
                title = "Cozy Shelter",
                durationSeconds = 190,
                moodDescription = "Intimate warmth while rain pours outside",
                chords = listOf(
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 329.63f), // Cmaj9
                    floatArrayOf(110.00f, 146.83f, 174.61f, 220.00f, 261.63f), // Am7
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(98.00f, 146.83f, 185.00f, 220.00f, 293.66f)   // G7b9
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 523.25f)
            ),
            MusicTrack(
                id = "rain_5",
                title = "Soft Rain Lullaby",
                durationSeconds = 210,
                moodDescription = "Slow, repetitive calm chords for deep peace",
                chords = listOf(
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // Am9
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 349.23f), // Em7
                    floatArrayOf(87.31f, 130.81f, 174.61f, 220.00f, 261.63f),  // Fmaj7
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f)  // Cmaj7
                ),
                melodyNotes = floatArrayOf(220.00f, 261.63f, 329.63f, 440.00f, 523.25f)
            )
        )
    )

    // -------------------------------------------------------------
    // 3. PLAYLIST C: NIGHT (AVAILABLE)
    // -------------------------------------------------------------
    val NIGHT_PLAYLIST = Playlist(
        id = "night",
        name = "Night",
        description = "Slow and peaceful night ambience and serene stargazing melodies.",
        status = PlaylistStatus.AVAILABLE,
        coverAccentHex = 0xFFA5B4FC,
        tracks = listOf(
            MusicTrack(
                id = "night_1",
                title = "Midnight Starlight",
                durationSeconds = 190,
                moodDescription = "Dreamy night chords and twinkling bell harmonics",
                chords = listOf(
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 392.00f), // Em7
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 349.23f), // Fmaj7
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f)  // Cmaj7
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 329.63f, 392.00f, 523.25f, 659.25f)
            ),
            MusicTrack(
                id = "night_2",
                title = "Moonlit Petals",
                durationSeconds = 205,
                moodDescription = "Subtle nocturne pacing with lush low-end pads",
                chords = listOf(
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 220.00f), // Am7
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f),   // G13
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 329.63f)  // Cmaj9
                ),
                melodyNotes = floatArrayOf(220.00f, 261.63f, 329.63f, 392.00f, 440.00f, 523.25f)
            ),
            MusicTrack(
                id = "night_3",
                title = "Distant Nebulae",
                durationSeconds = 210,
                moodDescription = "Expansive, ethereal pads with glowing high overtones",
                chords = listOf(
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 392.00f), // Fmaj9
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // Am9
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 329.63f), // Em7
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 293.66f)  // Dm7
                ),
                melodyNotes = floatArrayOf(261.63f, 329.63f, 392.00f, 493.88f, 587.33f)
            ),
            MusicTrack(
                id = "night_4",
                title = "Slumber Glow",
                durationSeconds = 195,
                moodDescription = "Warm, deeply soothing progression for quiet nights",
                chords = listOf(
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 261.63f), // Cmaj7
                    floatArrayOf(116.54f, 146.83f, 174.61f, 220.00f, 261.63f), // Bbmaj9
                    floatArrayOf(110.00f, 130.81f, 164.81f, 196.00f, 246.94f), // Am9
                    floatArrayOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f)   // G
                ),
                melodyNotes = floatArrayOf(261.63f, 293.66f, 349.23f, 392.00f, 523.25f)
            ),
            MusicTrack(
                id = "night_5",
                title = "Quiet Canopy",
                durationSeconds = 220,
                moodDescription = "Whispering night wind with gentle melodic touches",
                chords = listOf(
                    floatArrayOf(164.81f, 196.00f, 246.94f, 293.66f, 349.23f), // Em7
                    floatArrayOf(174.61f, 220.00f, 261.63f, 329.63f, 392.00f), // Fmaj9
                    floatArrayOf(146.83f, 174.61f, 220.00f, 261.63f, 329.63f), // Dm9
                    floatArrayOf(130.81f, 164.81f, 196.00f, 246.94f, 329.63f)  // Cmaj9
                ),
                melodyNotes = floatArrayOf(246.94f, 261.63f, 329.63f, 392.00f, 493.88f)
            )
        )
    )

    // -------------------------------------------------------------
    // 4. PLAYLIST D: NATURE (COMING SOON)
    // -------------------------------------------------------------
    val NATURE_PLAYLIST = Playlist(
        id = "nature",
        name = "Nature",
        description = "Natural sounds and relaxing ambience.",
        status = PlaylistStatus.COMING_SOON,
        subtitle = "Available in a future update",
        coverAccentHex = 0xFF4ADE80,
        tracks = emptyList()
    )

    // -------------------------------------------------------------
    // 5. PLAYLIST E: SLEEP (COMING SOON)
    // -------------------------------------------------------------
    val SLEEP_PLAYLIST = Playlist(
        id = "sleep",
        name = "Sleep",
        description = "Deep relaxing sounds for sleeping.",
        status = PlaylistStatus.COMING_SOON,
        subtitle = "Available in a future update",
        coverAccentHex = 0xFF818CF8,
        tracks = emptyList()
    )

    // -------------------------------------------------------------
    // 6. PLAYLIST F: JAPANESE GARDEN (COMING SOON)
    // -------------------------------------------------------------
    val JAPANESE_GARDEN_PLAYLIST = Playlist(
        id = "japanese_garden",
        name = "Japanese Garden",
        description = "Traditional Zen garden harmonies and peaceful stone fountain melodies.",
        status = PlaylistStatus.COMING_SOON,
        subtitle = "Available in a future update",
        coverAccentHex = 0xFFF472B6,
        tracks = emptyList()
    )

    val ALL_PLAYLISTS: List<Playlist> = listOf(
        LOFI_PLAYLIST,
        RAINY_DAY_PLAYLIST,
        NIGHT_PLAYLIST,
        NATURE_PLAYLIST,
        SLEEP_PLAYLIST,
        JAPANESE_GARDEN_PLAYLIST
    )

    fun getPlaylistById(id: String): Playlist {
        return ALL_PLAYLISTS.find { it.id == id } ?: LOFI_PLAYLIST
    }
}
