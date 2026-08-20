package com.example.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.example.model.TimeOfDay
import java.time.LocalTime

/**
 * Live calculated sky, lighting, and global screen tinting context for the garden canvas and UI.
 */
data class DayNightContext(
    val timeOfDay: TimeOfDay,
    val localTimeFormatted: String,
    val localTimeWithSeconds: String = "",
    val skyTopColor: Color,
    val skyBottomColor: Color,
    val horizonGlowColor: Color,
    val globalScreenTint: Color,
    val sunShaftColor: Color,
    val cardBackgroundColor: Color,
    val cardBorderColor: Color,
    val cardCornerColor: Color,
    val foliageLightTint: Color,
    val ambientLight: Float, // 0.0 to 1.0
    val sunMoonX: Float, // 0.0 (left) to 1.0 (right)
    val sunMoonY: Float, // 0.0 (top) to 1.0 (bottom)
    val isSun: Boolean,
    val starAlpha: Float,
    val fireflyCount: Int,
    val cloudTint: Color,
    val moodTitle: String,
    val moodDescription: String
)

object DayNightEngine {

    /**
     * Computes the real-time atmosphere and global screen tinting from the device's local time
     * or a specified manual time override.
     */
    fun calculateCurrentContext(
        localTime: LocalTime = LocalTime.now(),
        overrideTimeOfDay: TimeOfDay? = null
    ): DayNightContext {
        val hour = localTime.hour
        val minute = localTime.minute
        val second = localTime.second
        val timeFloat = hour + (minute / 60.0f) + (second / 3600.0f) // 0.0 to 23.99

        val hourFormatted = String.format("%02d:%02d", hour, minute)
        val fullTimeFormatted = String.format("%02d:%02d:%02d", hour, minute, second)

        if (overrideTimeOfDay != null) {
            return fromPreset(overrideTimeOfDay, hourFormatted, fullTimeFormatted)
        }

        val tuple = when {
            // 1. Dawn (05:00 - 08:00)
            timeFloat in 5.0f..8.0f -> {
                val t = (timeFloat - 5.0f) / 3.0f
                val top = lerp(TimeOfDay.MIDNIGHT.skyTopColor, TimeOfDay.MORNING.skyTopColor, t)
                val bot = lerp(TimeOfDay.DAWN.skyBottomColor, TimeOfDay.MORNING.skyBottomColor, t)
                val hor = lerp(TimeOfDay.DAWN.horizonGlowColor, TimeOfDay.MORNING.horizonGlowColor, t)
                val tint = lerp(TimeOfDay.DAWN.globalScreenTint, TimeOfDay.MORNING.globalScreenTint, t)
                val shaft = lerp(TimeOfDay.DAWN.sunShaftColor, TimeOfDay.MORNING.sunShaftColor, t)
                val cardBg = lerp(TimeOfDay.DAWN.cardBackgroundColor, TimeOfDay.MORNING.cardBackgroundColor, t)
                val cardBorder = lerp(TimeOfDay.DAWN.cardBorderColor, TimeOfDay.MORNING.cardBorderColor, t)
                val cardCorner = lerp(TimeOfDay.DAWN.cardCornerColor, TimeOfDay.MORNING.cardCornerColor, t)
                val foliage = lerp(TimeOfDay.DAWN.foliageLightTint, TimeOfDay.MORNING.foliageLightTint, t)
                val amb = 0.40f + 0.50f * t
                val sx = 0.08f + 0.25f * t
                val sy = 0.65f - 0.35f * t
                val stars = (1.0f - t).coerceIn(0f, 1f)
                val ff = (6 * (1.0f - t)).toInt()
                DayNightTuple(
                    tod = if (t < 0.5f) TimeOfDay.DAWN else TimeOfDay.MORNING,
                    top = top, bot = bot, hor = hor, tint = tint, shaft = shaft,
                    cardBg = cardBg, cardBorderColor = cardBorder, cardCorner = cardCorner,
                    foliage = foliage, ambient = amb, sx = sx, sy = sy,
                    isSun = true, starAlpha = stars, fireflies = ff
                )
            }
            // 2. Morning to Afternoon (08:00 - 16:30)
            timeFloat in 8.0f..16.5f -> {
                val t = (timeFloat - 8.0f) / 8.5f
                val top = lerp(TimeOfDay.MORNING.skyTopColor, TimeOfDay.AFTERNOON.skyTopColor, t)
                val bot = lerp(TimeOfDay.MORNING.skyBottomColor, TimeOfDay.AFTERNOON.skyBottomColor, t)
                val hor = lerp(TimeOfDay.MORNING.horizonGlowColor, TimeOfDay.AFTERNOON.horizonGlowColor, t)
                val tint = lerp(TimeOfDay.MORNING.globalScreenTint, TimeOfDay.AFTERNOON.globalScreenTint, t)
                val shaft = lerp(TimeOfDay.MORNING.sunShaftColor, TimeOfDay.AFTERNOON.sunShaftColor, t)
                val cardBg = lerp(TimeOfDay.MORNING.cardBackgroundColor, TimeOfDay.AFTERNOON.cardBackgroundColor, t)
                val cardBorder = lerp(TimeOfDay.MORNING.cardBorderColor, TimeOfDay.AFTERNOON.cardBorderColor, t)
                val cardCorner = lerp(TimeOfDay.MORNING.cardCornerColor, TimeOfDay.AFTERNOON.cardCornerColor, t)
                val foliage = lerp(TimeOfDay.MORNING.foliageLightTint, TimeOfDay.AFTERNOON.foliageLightTint, t)
                val amb = 0.90f + 0.10f * (1.0f - kotlin.math.abs(t - 0.5f) * 2f)
                val sx = 0.33f + 0.38f * t
                val sy = 0.30f - 0.15f * kotlin.math.sin(t * Math.PI).toFloat()
                DayNightTuple(
                    tod = if (t < 0.4f) TimeOfDay.MORNING else TimeOfDay.AFTERNOON,
                    top = top, bot = bot, hor = hor, tint = tint, shaft = shaft,
                    cardBg = cardBg, cardBorderColor = cardBorder, cardCorner = cardCorner,
                    foliage = foliage, ambient = amb, sx = sx, sy = sy,
                    isSun = true, starAlpha = 0f, fireflies = 0
                )
            }
            // 3. Golden Hour & Sunset (16:30 - 19:30)
            timeFloat in 16.5f..19.5f -> {
                val t = (timeFloat - 16.5f) / 3.0f
                val top = lerp(TimeOfDay.GOLDEN_HOUR.skyTopColor, TimeOfDay.SUNSET.skyTopColor, t)
                val bot = lerp(TimeOfDay.GOLDEN_HOUR.skyBottomColor, TimeOfDay.SUNSET.skyBottomColor, t)
                val hor = lerp(TimeOfDay.GOLDEN_HOUR.horizonGlowColor, TimeOfDay.SUNSET.horizonGlowColor, t)
                val tint = lerp(TimeOfDay.GOLDEN_HOUR.globalScreenTint, TimeOfDay.SUNSET.globalScreenTint, t)
                val shaft = lerp(TimeOfDay.GOLDEN_HOUR.sunShaftColor, TimeOfDay.SUNSET.sunShaftColor, t)
                val cardBg = lerp(TimeOfDay.GOLDEN_HOUR.cardBackgroundColor, TimeOfDay.SUNSET.cardBackgroundColor, t)
                val cardBorder = lerp(TimeOfDay.GOLDEN_HOUR.cardBorderColor, TimeOfDay.SUNSET.cardBorderColor, t)
                val cardCorner = lerp(TimeOfDay.GOLDEN_HOUR.cardCornerColor, TimeOfDay.SUNSET.cardCornerColor, t)
                val foliage = lerp(TimeOfDay.GOLDEN_HOUR.foliageLightTint, TimeOfDay.SUNSET.foliageLightTint, t)
                val amb = 0.85f - 0.25f * t
                val sx = 0.71f + 0.19f * t
                val sy = 0.20f + 0.45f * t
                val stars = (t * 0.25f).coerceIn(0f, 1f)
                val ff = (2 + 8 * t).toInt()
                DayNightTuple(
                    tod = if (t < 0.4f) TimeOfDay.GOLDEN_HOUR else TimeOfDay.SUNSET,
                    top = top, bot = bot, hor = hor, tint = tint, shaft = shaft,
                    cardBg = cardBg, cardBorderColor = cardBorder, cardCorner = cardCorner,
                    foliage = foliage, ambient = amb, sx = sx, sy = sy,
                    isSun = true, starAlpha = stars, fireflies = ff
                )
            }
            // 4. Dusk (19:30 - 21:30)
            timeFloat in 19.5f..21.5f -> {
                val t = (timeFloat - 19.5f) / 2.0f
                val top = lerp(TimeOfDay.SUNSET.skyTopColor, TimeOfDay.DUSK.skyTopColor, t)
                val bot = lerp(TimeOfDay.SUNSET.skyBottomColor, TimeOfDay.DUSK.skyBottomColor, t)
                val hor = lerp(TimeOfDay.SUNSET.horizonGlowColor, TimeOfDay.DUSK.horizonGlowColor, t)
                val tint = lerp(TimeOfDay.SUNSET.globalScreenTint, TimeOfDay.DUSK.globalScreenTint, t)
                val shaft = lerp(TimeOfDay.SUNSET.sunShaftColor, TimeOfDay.DUSK.sunShaftColor, t)
                val cardBg = lerp(TimeOfDay.SUNSET.cardBackgroundColor, TimeOfDay.DUSK.cardBackgroundColor, t)
                val cardBorder = lerp(TimeOfDay.SUNSET.cardBorderColor, TimeOfDay.DUSK.cardBorderColor, t)
                val cardCorner = lerp(TimeOfDay.SUNSET.cardCornerColor, TimeOfDay.DUSK.cardCornerColor, t)
                val foliage = lerp(TimeOfDay.SUNSET.foliageLightTint, TimeOfDay.DUSK.foliageLightTint, t)
                val amb = 0.60f - 0.22f * t
                val mx = 0.12f + 0.20f * t
                val my = 0.60f - 0.30f * t
                val stars = 0.25f + 0.60f * t
                val ff = (8 + 12 * t).toInt()
                DayNightTuple(
                    tod = TimeOfDay.DUSK,
                    top = top, bot = bot, hor = hor, tint = tint, shaft = shaft,
                    cardBg = cardBg, cardBorderColor = cardBorder, cardCorner = cardCorner,
                    foliage = foliage, ambient = amb, sx = mx, sy = my,
                    isSun = false, starAlpha = stars, fireflies = ff
                )
            }
            // 5. Night & Midnight (21:30 - 05:00)
            else -> {
                val nightT = if (timeFloat >= 21.5f) {
                    (timeFloat - 21.5f) / 7.5f
                } else {
                    (timeFloat + 2.5f) / 7.5f
                }
                val isDeepMidnight = (timeFloat >= 23.5f || timeFloat < 3.5f)
                val targetTod = if (isDeepMidnight) TimeOfDay.MIDNIGHT else TimeOfDay.NIGHT

                val top = lerp(TimeOfDay.NIGHT.skyTopColor, TimeOfDay.MIDNIGHT.skyTopColor, nightT)
                val bot = lerp(TimeOfDay.NIGHT.skyBottomColor, TimeOfDay.MIDNIGHT.skyBottomColor, nightT)
                val hor = lerp(TimeOfDay.NIGHT.horizonGlowColor, TimeOfDay.MIDNIGHT.horizonGlowColor, nightT)
                val tint = lerp(TimeOfDay.NIGHT.globalScreenTint, TimeOfDay.MIDNIGHT.globalScreenTint, nightT)
                val shaft = lerp(TimeOfDay.NIGHT.sunShaftColor, TimeOfDay.MIDNIGHT.sunShaftColor, nightT)
                val cardBg = lerp(TimeOfDay.NIGHT.cardBackgroundColor, TimeOfDay.MIDNIGHT.cardBackgroundColor, nightT)
                val cardBorder = lerp(TimeOfDay.NIGHT.cardBorderColor, TimeOfDay.MIDNIGHT.cardBorderColor, nightT)
                val cardCorner = lerp(TimeOfDay.NIGHT.cardCornerColor, TimeOfDay.MIDNIGHT.cardCornerColor, nightT)
                val foliage = lerp(TimeOfDay.NIGHT.foliageLightTint, TimeOfDay.MIDNIGHT.foliageLightTint, nightT)
                val amb = 0.32f - 0.10f * kotlin.math.sin(nightT * Math.PI).toFloat()
                val mx = 0.32f + 0.48f * nightT
                val my = 0.22f + 0.06f * kotlin.math.sin(nightT * Math.PI).toFloat()
                DayNightTuple(
                    tod = targetTod,
                    top = top, bot = bot, hor = hor, tint = tint, shaft = shaft,
                    cardBg = cardBg, cardBorderColor = cardBorder, cardCorner = cardCorner,
                    foliage = foliage, ambient = amb, sx = mx, sy = my,
                    isSun = false, starAlpha = 0.95f, fireflies = 24
                )
            }
        }

        val cloudTint = if (tuple.isSun) {
            if (tuple.tod == TimeOfDay.SUNSET || tuple.tod == TimeOfDay.GOLDEN_HOUR || tuple.tod == TimeOfDay.DAWN) {
                Color(0xFFFFD1B3)
            } else {
                Color(0xFFFFFFFF)
            }
        } else {
            Color(0x773949AB)
        }

        return DayNightContext(
            timeOfDay = tuple.tod,
            localTimeFormatted = hourFormatted,
            localTimeWithSeconds = fullTimeFormatted,
            skyTopColor = tuple.top,
            skyBottomColor = tuple.bot,
            horizonGlowColor = tuple.hor,
            globalScreenTint = tuple.tint,
            sunShaftColor = tuple.shaft,
            cardBackgroundColor = tuple.cardBg,
            cardBorderColor = tuple.cardBorderColor,
            cardCornerColor = tuple.cardCorner,
            foliageLightTint = tuple.foliage,
            ambientLight = tuple.ambient,
            sunMoonX = tuple.sx,
            sunMoonY = tuple.sy,
            isSun = tuple.isSun,
            starAlpha = tuple.starAlpha,
            fireflyCount = tuple.fireflies,
            cloudTint = cloudTint,
            moodTitle = tuple.tod.moodTitle,
            moodDescription = tuple.tod.moodDescription
        )
    }

