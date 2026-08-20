package com.example.ui.util

import androidx.compose.runtime.compositionLocalOf

data class AppStrings(
    // Settings
    val settingsTitle: String = "Garden Settings",
    val lightingScreenTint: String = "LIGHTING & SCREEN TINT",
    val dynamicColorGrade: String = "Dynamic color grade transitions simulating morning, sunset, and starry night light.",
    val autoClock: String = "Auto Clock",
    val language: String = "LANGUAGE / BAHASA",
    val performanceBattery: String = "PERFORMANCE & BATTERY",
    val ecoPerformanceMode: String = "Eco Performance Mode",
    val ecoDesc: String = "Optimizes pixel particle rendering for cooler running.",
    val growthTimeScale: String = "GROWTH TIME SCALE",
    val growthDesc: String = "Default is wall-clock pacing (days). You can preview faster progression below.",
    val saveProgress: String = "Save Progress to Room DB",
    val plantFreshSeed: String = "Plant Fresh Seed",
    val english: String = "English",
    val indonesian: String = "Indonesia",
    
    // Main UI
    val save: String = "Save",
    val myGarden: String = "My Garden",
    val zenRelaxMode: String = "Zen Relax Mode • Pure ambient plant growth",
    
    // Plant Info
    val plantInfo: String = "Plant Info",
    val health: String = "Health",
    val moisture: String = "Moisture",
    val growthTimeline: String = "GROWTH TIMELINE (REAL TIME)",
    val age: String = "Age",
    val next: String = "Next",
    val growthPhases: String = "GROWTH PHASES",
    val phase: String = "Phase",
    val recentCareLog: String = "RECENT CARE LOG (ROOM DB)",
    val waterPlant: String = "Water Plant",
    val newSeed: String = "New Seed",
    
    // Weather
    val gardenWeather: String = "Garden Weather",
    val dynamicAtmosphere: String = "Dynamic atmosphere cycle",
    val naturalWeatherCycle: String = "Natural Weather Cycle",
    val naturalWeatherDesc: String = "Gently shifts between clear skies, clouds, and rain over time.",
    val manualOverride: String = "MANUAL OVERRIDE",
    
    // Audio & Music System
    val musicTitle: String = "Music",
    val musicSubtitle: String = "Curated calming playlists & soundscapes",
    val nowPlaying: String = "NOW PLAYING",
    val musicVolume: String = "Music Volume",
    val ambientVolume: String = "Ambience Volume",
    val effectsVolume: String = "Effects Volume",
    val autoMusic: String = "Auto Music",
    val autoMusicDesc: String = "Automatically selects playlists based on weather and time.",
    val shuffle: String = "Shuffle",
    val available: String = "AVAILABLE",
    val comingSoon: String = "COMING SOON",
    val tracksLabel: String = "Tracks",
    val play: String = "PLAY",
    val pause: String = "PAUSE",
    val activeBadge: String = "PLAYING",
    val futureUpdateSubtitle: String = "Available in a future update",
    val lofiSoundscape: String = "Lo-Fi Soundscape",
    val audioMixerDesc: String = "Calming ambient audio mixer",
    val melodicProgression: String = "MELODIC PROGRESSION"
)

val EnStrings = AppStrings()

val IdStrings = AppStrings(
    settingsTitle = "Pengaturan Kebun",
    lightingScreenTint = "PENCAHAYAAN & WARNA LAYAR",
    dynamicColorGrade = "Transisi warna dinamis mensimulasikan cahaya pagi, sore, dan malam penuh bintang.",
    autoClock = "Jam Otomatis",
    language = "BAHASA / LANGUAGE",
    performanceBattery = "PERFORMA & BATERAI",
    ecoPerformanceMode = "Mode Performa Ramah Lingkungan",
    ecoDesc = "Mengoptimalkan rendering partikel piksel agar perangkat tetap dingin.",
    growthTimeScale = "SKALA WAKTU PERTUMBUHAN",
    growthDesc = "Bawaan adalah waktu nyata (hari). Anda dapat melihat progres lebih cepat di bawah.",
    saveProgress = "Simpan Progres ke Room DB",
    plantFreshSeed = "Tanam Benih Baru",
    english = "Inggris",
    indonesian = "Indonesia",
    
    save = "Simpan",
    myGarden = "Kebunku",
    zenRelaxMode = "Mode Santai Zen • Pertumbuhan tanaman ambien yang murni",
    
    plantInfo = "Info Tanaman",
    health = "Kesehatan",
    moisture = "Kelembapan",
    growthTimeline = "GARIS WAKTU PERTUMBUHAN (WAKTU NYATA)",
    age = "Umur",
    next = "Selanjutnya",
    growthPhases = "FASE PERTUMBUHAN",
    phase = "Fase",
    recentCareLog = "LOG PERAWATAN TERBARU (ROOM DB)",
    waterPlant = "Siram Tanaman",
    newSeed = "Benih Baru",
    
    gardenWeather = "Cuaca Kebun",
    dynamicAtmosphere = "Siklus atmosfer dinamis",
    naturalWeatherCycle = "Siklus Cuaca Alami",
    naturalWeatherDesc = "Berubah secara perlahan antara langit cerah, awan, dan hujan seiring waktu.",
    manualOverride = "TIMPA MANUAL",
    
    musicTitle = "Musik",
    musicSubtitle = "Daftar putar menenangkan & lanskap suara",
    nowPlaying = "SEDANG DIPUTAR",
    musicVolume = "Volume Musik",
    ambientVolume = "Volume Suasana",
    effectsVolume = "Volume Efek",
    autoMusic = "Musik Otomatis",
    autoMusicDesc = "Secara otomatis memilih daftar putar berdasarkan cuaca dan waktu.",
    shuffle = "Acak",
    available = "TERSEDIA",
    comingSoon = "SEGERA HADIR",
    tracksLabel = "Lagu",
    play = "PUTAR",
    pause = "JEDA",
    activeBadge = "MEMUTAR",
    futureUpdateSubtitle = "Tersedia pada pembaruan mendatang",
    lofiSoundscape = "Lanskap Suara Lo-Fi",
    audioMixerDesc = "Pencampur audio ambien yang menenangkan",
    melodicProgression = "PROGRESI MELODI"
)

val LocalAppStrings = compositionLocalOf { EnStrings }
