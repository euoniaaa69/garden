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
import com.example.model.WeatherState
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance, Canvas-based atmospheric weather particle overlay.
 * Renders gentle rain streaks with ground splashes, or vibrant golden sunshine
 * with radiant sunbeams and shimmering floating sun motes based on the active WeatherState.
 */
@Composable
fun WeatherParticleOverlay(
    weatherState: WeatherState,
    modifier: Modifier = Modifier,
    isPerformanceMode: Boolean = false,
    sunPositionXRatio: Float = 0.5f,
    sunPositionYRatio: Float = 0.22f
) {
    // 60 FPS animation ticker
    var frameTimeNanos by remember { mutableLongStateOf(0L) }
    var simulationTimeSeconds by remember { mutableFloatStateOf(0f) }

    // Sunshine breathing & rotation animators
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

    val sunRayRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 48000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SunRayRotationAnim"
    )

    // Particle state storage
    val rainParticles = remember { mutableStateListOf<RainParticleData>() }
    val splashParticles = remember { mutableStateListOf<SplashParticleData>() }
    val sunMoteParticles = remember { mutableStateListOf<SunMoteParticleData>() }
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
        val isSunny = weatherState == WeatherState.CLEAR
        val isFoggy = weatherState == WeatherState.FOG || weatherState == WeatherState.CLOUDY

        // =====================================================================
        // 1. SUNSHINE & SUNBEAM EFFECTS (Clear Skies / Sunshine)
        // =====================================================================
        if (isSunny || weatherState == WeatherState.CLEAR) {
            val sunX = width * sunPositionXRatio
            val sunY = height * sunPositionYRatio
            val baseRadius = width * 0.45f * sunPulse

            // 1a. Radiant Sun Corona / Ambient Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x35FFF9C4),
                        Color(0x18FDE047),
                        Color(0x08F59E0B),
                        Color.Transparent
                    ),
                    center = Offset(sunX, sunY),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = Offset(sunX, sunY)
            )

            // 1b. Rotating Golden God Rays / Sunbeams
            val rayCount = if (isPerformanceMode) 6 else 10
            val rayLength = width * 0.9f
            for (i in 0 until rayCount) {
                val angleDeg = sunRayRotation + (360f / rayCount) * i
                val angleRad = (angleDeg * PI / 180.0).toFloat()
                val spread = 0.08f

                val p1x = sunX + cos(angleRad - spread) * rayLength
                val p1y = sunY + sin(angleRad - spread) * rayLength
                val p2x = sunX + cos(angleRad + spread) * rayLength
                val p2y = sunY + sin(angleRad + spread) * rayLength

                val rayPath = Path().apply {
                    moveTo(sunX, sunY)
                    lineTo(p1x, p1y)
                    lineTo(p2x, p2y)
                    close()
                }

                drawPath(
                    path = rayPath,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x22FFF59D),
                            Color(0x0CFFD54F),
                            Color.Transparent
                        ),
                        center = Offset(sunX, sunY),
                        radius = rayLength
                    )
                )
            }

            // 1c. Warm Golden Sun Dust / Pollen Motes
            val maxMotes = if (isPerformanceMode) 16 else 32
            if (sunMoteParticles.size < maxMotes) {
                val random = Random()
                while (sunMoteParticles.size < maxMotes) {
                    sunMoteParticles.add(
                        SunMoteParticleData(
                            x = random.nextFloat() * width,
                            y = random.nextFloat() * height,
                            baseX = random.nextFloat() * width,
                            speedY = -12f - random.nextFloat() * 18f,
                            swaySpeed = 0.8f + random.nextFloat() * 1.5f,
                            swayOffset = random.nextFloat() * 6.28f,
                            radius = 2.0f + random.nextFloat() * 3.0f,
                            alpha = 0.3f + random.nextFloat() * 0.5f,
                            color = if (random.nextBoolean()) Color(0xFFFFF59D) else Color(0xFFFFD54F)
                        )
                    )
                }
            }

            // Update & Render Sun Motes
            val deltaSec = 0.016f
            for (mote in sunMoteParticles) {
                mote.y += mote.speedY * deltaSec
                mote.swayOffset += mote.swaySpeed * deltaSec
                mote.x = mote.baseX + sin(mote.swayOffset.toDouble()).toFloat() * 25f

                // Wrap around bottom to top
                if (mote.y < -20f) {
                    mote.y = height + 20f
                    mote.baseX = Random().nextFloat() * width
                    mote.x = mote.baseX
                }

                val shimmer = (sin(mote.swayOffset * 2.0).toFloat() * 0.3f + 0.7f)
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

private data class FogBandData(
    val yRatio: Float,
    val heightRatio: Float,
    val speed: Float,
    var offset: Float
)
