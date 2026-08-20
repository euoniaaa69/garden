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
import com.example.domain.RandomEventManager
import com.example.model.LivePlantState
import com.example.model.PlantSpecies
import com.example.model.WeatherState
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PixelGardenCanvas(
    dayNightContext: DayNightContext,
    weatherState: WeatherState,
    species: PlantSpecies,
    livePlantState: LivePlantState,
    isPerformanceMode: Boolean,
    particleManager: ParticleManager,
    randomEventManager: RandomEventManager? = null,
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

        // Update spontaneous random event manager
        randomEventManager?.update(
            deltaSeconds = currentDeltaSeconds,
            width = canvasWidth,
            height = canvasHeight,
            isNight = dayNightContext.timeOfDay.isNight
        )

        val overcastDarkness = randomEventManager?.overcastFactor ?: 0f
        val suddenRainLevel = randomEventManager?.suddenRainFactor ?: 0f
        val effectiveRainCount = (weatherState.rainDropCount + (suddenRainLevel * 85).toInt())

        // Update continuous particle simulation
        particleManager.update(
            deltaSeconds = currentDeltaSeconds,
            width = canvasWidth,
            height = canvasHeight,
            targetRainCount = effectiveRainCount,
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
        // 2. Stars & Shooting Stars at Night/Dusk
        // ---------------------------------------------------------------------
        if (dayNightContext.starAlpha > 0.05f) {
            val starAlpha = dayNightContext.starAlpha
            // Procedural fixed star positions across multiple magnitudes
            val starPositions = listOf(
                Pair(0.10f, 0.08f), Pair(0.22f, 0.16f), Pair(0.36f, 0.07f),
                Pair(0.52f, 0.13f), Pair(0.70f, 0.08f), Pair(0.86f, 0.20f),
                Pair(0.16f, 0.30f), Pair(0.42f, 0.26f), Pair(0.66f, 0.32f),
                Pair(0.92f, 0.14f), Pair(0.05f, 0.22f), Pair(0.78f, 0.28f),
                Pair(0.28f, 0.04f), Pair(0.60f, 0.05f), Pair(0.46f, 0.18f),
                Pair(0.14f, 0.44f), Pair(0.84f, 0.40f), Pair(0.34f, 0.38f),
                Pair(0.58f, 0.42f), Pair(0.74f, 0.18f), Pair(0.03f, 0.12f)
            )

            // Constellation / Major Star clusters
            for ((idx, pos) in starPositions.withIndex()) {
                val twinkleSpeed = 1.8f + (idx % 5) * 0.4f
                val twinkle = (sin(fogDrift.toDouble() * twinkleSpeed + idx * 1.3).toFloat() * 0.4f + 0.6f) * starAlpha
                val sx = pos.first * canvasWidth
                val sy = pos.second * canvasHeight * 0.58f
                val starColor = Color(0xFFFFFFFF).copy(alpha = twinkle.coerceIn(0f, 1f))

                if (idx % 4 == 0) {
                    // 4-Point Diamond Sparkle Star (Major Beacon Star)
                    val starSize = 3.8f * (twinkle / starAlpha).coerceIn(0.5f, 1.2f)
                    val starPath = Path().apply {
                        moveTo(sx, sy - starSize)
                        lineTo(sx + starSize * 0.35f, sy)
                        lineTo(sx, sy + starSize)
                        lineTo(sx - starSize * 0.35f, sy)
                        close()
                    }
                    drawPath(starPath, starColor)

                    val crossPath = Path().apply {
                        moveTo(sx - starSize, sy)
                        lineTo(sx, sy - starSize * 0.35f)
                        lineTo(sx + starSize, sy)
                        lineTo(sx, sy + starSize * 0.35f)
                        close()
                    }
                    drawPath(crossPath, starColor)

                    // Subtle soft halo
                    drawCircle(
                        color = Color(0x33C7D2FE).copy(alpha = (twinkle * 0.4f).coerceIn(0f, 1f)),
                        radius = starSize * 2.2f,
                        center = Offset(sx, sy)
                    )
                } else {
                    // Fine round star
                    val radius = if (idx % 2 == 0) 2.2f else 1.4f
                    drawCircle(starColor, radius, Offset(sx, sy))
                }
            }

            // Procedural Shooting Star / Meteor (streaks occasionally across the upper night sky)
            if (dayNightContext.timeOfDay.isNight || dayNightContext.starAlpha > 0.4f) {
                val shootingCycle = (fogDrift * 0.35f) % 12f // 12 second loop
                if (shootingCycle < 1.8f) { // Active shooting phase for 1.8 seconds
                    val progress = shootingCycle / 1.8f
                    val startX = canvasWidth * 0.82f - progress * (canvasWidth * 0.55f)
                    val startY = canvasHeight * 0.04f + progress * (canvasHeight * 0.22f)
                    val tailLen = 65f * (1f - (progress - 0.7f).coerceAtLeast(0f) / 0.3f)

                    val trailAngle = (32f * Math.PI / 180.0).toFloat()
                    val tailX = startX + cos(trailAngle) * tailLen
                    val tailY = startY - sin(trailAngle) * tailLen

                    val streakAlpha = (sin(progress * Math.PI).toFloat() * starAlpha).coerceIn(0f, 1f)

                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF).copy(alpha = streakAlpha),
                                Color(0xFFBAE6FD).copy(alpha = streakAlpha * 0.7f),
                                Color.Transparent
                            ),
                            start = Offset(startX, startY),
                            end = Offset(tailX, tailY)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(tailX, tailY),
                        strokeWidth = 2.5f
                    )

                    // Glowing shooting star head
                    drawCircle(
                        color = Color(0xFFFFFFFF).copy(alpha = streakAlpha),
                        radius = 2.8f,
                        center = Offset(startX, startY)
                    )
                    drawCircle(
                        color = Color(0x6660A5FA).copy(alpha = streakAlpha * 0.6f),
                        radius = 6.0f,
                        center = Offset(startX, startY)
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // 3. Sun or Moon (Overhauled Celestial Renderer)
        // ---------------------------------------------------------------------
        val celX = dayNightContext.sunMoonX * canvasWidth
        val celY = dayNightContext.sunMoonY * canvasHeight * 0.55f
        PixelArtDrawers.drawCelestialBody(
            drawScope = this,
            cx = celX,
            cy = celY,
            isSun = dayNightContext.isSun,
            ambientLight = dayNightContext.ambientLight,
            timeOfDay = dayNightContext.timeOfDay,
            animTime = fogDrift
        )

        // ---------------------------------------------------------------------
        // 4. Distant Mountain Silhouettes (Gunung Siluet di Belakang Pohon Bambu)
        // ---------------------------------------------------------------------
        val horizonY = canvasHeight * 0.60f
        PixelArtDrawers.drawMountainSilhouettes(
            drawScope = this,
            width = canvasWidth,
            height = canvasHeight,
            horizonY = horizonY,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight,
            fogAlpha = weatherState.fogAlpha
        )

        // ---------------------------------------------------------------------
        // 5. Dense Lush Bamboo Grove (Pohon Bambu Banyak & Rimbun)
        // ---------------------------------------------------------------------
        val groundY = canvasHeight * 0.70f
        PixelArtDrawers.drawDenseBambooForest(
            drawScope = this,
            width = canvasWidth,
            baseY = groundY + 10f,
            windSway = windSway,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight
        )

        // ---------------------------------------------------------------------
        // 6. Traditional Rural Village Hut (Gubuk Desa / Saung Bambu)
        // ---------------------------------------------------------------------
        val hutX = canvasWidth * 0.28f
        PixelArtDrawers.drawVillageHut(
            drawScope = this,
            hutCenterX = hutX,
            groundY = groundY - 4f,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight,
            animTime = fogDrift
        )

        // ---------------------------------------------------------------------
        // 7. Flying Birds & Spontaneous Bird Flocks (Kawanan Burung Berterbangan)
        // ---------------------------------------------------------------------
        if (!dayNightContext.timeOfDay.isNight && weatherState != WeatherState.RAIN && suddenRainLevel < 0.3f) {
            val birdY = canvasHeight * 0.22f + sin(birdFlyX * 0.01).toFloat() * 15f
            val wingPhase = birdFlyX * 0.15f
            val birdColor = if (dayNightContext.ambientLight > 0.6f) Color(0x66263238) else Color(0x44FFFFFF)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX, birdY, wingPhase, birdColor)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX - 28f, birdY + 12f, wingPhase + 0.6f, birdColor)
            PixelArtDrawers.drawFlyingBird(this, birdFlyX - 52f, birdY - 8f, wingPhase + 1.2f, birdColor)
        }

        // Spontaneous bird flock event across mountains & sky
        randomEventManager?.flockBirds?.forEach { flockBird ->
            PixelArtDrawers.drawFlockBird(
                drawScope = this,
                bird = flockBird,
                isNight = dayNightContext.timeOfDay.isNight
            )
        }

        // ---------------------------------------------------------------------
        // 8. Drifting Clouds (Enhanced with Overcast Darkening)
        // ---------------------------------------------------------------------
        val cloudTintFinal = if (overcastDarkness > 0.1f) {
            Color(0xFF37474F).copy(alpha = 0.85f * overcastDarkness)
        } else {
            dayNightContext.cloudTint
        }
        for (cloud in particleManager.clouds) {
            PixelArtDrawers.drawPixelCloud(
                drawScope = this,
                x = cloud.x,
                y = cloud.y,
                w = cloud.width,
                h = cloud.height,
                tint = cloudTintFinal,
                alpha = (cloud.alpha + overcastDarkness * 0.4f).coerceIn(0f, 1f) * (if (weatherState == WeatherState.CLOUDY || weatherState == WeatherState.RAIN || overcastDarkness > 0.3f) 0.95f else 0.5f)
            )
        }

        // ---------------------------------------------------------------------
        // 9. Rural Dirt Road & Earthen Terrain (Jalan Tanah Pedesaan & Rumput)
        // ---------------------------------------------------------------------
        PixelArtDrawers.drawRuralDirtRoadAndGround(
            drawScope = this,
            width = canvasWidth,
            height = canvasHeight,
            groundY = groundY,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight,
            windSway = windSway
        )

        // ---------------------------------------------------------------------
        // 9b. Walking Villagers on Dirt Road (Orang-Orang Desa Berlalu Lalang)
        // ---------------------------------------------------------------------
        randomEventManager?.villagers?.forEach { villager ->
            PixelArtDrawers.drawVillager(
                drawScope = this,
                villager = villager,
                isNight = dayNightContext.timeOfDay.isNight,
                ambientLight = dayNightContext.ambientLight
            )
        }

        // ---------------------------------------------------------------------
        // 10. Rustic Wooden Garden Deck / Pedestal for Plant
        // ---------------------------------------------------------------------
        val deckColor = if (dayNightContext.timeOfDay.isNight) Color(0xFF2A1C16) else Color(0xFF5D4037)
        val deckHighlight = if (dayNightContext.timeOfDay.isNight) Color(0xFF38261F) else Color(0xFF795548)
        val deckLeft = canvasWidth * 0.14f
        val deckWidth = canvasWidth * 0.72f
        val deckTop = groundY + 18f
        val deckHeight = 44f

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
        // 11. Japanese / Village Lantern (Lentera Desa)
        // ---------------------------------------------------------------------
        val lanternX = canvasWidth * 0.82f
        PixelArtDrawers.drawStoneLantern(
            drawScope = this,
            x = lanternX,
            groundY = groundY + 28f,
            isNight = dayNightContext.timeOfDay.isNight,
            ambientLight = dayNightContext.ambientLight
        )

        // ---------------------------------------------------------------------
        // 12. Plant Pot & Live Growing Plant
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
        // 11. Weather Effects (Rain & Splashes, including Sudden Rain Event)
        // ---------------------------------------------------------------------
        if (effectiveRainCount > 0) {
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
        val effectiveFogAlpha = (weatherState.fogAlpha + overcastDarkness * 0.15f).coerceIn(0f, 0.7f)
        if (effectiveFogAlpha > 0.02f) {
            for (f in 0..3) {
                val fy = groundY - 40f + f * 35f + sin(fogDrift + f).toFloat() * 10f
                val fogBrush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFFECEFF1).copy(alpha = effectiveFogAlpha),
                        Color(0xFFECEFF1).copy(alpha = effectiveFogAlpha * 1.3f),
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
        // 14. Weather Atmospheric Tint Overlay & Dark Overcast Effect (Mendung Gelap)
        // ---------------------------------------------------------------------
        if (weatherState.skyTint != Color.Transparent) {
            drawRect(weatherState.skyTint, size = size)
        }

        // Spontaneous Dark Overcast Shadow
        if (overcastDarkness > 0.05f) {
            val overcastColor = Color(0xFF1E293B).copy(alpha = overcastDarkness * 0.65f)
            drawRect(overcastColor, size = size)

            // Deep stormy sky top vignette
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xCC0F172A).copy(alpha = overcastDarkness * 0.75f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = canvasHeight * 0.45f
                ),
                size = size
            )
        }
    }
}
