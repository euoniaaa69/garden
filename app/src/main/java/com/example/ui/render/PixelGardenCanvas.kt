package com.example.ui.render

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.domain.DayNightContext
import com.example.domain.ParticleManager
import com.example.model.LivePlantState
import com.example.model.PlantSpecies
import com.example.model.WeatherState
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun PixelGardenCanvas(
    dayNightContext: DayNightContext,
    weatherState: WeatherState,
    species: PlantSpecies,
    livePlantState: LivePlantState,
    isPerformanceMode: Boolean,
    particleManager: ParticleManager,
    modifier: Modifier = Modifier,
    onPlantTapped: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val plantBounceAnim = remember { Animatable(1.0f) }

    // Infinite breathing/wind transition for gentle ambient motion
    val infiniteTransition = rememberInfiniteTransition(label = "GardenWind")
    val windSway by infiniteTransition.animateFloat(
        initialValue = -0.06f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WindSway"
    )

    val birdFlyX by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BirdFly"
    )

    val fogDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FogDrift"
    )

    var lastFrameTimeNanos by remember { mutableLongStateOf(0L) }
    var currentDeltaSeconds by remember { mutableFloatStateOf(0.016f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameTimeNanos > 0L) {
                    val delta = (frameTimeNanos - lastFrameTimeNanos) / 1_000_000_000.0f
                    currentDeltaSeconds = delta.coerceIn(0.001f, 0.05f)
                }
                lastFrameTimeNanos = frameTimeNanos
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("pixel_garden_canvas")
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Trigger plant bounce on tap
                    coroutineScope.launch {
                        plantBounceAnim.animateTo(1.12f, tween(120, easing = FastOutSlowInEasing))
                        plantBounceAnim.animateTo(1.0f, tween(240, easing = FastOutSlowInEasing))
                    }
                    onPlantTapped()
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Update continuous particle simulation
        particleManager.update(
            deltaSeconds = currentDeltaSeconds,
            width = canvasWidth,
            height = canvasHeight,
            targetRainCount = weatherState.rainDropCount,
            targetFireflyCount = dayNightContext.fireflyCount,
            isPerformanceMode = isPerformanceMode
        )

        // ---------------------------------------------------------------------
        // 1. Sky Gradient Background with Artistic Atmospheric Nebulae
        // ---------------------------------------------------------------------
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    dayNightContext.skyTopColor,
                    dayNightContext.skyBottomColor,
                    dayNightContext.horizonGlowColor
                ),
                startY = 0f,
                endY = canvasHeight * 0.85f
            ),
            size = size
        )

        // Artistic Ambient Glowing Aurora Orbs (Indigo & Purple depth nebulae)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x3D6366F1), Color(0x154F46E5), Color.Transparent),
                center = Offset(canvasWidth * 0.22f, canvasHeight * 0.20f),
                radius = canvasWidth * 0.60f
            ),
            radius = canvasWidth * 0.60f,
            center = Offset(canvasWidth * 0.22f, canvasHeight * 0.20f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x357E22CE), Color(0x12581C87), Color.Transparent),
                center = Offset(canvasWidth * 0.80f, canvasHeight * 0.48f),
                radius = canvasWidth * 0.55f
            ),
            radius = canvasWidth * 0.55f,
            center = Offset(canvasWidth * 0.80f, canvasHeight * 0.48f)
        )

        // ---------------------------------------------------------------------
        // 2. Stars at Night/Dusk
        // ---------------------------------------------------------------------
        if (dayNightContext.starAlpha > 0.05f) {
            val starAlpha = dayNightContext.starAlpha
            // Procedural fixed star positions
            val starPositions = listOf(
                Pair(0.12f, 0.10f), Pair(0.25f, 0.18f), Pair(0.38f, 0.08f),
                Pair(0.55f, 0.14f), Pair(0.72f, 0.09f), Pair(0.85f, 0.22f),
                Pair(0.18f, 0.32f), Pair(0.44f, 0.28f), Pair(0.68f, 0.34f),
                Pair(0.92f, 0.15f), Pair(0.06f, 0.24f), Pair(0.79f, 0.29f),
                Pair(0.30f, 0.04f), Pair(0.62f, 0.05f), Pair(0.48f, 0.19f)
            )
            for ((idx, pos) in starPositions.withIndex()) {
                val twinkle = (sin(fogDrift.toDouble() * 2.0 + idx).toFloat() * 0.3f + 0.7f) * starAlpha
                val starColor = Color(0xFFFFFFFF).copy(alpha = twinkle.coerceIn(0f, 1f))
                val sx = pos.first * canvasWidth
                val sy = pos.second * canvasHeight * 0.6f
                drawCircle(starColor, if (idx % 3 == 0) 2.2f else 1.5f, Offset(sx, sy))
            }
        }

        // ---------------------------------------------------------------------
        // 3. Sun or Moon
        // ---------------------------------------------------------------------
        val celX = dayNightContext.sunMoonX * canvasWidth
        val celY = dayNightContext.sunMoonY * canvasHeight * 0.55f
        PixelArtDrawers.drawCelestialBody(
            drawScope = this,
            cx = celX,
            cy = celY,
            isSun = dayNightContext.isSun,
            ambientLight = dayNightContext.ambientLight
        )

        // ---------------------------------------------------------------------
        // 4. Distant Parallax Mountain Silhouettes
        // ---------------------------------------------------------------------
        val horizonY = canvasHeight * 0.62f
        val mountainFarColor = if (dayNightContext.timeOfDay.isNight) Color(0xFF101928) else Color(0xFF6B8299)
        val mountainNearColor = if (dayNightContext.timeOfDay.isNight) Color(0xFF0A111C) else Color(0xFF475B6E)

        val farMountainPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(canvasWidth * 0.15f, horizonY - 80f)
            lineTo(canvasWidth * 0.35f, horizonY - 30f)
            lineTo(canvasWidth * 0.58f, horizonY - 105f)
            lineTo(canvasWidth * 0.82f, horizonY - 45f)
            lineTo(canvasWidth, horizonY - 70f)
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }
        drawPath(farMountainPath, mountainFarColor.copy(alpha = 0.5f))

        val nearMountainPath = Path().apply {
            moveTo(0f, horizonY + 20f)
            lineTo(canvasWidth * 0.28f, horizonY - 55f)
            lineTo(canvasWidth * 0.50f, horizonY - 15f)
            lineTo(canvasWidth * 0.75f, horizonY - 65f)
            lineTo(canvasWidth, horizonY - 25f)
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }
        drawPath(nearMountainPath, mountainNearColor.copy(alpha = 0.75f))

        // ---------------------------------------------------------------------
        // 5. Flying Birds
        // ---------------------------------------------------------------------
        if (!dayNightContext.timeOfDay.isNight && weatherState != WeatherState.RAIN) {
            val birdY = canvasHeight * 0.24f + sin(birdFlyX * 0.01).toFloat() * 15f
            val wingPhase = birdFlyX * 0.15f
            val birdColor = if (dayNightContext.ambientLight > 0.6f) Color(0x66263238) else Color(0x44FFFFFF)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX, birdY, wingPhase, birdColor)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX - 28f, birdY + 12f, wingPhase + 0.6f, birdColor)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX - 52f, birdY - 8f, wingPhase + 1.2f, birdColor)
        }

        // ---------------------------------------------------------------------
        // 6. Drifting Clouds
        // ---------------------------------------------------------------------
        for (cloud in particleManager.clouds) {
            PixelArtDrawers.drawPixelCloud(
                drawScope = this,
                x = cloud.x,
                y = cloud.y,
                w = cloud.width,
                h = cloud.height,
                tint = dayNightContext.cloudTint,
                alpha = cloud.alpha * (if (weatherState == WeatherState.CLOUDY || weatherState == WeatherState.RAIN) 0.9f else 0.5f)
            )
        }

        // ---------------------------------------------------------------------
        // 7. Garden Flooring & Wooden Decking (Engawa)
        // ---------------------------------------------------------------------
        val groundY = canvasHeight * 0.72f
        val grassColor = if (dayNightContext.timeOfDay.isNight) Color(0xFF13231A) else Color(0xFF335C3E)
        val deckColor = if (dayNightContext.timeOfDay.isNight) Color(0xFF2A1C16) else Color(0xFF5D4037)
        val deckHighlight = if (dayNightContext.timeOfDay.isNight) Color(0xFF38261F) else Color(0xFF795548)

        // Lush grass lawn
        drawRect(grassColor, Offset(0f, groundY), Size(canvasWidth, canvasHeight - groundY))

        // Gentle grass blade tufts
        for (i in 0..12) {
            val gx = (canvasWidth / 12f) * i + (i % 3) * 10f
            val gy = groundY + 15f + (i % 4) * 12f
            val swayX = gx + windSway * 180f
            drawLine(
                color = if (dayNightContext.timeOfDay.isNight) Color(0xFF1F3D2C) else Color(0xFF4E7C59),
                start = Offset(gx, gy),
                end = Offset(swayX, gy - 12f),
                strokeWidth = 2.5f
            )
        }

        // Japanese wooden veranda deck / garden pedestal
        val deckLeft = canvasWidth * 0.12f
        val deckWidth = canvasWidth * 0.76f
        val deckTop = groundY + 18f
        val deckHeight = 48f

        // Deck shadow
        drawOval(Color(0x44000000), Offset(deckLeft - 10f, deckTop + deckHeight - 8f), Size(deckWidth + 20f, 18f))

        // Wooden deck base
        drawRoundRect(deckColor, Offset(deckLeft, deckTop), Size(deckWidth, deckHeight), CornerRadius(6f, 6f))

        // Deck planks lines
        val plankCount = 6
        for (p in 1 until plankCount) {
            val px = deckLeft + (deckWidth / plankCount) * p
            drawLine(Color(0x33000000), Offset(px, deckTop), Offset(px, deckTop + deckHeight), strokeWidth = 2f)
        }
        // Deck top edge highlight
        drawLine(deckHighlight, Offset(deckLeft, deckTop + 2f), Offset(deckLeft + deckWidth, deckTop + 2f), strokeWidth = 3f)

        // ---------------------------------------------------------------------
        // 8. Japanese Stone Lantern (Tōrō)
        // ---------------------------------------------------------------------
        val lanternX = canvasWidth * 0.20f
        PixelArtDrawers.drawStoneLantern(
            drawScope = this,
            x = lanternX,
            groundY = groundY + 28f,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight
        )

        // ---------------------------------------------------------------------
        // 9. Plant Pot & Live Growing Plant
        // ---------------------------------------------------------------------
        val plantCenterX = canvasWidth * 0.50f
        val potBottomY = deckTop + 14f
        val potWidth = 140f
        val potHeight = 85f

        // Draw Ceramic Pot
        PixelArtDrawers.drawGardenPot(
            drawScope = this,
            centerX = plantCenterX,
            bottomY = potBottomY,
            potWidth = potWidth,
            potHeight = potHeight,
            potStyle = "ceramic",
            hydration = livePlantState.hydrationLevel
        )

        // Draw Active Live Plant
        val soilCenterY = potBottomY - potHeight + 8f
        PixelArtDrawers.drawPlant(
            drawScope = this,
            speciesId = species.id,
            stage = livePlantState.stage,
            progressInStage = livePlantState.progressInStage,
            soilCenterX = plantCenterX,
            soilCenterY = soilCenterY,
            swayAngle = windSway,
            bounceScale = plantBounceAnim.value
        )

        // ---------------------------------------------------------------------
        // 10. Watering Particle Waterfall
        // ---------------------------------------------------------------------
        for (wp in particleManager.wateringParticles) {
            val pAlpha = (1.0f - (wp.life / wp.maxLife)).coerceIn(0f, 1f)
            drawCircle(Color(0xFF4FC3F7).copy(alpha = pAlpha), 3f, Offset(wp.x, wp.y))
        }

        // ---------------------------------------------------------------------
        // 11. Weather Effects (Rain & Splashes)
        // ---------------------------------------------------------------------
        if (weatherState.rainDropCount > 0) {
            for (drop in particleManager.rainDrops) {
                drawLine(
                    color = Color(0xFFB0BEC5).copy(alpha = drop.alpha),
                    start = Offset(drop.x, drop.y),
                    end = Offset(drop.x + drop.length * 0.15f, drop.y + drop.length),
                    strokeWidth = 1.8f
                )
            }

            for (splash in particleManager.rainSplashes) {
                drawCircle(
                    color = Color(0xFFB0BEC5).copy(alpha = splash.alpha),
                    radius = splash.radius,
                    center = Offset(splash.x, splash.y),
                    style = Stroke(1.2f)
                )
            }
        }

        // ---------------------------------------------------------------------
        // 12. Weather Effects (Fog Mist Bands)
        // ---------------------------------------------------------------------
        if (weatherState.fogAlpha > 0.02f) {
            val fogAlpha = weatherState.fogAlpha
            for (f in 0..3) {
                val fy = groundY - 40f + f * 35f + sin(fogDrift + f).toFloat() * 10f
                val fogBrush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFFECEFF1).copy(alpha = fogAlpha),
                        Color(0xFFECEFF1).copy(alpha = fogAlpha * 1.3f),
                        Color.Transparent
                    )
                )
                drawRect(fogBrush, Offset(0f, fy), Size(canvasWidth, 45f))
            }
        }

        // ---------------------------------------------------------------------
        // 13. Drifting Petals & Night Fireflies
        // ---------------------------------------------------------------------
        for (petal in particleManager.petals) {
            drawCircle(petal.color.copy(alpha = petal.alpha), petal.size / 2f, Offset(petal.x, petal.y))
        }

        for (ff in particleManager.fireflies) {
            if (ff.alpha > 0.05f) {
                // Firefly glow halo
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0x99FFFF8D), Color(0x33C6FF00), Color.Transparent),
                        center = Offset(ff.x, ff.y),
                        radius = ff.size * 3.5f
                    ),
                    radius = ff.size * 3.5f,
                    center = Offset(ff.x, ff.y)
                )
                // Firefly core
                drawCircle(Color(0xFFFFFFB2).copy(alpha = ff.alpha), ff.size * 0.7f, Offset(ff.x, ff.y))
            }
        }

        // ---------------------------------------------------------------------
        // 14. Weather Atmospheric Tint Overlay
        // ---------------------------------------------------------------------
        if (weatherState.skyTint != Color.Transparent) {
            drawRect(weatherState.skyTint, size = size)
        }
    }
}
