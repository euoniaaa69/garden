package com.example.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ultra-lightweight particle structures for smooth 60 FPS 2D canvas rendering.
 */
data class RainDrop(
    var x: Float,
    var y: Float,
    var speed: Float,
    var length: Float,
    var alpha: Float
)

data class RainSplash(
    var x: Float,
    var y: Float,
    var radius: Float,
    var maxRadius: Float,
    var alpha: Float,
    var active: Boolean
)

data class CloudParticle(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var speed: Float,
    var alpha: Float,
    var seed: Long
)

data class FireflyParticle(
    var x: Float,
    var y: Float,
    var baseX: Float,
    var baseY: Float,
    var phase: Float,
    var glowSpeed: Float,
    var size: Float,
    var alpha: Float
)

data class PetalParticle(
    var x: Float,
    var y: Float,
    var speedY: Float,
    var swaySpeed: Float,
    var swayOffset: Float,
    var rotation: Float,
    var size: Float,
    var color: Color,
    var alpha: Float
)

data class WaterDropletParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    var maxLife: Float
)

class ParticleManager {
    private val random = Random()

    val rainDrops = mutableListOf<RainDrop>()
    val rainSplashes = mutableListOf<RainSplash>()
    val clouds = mutableListOf<CloudParticle>()
    val fireflies = mutableListOf<FireflyParticle>()
    val petals = mutableListOf<PetalParticle>()
    val wateringParticles = mutableListOf<WaterDropletParticle>()

    private var initialized = false

    fun initialize(width: Float, height: Float) {
        if (initialized || width <= 0 || height <= 0) return
        initialized = true

        // Init clouds
        clouds.clear()
        for (i in 0..7) {
            clouds.add(
                CloudParticle(
                    x = random.nextFloat() * width,
                    y = height * (0.05f + random.nextFloat() * 0.35f),
                    width = 120f + random.nextFloat() * 180f,
                    height = 40f + random.nextFloat() * 50f,
                    speed = 10f + random.nextFloat() * 20f,
                    alpha = 0.4f + random.nextFloat() * 0.4f,
                    seed = random.nextLong()
                )
            )
        }

        // Init Fireflies
        fireflies.clear()
        for (i in 0..25) {
            val bx = random.nextFloat() * width
            val by = height * (0.45f + random.nextFloat() * 0.45f)
            fireflies.add(
                FireflyParticle(
                    x = bx,
                    y = by,
                    baseX = bx,
                    baseY = by,
                    phase = random.nextFloat() * 6.28f,
                    glowSpeed = 1.2f + random.nextFloat() * 2.0f,
                    size = 3f + random.nextFloat() * 4f,
                    alpha = 0f
                )
            )
        }

        // Init gentle floating petals / pollen
        petals.clear()
        for (i in 0..12) {
            petals.add(
                PetalParticle(
                    x = random.nextFloat() * width,
                    y = random.nextFloat() * height,
                    speedY = 15f + random.nextFloat() * 25f,
                    swaySpeed = 1.0f + random.nextFloat() * 2.0f,
                    swayOffset = random.nextFloat() * 6.28f,
                    rotation = random.nextFloat() * 360f,
                    size = 5f + random.nextFloat() * 6f,
                    color = if (random.nextBoolean()) Color(0xFFFFCDD2) else Color(0xFFC8E6C9),
                    alpha = 0.5f + random.nextFloat() * 0.4f
                )
            )
        }
    }

