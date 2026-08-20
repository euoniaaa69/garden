package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.domain.DayNightContext
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance, Canvas-based atmospheric weather and celestial overlay.
 * Renders overhauled sun/moon effects including dynamic volumetric god rays,
 * anamorphic sun lens flares, mystical moonbeams, shimmering celestial dust,
 * rain splashes, and night fireflies.
 */
@Composable
fun WeatherParticleOverlay(
    weatherState: WeatherState,
    modifier: Modifier = Modifier,
    isPerformanceMode: Boolean = false,
    sunPositionXRatio: Float = 0.5f,
    sunPositionYRatio: Float = 0.22f,
    dayNightContext: DayNightContext? = null
) {
    // 60 FPS animation ticker
    var frameTimeNanos by remember { mutableLongStateOf(0L) }
    var simulationTimeSeconds by remember { mutableFloatStateOf(0f) }

    // Celestial breathing & rotation animators
    val infiniteTransition = rememberInfiniteTransition(label = "WeatherAmbientInfinite")
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SunPulseAnim"
    )

    val celestialRayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 48000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CelestialRayRotationAnim"
    )

    // Particle state storage
    val rainParticles = remember { mutableStateListOf<RainParticleData>() }
    val splashParticles = remember { mutableStateListOf<SplashParticleData>() }
    val celestialMoteParticles = remember { mutableStateListOf<SunMoteParticleData>() }
    val fireflyParticles = remember { mutableStateListOf<FireflyParticleData>() }
    val fogBands = remember { mutableStateListOf<FogBandData>() }

    // Drive 60fps frame loop
    LaunchedEffect(Unit) {
        var lastNano = 0L
        while (true) {
            withFrameNanos { nano ->
                if (lastNano != 0L) {
                    val deltaSeconds = ((nano - lastNano) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    simulationTimeSeconds += deltaSeconds
                }
                lastNano = nano
                frameTimeNanos = nano
            }
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // Read animation frame time to trigger redraw every frame
        @Suppress("UNUSED_VARIABLE")
        val tick = frameTimeNanos
        val width = size.width
        val height = size.height
        if (width <= 0 || height <= 0) return@Canvas

        val isRainy = weatherState == WeatherState.RAIN || weatherState == WeatherState.LIGHT_RAIN
        val isFoggy = weatherState == WeatherState.FOG || weatherState == WeatherState.CLOUDY

        val isSunActive = dayNightContext?.isSun ?: true
        val timeOfDay = dayNightContext?.timeOfDay ?: TimeOfDay.AFTERNOON
        val effectiveCelXRatio = dayNightContext?.sunMoonX ?: sunPositionXRatio
        val effectiveCelYRatio = (dayNightContext?.sunMoonY ?: sunPositionYRatio) * 0.55f
        val celX = width * effectiveCelXRatio
        val celY = height * effectiveCelYRatio

        // =====================================================================
        // 1. CELESTIAL ATMOSPHERIC EFFECTS (SUN / MOON)
        // =====================================================================
        if (!isRainy) {
            if (isSunActive) {
                // -------------------------------------------------------------
                // 1A. SUN EFFECTS: Volumetric God Rays & Lens Flares
                // -------------------------------------------------------------
                val isSunset = timeOfDay == TimeOfDay.SUNSET || timeOfDay == TimeOfDay.GOLDEN_HOUR
                val isDawn = timeOfDay == TimeOfDay.DAWN
                val baseRadius = width * (if (isSunset) 0.55f else 0.45f) * sunPulse

                // 1. Radiant Ambient Corona Bloom
                val coronaColors = when {
                    isSunset -> listOf(Color(0x45F97316), Color(0x22EA580C), Color(0x0A7C2D12), Color.Transparent)
                    isDawn -> listOf(Color(0x3DFF8A80), Color(0x18FFE082), Color.Transparent)
                    else -> listOf(Color(0x35FFF9C4), Color(0x18FDE047), Color(0x08F59E0B), Color.Transparent)
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = coronaColors,
                        center = Offset(celX, celY),
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = Offset(celX, celY)
                )

                // 2. Rotating Volumetric Golden God Rays
                val rayCount = if (isPerformanceMode) 6 else 10
                val rayLength = width * 1.1f
                val rayColors = when {
                    isSunset -> listOf(Color(0x28F97316), Color(0x10EA580C), Color.Transparent)
                    isDawn -> listOf(Color(0x22FFD54F), Color(0x0CFFAB91), Color.Transparent)
                    else -> listOf(Color(0x22FFF59D), Color(0x0CFFD54F), Color.Transparent)
                }

                for (i in 0 until rayCount) {
                    val angleDeg = celestialRayRotation + (360f / rayCount) * i
                    val angleRad = (angleDeg * PI / 180.0).toFloat()
                    val spread = 0.075f

                    val p1x = celX + cos(angleRad - spread) * rayLength
                    val p1y = celY + sin(angleRad - spread) * rayLength
                    val p2x = celX + cos(angleRad + spread) * rayLength
                    val p2y = celY + sin(angleRad + spread) * rayLength

                    val rayPath = Path().apply {
                        moveTo(celX, celY)
                        lineTo(p1x, p1y)
                        lineTo(p2x, p2y)
                        close()
                    }

                    drawPath(
                        path = rayPath,
                        brush = Brush.radialGradient(
                            colors = rayColors,
                            center = Offset(celX, celY),
                            radius = rayLength
                        )
                    )
                }

                // 3. Peak-Sun Atmospheric Lens Flare Effect (Clear Weather Zenith Peak)
                val isClearWeather = weatherState == WeatherState.CLEAR
                val isDaytimeSun = isSunActive && !timeOfDay.isNight && timeOfDay != TimeOfDay.DUSK
                val sunZenithFactor = if (isDaytimeSun && isClearWeather) {
                    val altitudeScore = (1.0f - (effectiveCelYRatio / 0.32f)).coerceIn(0f, 1f)
                    val todMultiplier = when (timeOfDay) {
                        TimeOfDay.MORNING -> 0.90f
                        TimeOfDay.AFTERNOON -> 1.0f
                        TimeOfDay.DAWN, TimeOfDay.GOLDEN_HOUR, TimeOfDay.SUNSET -> 0.30f
                        else -> 1.0f
                    }
                    (altitudeScore * todMultiplier).coerceIn(0f, 1f)
                } else 0f

                if (sunZenithFactor > 0.02f) {
                    drawPeakSunLensFlare(
                        drawScope = this,
                        celX = celX,
                        celY = celY,
                        width = width,
                        height = height,
                        zenithFactor = sunZenithFactor,
                        animTime = simulationTimeSeconds,
                        pulse = sunPulse
                    )
                }

                // 4. Warm Sun Dust / Pollen Motes
                val maxMotes = if (isPerformanceMode) 16 else 32
                val moteColorPrimary = if (isSunset) Color(0xFFFFAB40) else Color(0xFFFFF59D)
                val moteColorSecondary = if (isSunset) Color(0xFFFF7043) else Color(0xFFFFD54F)

                if (celestialMoteParticles.size < maxMotes) {
                    val random = Random()
                    while (celestialMoteParticles.size < maxMotes) {
                        celestialMoteParticles.add(
                            SunMoteParticleData(
                                x = random.nextFloat() * width,
                                y = random.nextFloat() * height,
                                baseX = random.nextFloat() * width,
                                speedY = -12f - random.nextFloat() * 18f,
                                swaySpeed = 0.8f + random.nextFloat() * 1.5f,
                                swayOffset = random.nextFloat() * 6.28f,
                                radius = 2.0f + random.nextFloat() * 3.0f,
                                alpha = 0.3f + random.nextFloat() * 0.5f,
                                color = if (random.nextBoolean()) moteColorPrimary else moteColorSecondary
                            )
                        )
                    }
                }
            } else {
                // -------------------------------------------------------------
                // 1B. MOON EFFECTS: Volumetric Moonbeams & Silver Stardust
                // -------------------------------------------------------------
                val baseRadius = width * 0.40f * sunPulse

                // 1. Ethereal Moonlit Aura Bloom
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x28818CF8),
                            Color(0x1238BDF8),
                            Color(0x051E1B4B),
                            Color.Transparent
                        ),
                        center = Offset(celX, celY),
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = Offset(celX, celY)
                )

                // 2. Cascading Volumetric Moonbeams (Sinar Bulan)
                val beamCount = if (isPerformanceMode) 4 else 7
                val beamLength = width * 0.95f
                for (i in 0 until beamCount) {
                    val baseAngle = 40f + (70f / (beamCount - 1)) * i
                    val sway = sin((simulationTimeSeconds * 0.8f + i * 1.2f).toDouble()).toFloat() * 3.5f
                    val angleRad = ((baseAngle + sway) * PI / 180.0).toFloat()
                    val spread = 0.055f

                    val p1x = celX + cos(angleRad - spread) * beamLength
                    val p1y = celY + sin(angleRad - spread) * beamLength
                    val p2x = celX + cos(angleRad + spread) * beamLength
                    val p2y = celY + sin(angleRad + spread) * beamLength

                    val beamPath = Path().apply {
                        moveTo(celX, celY)
                        lineTo(p1x, p1y)
                        lineTo(p2x, p2y)
                        close()
                    }

                    drawPath(
                        path = beamPath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x1A818CF8),
                                Color(0x0EC7D2FE),
                                Color.Transparent
                            ),
                            center = Offset(celX, celY),
                            radius = beamLength
                        )
                    )
                }

                // 3. Moonlit Stardust / Luminous Celestial Dust
                val maxMotes = if (isPerformanceMode) 14 else 26
                if (celestialMoteParticles.size < maxMotes) {
                    val random = Random()
                    while (celestialMoteParticles.size < maxMotes) {
                        celestialMoteParticles.add(
                            SunMoteParticleData(
                                x = random.nextFloat() * width,
                                y = random.nextFloat() * height,
                                baseX = random.nextFloat() * width,
                                speedY = -8f - random.nextFloat() * 14f,
                                swaySpeed = 0.6f + random.nextFloat() * 1.2f,
                                swayOffset = random.nextFloat() * 6.28f,
                                radius = 1.8f + random.nextFloat() * 2.5f,
                                alpha = 0.25f + random.nextFloat() * 0.45f,
                                color = if (random.nextBoolean()) Color(0xFFC7D2FE) else Color(0xFFBAE6FD)
                            )
                        )
                    }
                }

                // 4. Night Fireflies (Kunang-Kunang)
                val targetFireflies = if (isPerformanceMode) 8 else 18
                if (fireflyParticles.size < targetFireflies) {
                    val random = Random()
                    while (fireflyParticles.size < targetFireflies) {
                        fireflyParticles.add(
                            FireflyParticleData(
                                x = random.nextFloat() * width,
                                y = height * 0.50f + random.nextFloat() * (height * 0.40f),
                                baseX = random.nextFloat() * width,
                                baseY = height * 0.50f + random.nextFloat() * (height * 0.40f),
                                speedX = 0.8f + random.nextFloat() * 1.5f,
                                speedY = 0.6f + random.nextFloat() * 1.2f,
                                phaseX = random.nextFloat() * 6.28f,
                                phaseY = random.nextFloat() * 6.28f,
                                pulseSpeed = 2.0f + random.nextFloat() * 2.5f,
                                pulsePhase = random.nextFloat() * 6.28f,
                                radius = 2.2f + random.nextFloat() * 1.8f,
                                color = if (random.nextBoolean()) Color(0xFFFDE047) else Color(0xFFA3E635)
                            )
                        )
                    }
                }

                // Render & Update Fireflies
                val deltaSec = 0.016f
                for (ff in fireflyParticles) {
                    ff.phaseX += ff.speedX * deltaSec
                    ff.phaseY += ff.speedY * deltaSec
                    ff.pulsePhase += ff.pulseSpeed * deltaSec

                    ff.x = ff.baseX + sin(ff.phaseX.toDouble()).toFloat() * 35f
                    ff.y = ff.baseY + cos(ff.phaseY.toDouble()).toFloat() * 22f

                    val pulseAlpha = (sin(ff.pulsePhase.toDouble()).toFloat() * 0.45f + 0.55f).coerceIn(0f, 1f)
                    // Firefly glowing aura
                    drawCircle(
                        color = ff.color.copy(alpha = pulseAlpha * 0.25f),
                        radius = ff.radius * 3.5f,
                        center = Offset(ff.x, ff.y)
                    )
                    // Firefly core
                    drawCircle(
                        color = Color(0xFFFFFFFF).copy(alpha = pulseAlpha),
                        radius = ff.radius,
                        center = Offset(ff.x, ff.y)
                    )
                }
            }

            // Update & Render Celestial Motes (Sun / Moon dust)
            val deltaSec = 0.016f
            for (mote in celestialMoteParticles) {
                mote.y += mote.speedY * deltaSec
                mote.swayOffset += mote.swaySpeed * deltaSec
                mote.x = mote.baseX + sin(mote.swayOffset.toDouble()).toFloat() * 25f

                // Wrap around bottom to top
                if (mote.y < -20f) {
                    mote.y = height + 20f
                    mote.baseX = Random().nextFloat() * width
                    mote.x = mote.baseX
                }

                val shimmer = (sin(mote.swayOffset * 2.0).toFloat() * 0.35f + 0.65f)
                drawCircle(
                    color = mote.color.copy(alpha = (mote.alpha * shimmer).coerceIn(0f, 1f)),
                    radius = mote.radius,
                    center = Offset(mote.x, mote.y)
                )
            }
        }

        // =====================================================================
        // 2. GENTLE RAIN & WATER SPLASH EFFECTS (Light Rain / Soothing Rain)
        // =====================================================================
        if (isRainy) {
            val isHeavy = weatherState == WeatherState.RAIN
            val targetDropCount = if (isPerformanceMode) {
                if (isHeavy) 45 else 22
            } else {
                if (isHeavy) 90 else 40
            }

            val random = Random()
            // Populate rain pool
            while (rainParticles.size < targetDropCount) {
                rainParticles.add(
                    RainParticleData(
                        x = random.nextFloat() * (width + 120f) - 60f,
                        y = random.nextFloat() * height,
                        speed = if (isHeavy) 550f + random.nextFloat() * 250f else 380f + random.nextFloat() * 180f,
                        length = if (isHeavy) 18f + random.nextFloat() * 14f else 10f + random.nextFloat() * 10f,
                        windAngle = 0.16f, // slight diagonal slant
                        alpha = if (isHeavy) 0.55f + random.nextFloat() * 0.35f else 0.35f + random.nextFloat() * 0.25f,
                        strokeWidth = if (isHeavy) 2.2f else 1.5f
                    )
                )
            }

            // Atmospheric Rain Sky Gradient
            val rainSkyHaze = if (isHeavy) Color(0x2E1E293B) else Color(0x18334155)
            drawRect(color = rainSkyHaze, size = size)

            val deltaSec = 0.016f
            val groundY = height * 0.78f

            // Update & Render Rain Drops
            val rainColor = Color(0xFFBAE6FD)
            for (drop in rainParticles) {
                val dx = sin(drop.windAngle.toDouble()).toFloat() * drop.speed * deltaSec
                val dy = cos(drop.windAngle.toDouble()).toFloat() * drop.speed * deltaSec

                drop.x += dx
                drop.y += dy

                val startX = drop.x
                val startY = drop.y
                val endX = drop.x + sin(drop.windAngle.toDouble()).toFloat() * drop.length
                val endY = drop.y + cos(drop.windAngle.toDouble()).toFloat() * drop.length

                drawLine(
                    color = rainColor.copy(alpha = drop.alpha),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = drop.strokeWidth
                )

                // Check ground contact -> trigger ripple splash
                if (drop.y >= groundY) {
                    if (splashParticles.size < 24 && random.nextFloat() < 0.40f) {
                        splashParticles.add(
                            SplashParticleData(
                                x = drop.x,
                                y = groundY + (random.nextFloat() * 25f - 10f),
                                radius = 2f,
                                maxRadius = 10f + random.nextFloat() * 8f,
                                alpha = 0.6f
                            )
                        )
                    }
                    // Reset to top
                    drop.y = -drop.length - (random.nextFloat() * 60f)
                    drop.x = random.nextFloat() * (width + 120f) - 60f
                }
            }

            // Update & Render Ground Splash Ripples
            val splashIterator = splashParticles.iterator()
            while (splashIterator.hasNext()) {
                val splash = splashIterator.next()
                splash.radius += 28f * deltaSec
                splash.alpha = (1.0f - (splash.radius / splash.maxRadius)).coerceIn(0f, 1f) * 0.5f

                if (splash.radius >= splash.maxRadius || splash.alpha <= 0.01f) {
                    splashIterator.remove()
                } else {
                    // Elliptical ground water ripple
                    drawOval(
                        color = Color(0xFF7DD3FC).copy(alpha = splash.alpha),
                        topLeft = Offset(splash.x - splash.radius, splash.y - splash.radius * 0.4f),
                        size = Size(splash.radius * 2f, splash.radius * 0.8f),
                        style = Stroke(width = 1.6f)
                    )
                }
            }
        }

        // =====================================================================
        // 3. FOG & MIST EFFECTS (Morning Mist / Overcast Clouds)
        // =====================================================================
        if (isFoggy) {
            val fogAlphaTarget = weatherState.fogAlpha
            if (fogBands.isEmpty()) {
                fogBands.add(FogBandData(yRatio = 0.60f, heightRatio = 0.18f, speed = 8f, offset = 0f))
                fogBands.add(FogBandData(yRatio = 0.72f, heightRatio = 0.22f, speed = -12f, offset = 0f))
                fogBands.add(FogBandData(yRatio = 0.40f, heightRatio = 0.15f, speed = 6f, offset = 0f))
            }

            val deltaSec = 0.016f
            for (fog in fogBands) {
                fog.offset += fog.speed * deltaSec
                val wave = sin((simulationTimeSeconds * 0.5f + fog.yRatio * 4f).toDouble()).toFloat() * 15f
                val fogY = height * fog.yRatio + wave
                val fogH = height * fog.heightRatio

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFE2E8F0).copy(alpha = fogAlphaTarget * 0.6f),
                            Color(0xFFCBD5E1).copy(alpha = fogAlphaTarget * 0.8f),
                            Color.Transparent
                        ),
                        startY = fogY - fogH * 0.5f,
                        endY = fogY + fogH * 0.5f
                    ),
                    topLeft = Offset(0f, fogY - fogH * 0.5f),
                    size = Size(width, fogH)
                )
            }
        }
    }
}