    private fun fromPreset(tod: TimeOfDay, formattedTime: String, fullTimeFormatted: String = ""): DayNightContext {
        val isSun = !tod.isNight
        val sx = if (isSun) 0.5f else 0.7f
        val sy = if (isSun) 0.25f else 0.22f
        val starAlpha = if (tod.isNight) 0.9f else if (tod == TimeOfDay.SUNSET || tod == TimeOfDay.DAWN) 0.2f else 0f

        return DayNightContext(
            timeOfDay = tod,
            localTimeFormatted = formattedTime,
            localTimeWithSeconds = fullTimeFormatted.ifEmpty { formattedTime },
            skyTopColor = tod.skyTopColor,
            skyBottomColor = tod.skyBottomColor,
            horizonGlowColor = tod.horizonGlowColor,
            globalScreenTint = tod.globalScreenTint,
            sunShaftColor = tod.sunShaftColor,
            cardBackgroundColor = tod.cardBackgroundColor,
            cardBorderColor = tod.cardBorderColor,
            cardCornerColor = tod.cardCornerColor,
            foliageLightTint = tod.foliageLightTint,
            ambientLight = tod.ambientLight,
            sunMoonX = sx,
            sunMoonY = sy,
            isSun = isSun,
            starAlpha = starAlpha,
            fireflyCount = tod.fireflyCount,
            cloudTint = if (isSun) Color(0xFFFFFFFF) else Color(0x773949AB),
            moodTitle = tod.moodTitle,
            moodDescription = tod.moodDescription
        )
    }

    private data class DayNightTuple(
        val tod: TimeOfDay,
        val top: Color,
        val bot: Color,
        val hor: Color,
        val tint: Color,
        val shaft: Color,
        val cardBg: Color,
        val cardBorderColor: Color,
        val cardCorner: Color,
        val foliage: Color,
        val ambient: Float,
        val sx: Float,
        val sy: Float,
        val isSun: Boolean,
        val starAlpha: Float,
        val fireflies: Int
    )
}