    fun update(
        deltaSeconds: Float,
        width: Float,
        height: Float,
        targetRainCount: Int,
        targetFireflyCount: Int,
        isPerformanceMode: Boolean
    ) {
        if (width <= 0 || height <= 0) return
        if (!initialized) initialize(width, height)

        val actualRainCount = if (isPerformanceMode) targetRainCount / 2 else targetRainCount
        val actualFireflyCount = if (isPerformanceMode) targetFireflyCount / 2 else targetFireflyCount

        // 1. Update Clouds
        for (cloud in clouds) {
            cloud.x += cloud.speed * deltaSeconds
            if (cloud.x > width + cloud.width) {
                cloud.x = -cloud.width - 50f
                cloud.y = height * (0.05f + random.nextFloat() * 0.35f)
            }
        }

        // 2. Update Rain Drops
        while (rainDrops.size < actualRainCount) {
            rainDrops.add(
                RainDrop(
                    x = random.nextFloat() * width,
                    y = random.nextFloat() * height,
                    speed = 450f + random.nextFloat() * 350f,
                    length = 16f + random.nextFloat() * 24f,
                    alpha = 0.35f + random.nextFloat() * 0.45f
                )
            )
        }
        while (rainDrops.size > actualRainCount) {
            rainDrops.removeAt(rainDrops.size - 1)
        }

        val groundY = height * 0.85f
        val dropIterator = rainDrops.iterator()
        while (dropIterator.hasNext()) {
            val drop = dropIterator.next()
            drop.y += drop.speed * deltaSeconds
            drop.x += (drop.speed * 0.15f) * deltaSeconds // Slight wind angle

            if (drop.y >= groundY) {
                // Spawn small ripple splash
                if (rainSplashes.size < 20 && random.nextFloat() < 0.4f) {
                    rainSplashes.add(
                        RainSplash(
                            x = drop.x,
                            y = groundY + (random.nextFloat() * 40f - 20f),
                            radius = 2f,
                            maxRadius = 8f + random.nextFloat() * 10f,
                            alpha = 0.5f,
                            active = true
                        )
                    )
                }
                drop.y = -drop.length - (random.nextFloat() * 100f)
                drop.x = random.nextFloat() * (width + 100f) - 50f
            }
        }

        // 3. Update Splashes
        val splashIterator = rainSplashes.iterator()
        while (splashIterator.hasNext()) {
            val splash = splashIterator.next()
            splash.radius += 25f * deltaSeconds
            splash.alpha = (1.0f - (splash.radius / splash.maxRadius)).coerceIn(0f, 1f) * 0.5f
            if (splash.radius >= splash.maxRadius || splash.alpha <= 0f) {
                splashIterator.remove()
            }
        }

        // 4. Update Fireflies
        for (i in fireflies.indices) {
            val ff = fireflies[i]
            if (i < actualFireflyCount) {
                ff.phase += ff.glowSpeed * deltaSeconds
                ff.alpha = (sin(ff.phase.toDouble()).toFloat() * 0.5f + 0.5f).coerceIn(0f, 1f)
                ff.x = ff.baseX + sin(ff.phase * 0.6).toFloat() * 30f
                ff.y = ff.baseY + cos(ff.phase * 0.4).toFloat() * 20f

                // Wrap around edges
                if (ff.baseX < 0) ff.baseX = width
                if (ff.baseX > width) ff.baseX = 0f
            } else {
                ff.alpha = 0f
            }
        }

        // 5. Update Floating Petals
        for (petal in petals) {
            petal.y += petal.speedY * deltaSeconds
            petal.swayOffset += petal.swaySpeed * deltaSeconds
            petal.x += sin(petal.swayOffset.toDouble()).toFloat() * 20f * deltaSeconds
            petal.rotation += 15f * deltaSeconds

            if (petal.y > height + 20f) {
                petal.y = -20f
                petal.x = random.nextFloat() * width
            }
        }

        // 6. Update Watering particles
        val waterIterator = wateringParticles.iterator()
        while (waterIterator.hasNext()) {
            val wp = waterIterator.next()
            wp.life += deltaSeconds
            wp.x += wp.vx * deltaSeconds
            wp.y += wp.vy * deltaSeconds
            wp.vy += 400f * deltaSeconds // gravity
            if (wp.life >= wp.maxLife) {
                waterIterator.remove()
            }
        }
    }

    fun triggerWateringEffect(plantCenterX: Float, plantCenterY: Float) {
        for (i in 0..30) {
            wateringParticles.add(
                WaterDropletParticle(
                    x = plantCenterX + (random.nextFloat() * 60f - 30f),
                    y = plantCenterY - 120f + (random.nextFloat() * 20f),
                    vx = (random.nextFloat() * 80f - 40f),
                    vy = 120f + random.nextFloat() * 150f,
                    life = 0f,
                    maxLife = 0.5f + random.nextFloat() * 0.4f
                )
            )
        }
    }
}
