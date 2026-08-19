package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Real-time time of day classification and visual atmosphere parameters.
 * Transitions smoothly throughout the 24-hour cycle.
 */
enum class TimeOfDay(
    val label: String,
    val skyTopColor: Color,
    val skyBottomColor: Color,
    val horizonGlowColor: Color,
    val globalScreenTint: Color,
    val sunShaftColor: Color,
    val cardBackgroundColor: Color,
    val cardBorderColor: Color,
    val cardCornerColor: Color,
    val foliageLightTint: Color,
    val ambientLight: Float, // 0.0 (darkest) to 1.0 (brightest)
    val starCount: Int,
    val fireflyCount: Int,
    val isNight: Boolean,
    val moodTitle: String,
    val moodDescription: String
) {
    DAWN(
        label = "Dawn",
        skyTopColor = Color(0xFF1B2A47),
        skyBottomColor = Color(0xFFE88D67),
        horizonGlowColor = Color(0xFFFFD59E),
        globalScreenTint = Color(0x1CFB923C), // Warm peach-amber morning glow
        sunShaftColor = Color(0x28FDE047),
        cardBackgroundColor = Color(0xF20F172A),
        cardBorderColor = Color(0xFFF59E0B),
        cardCornerColor = Color(0xFFFEF08A),
        foliageLightTint = Color(0xFFFFE0B2),
        ambientLight = 0.58f,
        starCount = 12,
        fireflyCount = 6,
        isNight = false,
        moodTitle = "Golden Sunrise",
        moodDescription = "Soft morning rays gently warm the waking garden."
    ),
    MORNING(
        label = "Morning",
        skyTopColor = Color(0xFF5B92E5),
        skyBottomColor = Color(0xFFA6D0F7),
        horizonGlowColor = Color(0xFFFFF7C2),
        globalScreenTint = Color(0x0EFEF08A), // Crisp sunbeam luminance
        sunShaftColor = Color(0x1EFFF59D),
        cardBackgroundColor = Color(0xF00B132B),
        cardBorderColor = Color(0xFF38BDF8),
        cardCornerColor = Color(0xFFBAE6FD),
        foliageLightTint = Color(0xFFFFF9C4),
        ambientLight = 0.92f,
        starCount = 0,
        fireflyCount = 0,
        isNight = false,
        moodTitle = "Morning Dew",
        moodDescription = "Bright, cheerful daylight illuminating fresh sprouts."
    ),
    AFTERNOON(
        label = "Afternoon",
        skyTopColor = Color(0xFF3A7BD5),
        skyBottomColor = Color(0xFF8EC5FC),
        horizonGlowColor = Color(0xFFFFFFFF),
        globalScreenTint = Color(0x0638BDF8), // Clear azure ambiance
        sunShaftColor = Color(0x12FFFFFF),
        cardBackgroundColor = Color(0xF00B1120),
        cardBorderColor = Color(0xFF6366F1),
        cardCornerColor = Color(0xFFA5B4FC),
        foliageLightTint = Color(0xFFFFFFFF),
        ambientLight = 1.0f,
        starCount = 0,
        fireflyCount = 0,
        isNight = false,
        moodTitle = "Radiant Noon",
        moodDescription = "High sun providing maximum photosynthetic energy."
    ),
    GOLDEN_HOUR(
        label = "Golden Hour",
        skyTopColor = Color(0xFF4A569D),
        skyBottomColor = Color(0xFFE07A5F),
        horizonGlowColor = Color(0xFFF6BD60),
        globalScreenTint = Color(0x28F59E0B), // Rich golden amber filter
        sunShaftColor = Color(0x32F59E0B),
        cardBackgroundColor = Color(0xF2161026),
        cardBorderColor = Color(0xFFF59E0B),
        cardCornerColor = Color(0xFFFDE68A),
        foliageLightTint = Color(0xFFFFD54F),
        ambientLight = 0.82f,
        starCount = 0,
        fireflyCount = 4,
        isNight = false,
        moodTitle = "Golden Hour",
        moodDescription = "Warm amber glow casting long, cinematic shadows."
    ),
    SUNSET(
        label = "Sunset",
        skyTopColor = Color(0xFF2C194D),
        skyBottomColor = Color(0xFFC75D73),
        horizonGlowColor = Color(0xFFF4A261),
        globalScreenTint = Color(0x30EA580C), // Dramatic anime sunset orange-magenta
        sunShaftColor = Color(0x36F97316),
        cardBackgroundColor = Color(0xF21A0F2E),
        cardBorderColor = Color(0xFFF97316),
        cardCornerColor = Color(0xFFFCA5A5),
        foliageLightTint = Color(0xFFFFAB91),
        ambientLight = 0.62f,
        starCount = 8,
        fireflyCount = 10,
        isNight = false,
        moodTitle = "Anime Sunset",
        moodDescription = "Vibrant coral and violet clouds settling over the soil."
    ),
    DUSK(
        label = "Dusk",
        skyTopColor = Color(0xFF131738),
        skyBottomColor = Color(0xFF382952),
        horizonGlowColor = Color(0xFF6B4E71),
        globalScreenTint = Color(0x2A4F46E5), // Ethereal twilight indigo
        sunShaftColor = Color(0x18818CF8),
        cardBackgroundColor = Color(0xF20F0D24),
        cardBorderColor = Color(0xFF818CF8),
        cardCornerColor = Color(0xFFC7D2FE),
        foliageLightTint = Color(0xFFCE93D8),
        ambientLight = 0.42f,
        starCount = 28,
        fireflyCount = 16,
        isNight = true,
        moodTitle = "Twilight Dusk",
        moodDescription = "Soft purple twilight where the first fireflies emerge."
    ),
    NIGHT(
        label = "Night",
        skyTopColor = Color(0xFF090D1E),
        skyBottomColor = Color(0xFF151C38),
        horizonGlowColor = Color(0xFF1E284E),
        globalScreenTint = Color(0x240F172A), // Deep moonlit sapphire
        sunShaftColor = Color(0x1EA5B4FC),
        cardBackgroundColor = Color(0xF4070B18),
        cardBorderColor = Color(0xFF6366F1),
        cardCornerColor = Color(0xFF818CF8),
        foliageLightTint = Color(0xFF90CAF9),
        ambientLight = 0.30f,
        starCount = 45,
        fireflyCount = 22,
        isNight = true,
        moodTitle = "Starlit Night",
        moodDescription = "Quiet midnight breeze with sparkling constellations."
    ),
    MIDNIGHT(
        label = "Midnight",
        skyTopColor = Color(0xFF04060F),
        skyBottomColor = Color(0xFF0A1024),
        horizonGlowColor = Color(0xFF101935),
        globalScreenTint = Color(0x28020617), // Deepest obsidian cosmic filter
        sunShaftColor = Color(0x16818CF8),
        cardBackgroundColor = Color(0xF604060E),
        cardBorderColor = Color(0xFF4338CA),
        cardCornerColor = Color(0xFF6366F1),
        foliageLightTint = Color(0xFF7986CB),
        ambientLight = 0.22f,
        starCount = 60,
        fireflyCount = 26,
        isNight = true,
        moodTitle = "Cosmic Midnight",
        moodDescription = "Deep dreamscape under silver celestial moonlight."
    )
}