private data class RainParticleData(
    var x: Float,
    var y: Float,
    var speed: Float,
    var length: Float,
    var windAngle: Float,
    var alpha: Float,
    var strokeWidth: Float
)

private data class SplashParticleData(
    var x: Float,
    var y: Float,
    var radius: Float,
    var maxRadius: Float,
    var alpha: Float
)

private data class SunMoteParticleData(
    var x: Float,
    var y: Float,
    var baseX: Float,
    var speedY: Float,
    var swaySpeed: Float,
    var swayOffset: Float,
    var radius: Float,
    var alpha: Float,
    var color: Color
)

private data class FireflyParticleData(
    var x: Float,
    var y: Float,
    var baseX: Float,
    var baseY: Float,
    var speedX: Float,
    var speedY: Float,
    var phaseX: Float,
    var phaseY: Float,
    var pulseSpeed: Float,
    var pulsePhase: Float,
    var radius: Float,
    var color: Color
)

private data class FogBandData(
    val yRatio: Float,
    val heightRatio: Float,
    val speed: Float,
    var offset: Float
)

/**
 * Renders a delicate, low-opacity lens flare effect that appears when the Sun is at its peak
 * in the sky during clear weather, increasing the sense of depth, brightness, and optical atmosphere.
 */
private fun drawPeakSunLensFlare(
    drawScope: DrawScope,
    celX: Float,
    celY: Float,
    width: Float,
    height: Float,
    zenithFactor: Float,
    animTime: Float,
    pulse: Float
) {
    val centerX = width * 0.5f
    val centerY = height * 0.5f
    val vecX = centerX - celX
    val vecY = centerY - celY

    // 1. Soft Volumetric Light Beam linking Sun through viewport center
    val beamWidthStart = 24f
    val beamWidthEnd = width * 0.55f
    val beamPath = Path().apply {
        moveTo(celX - beamWidthStart, celY)
        lineTo(celX + beamWidthStart, celY)
        lineTo(centerX + vecX * 0.5f + beamWidthEnd, centerY + vecY * 0.5f + beamWidthEnd * 0.4f)
        lineTo(centerX + vecX * 0.5f - beamWidthEnd, centerY + vecY * 0.5f + beamWidthEnd * 0.4f)
        close()
    }
    drawScope.drawPath(
        path = beamPath,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFFDE7).copy(alpha = 0.07f * zenithFactor),
                Color(0xFFFEF08A).copy(alpha = 0.025f * zenithFactor),
                Color.Transparent
            ),
            start = Offset(celX, celY),
            end = Offset(centerX + vecX * 0.8f, centerY + vecY * 0.8f)
        )
    )

    // 2. Anamorphic Horizontal Golden Flare Streak across the Sun
    val streakWidth = width * 0.65f * pulse
    val streakHeight = 3.5f
    drawScope.drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF9C4).copy(alpha = 0.18f * zenithFactor),
                Color(0xFFFDE047).copy(alpha = 0.06f * zenithFactor),
                Color.Transparent
            ),
            center = Offset(celX, celY),
            radius = streakWidth * 0.5f
        ),
        topLeft = Offset(celX - streakWidth * 0.5f, celY - streakHeight * 0.5f),
        size = Size(streakWidth, streakHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(streakHeight * 0.5f, streakHeight * 0.5f)
    )

    // 3. Faint Rotating 4-Point Optical Needle Ray at Sun Core
    val needleRot = (animTime * 12f) % 360f
    drawScope.rotate(needleRot, Offset(celX, celY)) {
        val needleLen = 50f * pulse
        for (i in 0 until 2) {
            val angleRad = (i * 90f * PI / 180.0).toFloat()
            val p1x = celX + cos(angleRad) * needleLen
            val p1y = celY + sin(angleRad) * needleLen
            val p2x = celX - cos(angleRad) * needleLen
            val p2y = celY - sin(angleRad) * needleLen

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFFFFDE7).copy(alpha = 0.12f * zenithFactor),
                        Color.Transparent
                    ),
                    start = Offset(p1x, p1y),
                    end = Offset(p2x, p2y)
                ),
                start = Offset(p1x, p1y),
                end = Offset(p2x, p2y),
                strokeWidth = 1.6f
            )
        }
    }

    // 4. Circular Aperture / Iris Ring Halo along Optical Axis
    val irisRatio = 0.60f
    val irisX = celX + vecX * irisRatio
    val irisY = celY + vecY * irisRatio
    val irisRadius = 36f * pulse
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFF38BDF8).copy(alpha = 0.05f * zenithFactor),
                Color(0xFFFDE047).copy(alpha = 0.06f * zenithFactor),
                Color(0xFFF472B6).copy(alpha = 0.03f * zenithFactor),
                Color.Transparent
            ),
            center = Offset(irisX, irisY),
            radius = irisRadius
        ),
        radius = irisRadius,
        center = Offset(irisX, irisY)
    )

    // 5. Coaligned Chromatic Ghost Disks along the Optical Axis
    val ghostSpecs = listOf(
        GhostSpec(0.28f, 16f, 0.10f, Color(0xFFF59E0B)),  // Warm golden-amber iris ghost
        GhostSpec(0.48f, 10f, 0.08f, Color(0xFF10B981)),  // Delicate emerald aperture artifact
        GhostSpec(0.72f, 24f, 0.07f, Color(0xFF60A5FA)),  // Soft cyan/sky-blue ghost
        GhostSpec(1.10f, 14f, 0.08f, Color(0xFF8B5CF6)),  // Violet chromatic dispersion dot
        GhostSpec(1.35f, 20f, 0.09f, Color(0xFFFEF08A)),  // Warm golden reflection disc
        GhostSpec(1.60f, 48f, 0.04f, Color(0xFF38BDF8))   // Soft, expansive out-of-focus bokeh halo
    )

    for (ghost in ghostSpecs) {
        val gx = celX + vecX * ghost.ratio
        val gy = celY + vecY * ghost.ratio
        val effectiveAlpha = (ghost.alpha * zenithFactor).coerceIn(0f, 1f)

        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ghost.color.copy(alpha = effectiveAlpha),
                    ghost.color.copy(alpha = effectiveAlpha * 0.35f),
                    Color.Transparent
                ),
                center = Offset(gx, gy),
                radius = ghost.radius
            ),
            radius = ghost.radius,
            center = Offset(gx, gy)
        )
    }
}

private data class GhostSpec(
    val ratio: Float,
    val radius: Float,
    val alpha: Float,
    val color: Color
)

