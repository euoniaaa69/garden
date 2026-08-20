package com.example.domain

import androidx.compose.ui.graphics.Color
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Types of random natural events in the village environment.
 */
enum class RandomEventType(val label: String) {
    BIRD_FLOCK("Kawanan Burung"),
    WALKING_VILLAGERS("Warga Berlalu Lalang"),
    SUDDEN_RAIN("Hujan Mendadak"),
    DARK_OVERCAST("Mendung Gelap"),
    FALLING_BAMBOO_LEAVES("Guguran Daun Bambu"),
    EVENING_FIREFLIES("Pendaran Kunang-Kunang")
}

/**
 * Animated bird in a flock.
 */
data class FlockBird(
    var x: Float,
    var y: Float,
    var speedX: Float,
    var speedY: Float,
    var wingFreq: Float,
    var wingPhase: Float,
    var scale: Float
)

/**
 * Village passerby character (petani bertopi caping, warga membawa bakul/cangkul, anak desa, dsb)
 */
data class VillagerPasserby(
    val id: Int,
    var x: Float,
    var y: Float,
    var targetX: Float,
    var speed: Float, // positive = right, negative = left
    var direction: Int, // 1 = right, -1 = left
    var walkPhase: Float,
    var villagerType: VillagerType,
    var active: Boolean
)

enum class VillagerType {
    FARMER_CAPING,      // Petani dengan topi caping bambu & cangkul
    VILLAGE_TRAVELER,   // Pengembara desa dengan kain sarung & tongkat
    MARKET_VENDOR,      // Pedagang / warga membawa bakul pikulan
    VILLAGE_CHILD       // Anak desa berlari riang
}

/**
 * Dynamic event coordinator managing spontaneous natural occurrences.
 */
class RandomEventManager {
    private val random = Random()

    val flockBirds = mutableListOf<FlockBird>()
    val villagers = mutableListOf<VillagerPasserby>()

    // Overcast darkness factor (0f = normal, 1f = very dark overcast/mendung pekat)
    var overcastFactor: Float = 0f
        private set
    private var targetOvercast: Float = 0f

    // Sudden rain override (0f = none, 1f = full rain event)
    var suddenRainFactor: Float = 0f
        private set
    private var targetSuddenRain: Float = 0f

    // Event timing tracking
    private var nextEventTimer: Float = 4f // First event after 4 seconds
    private var activeEventDuration: Float = 0f
    private var currentEventType: RandomEventType? = null

    // Villager counter
    private var villagerIdCounter = 0

    fun update(
        deltaSeconds: Float,
        width: Float,
        height: Float,
        isNight: Boolean
    ) {
        if (width <= 0 || height <= 0) return

        // 1. Smoothly interpolate overcast & sudden rain factors
        if (overcastFactor < targetOvercast) {
            overcastFactor = (overcastFactor + deltaSeconds * 0.25f).coerceAtMost(targetOvercast)
        } else if (overcastFactor > targetOvercast) {
            overcastFactor = (overcastFactor - deltaSeconds * 0.18f).coerceAtLeast(targetOvercast)
        }

        if (suddenRainFactor < targetSuddenRain) {
            suddenRainFactor = (suddenRainFactor + deltaSeconds * 0.35f).coerceAtMost(targetSuddenRain)
        } else if (suddenRainFactor > targetSuddenRain) {
            suddenRainFactor = (suddenRainFactor - deltaSeconds * 0.20f).coerceAtLeast(targetSuddenRain)
        }

        // 2. Manage Event Triggers
        nextEventTimer -= deltaSeconds
        if (activeEventDuration > 0f) {
            activeEventDuration -= deltaSeconds
            if (activeEventDuration <= 0f) {
                // Event finished -> reset targets smoothly
                targetOvercast = 0f
                targetSuddenRain = 0f
                currentEventType = null
                nextEventTimer = 10f + random.nextFloat() * 18f // schedule next event in 10-28s
            }
        } else if (nextEventTimer <= 0f) {
            triggerNextRandomEvent(width, height, isNight)
        }

        // 3. Update Flock Birds
        val birdIter = flockBirds.iterator()
        while (birdIter.hasNext()) {
            val bird = birdIter.next()
            bird.x += bird.speedX * deltaSeconds
            bird.y += bird.speedY * deltaSeconds + sin(bird.wingPhase.toDouble()).toFloat() * 6f * deltaSeconds
            bird.wingPhase += bird.wingFreq * deltaSeconds

            // Remove if flown out of screen bounds
            if (bird.x > width + 120f || bird.x < -120f || bird.y < -80f || bird.y > height + 80f) {
                birdIter.remove()
            }
        }

        // 4. Update Villagers Passing By on the Dirt Road
        val villagerIter = villagers.iterator()
        while (villagerIter.hasNext()) {
            val v = villagerIter.next()
            v.x += v.speed * deltaSeconds
            v.walkPhase += (kotlin.math.abs(v.speed) * 0.12f) * deltaSeconds

            // Check if reached off-screen destination
            val finished = if (v.direction > 0) v.x > width + 70f else v.x < -70f
            if (finished) {
                villagerIter.remove()
            }
        }

        // Ambient background villagers check (ensure occasional solitary passerby even outside major event)
        if (villagers.isEmpty() && random.nextFloat() < 0.003f) {
            spawnSingleVillager(width, height)
        }
    }

    /**
     * Trigger a spontaneous random event
     */
    private fun triggerNextRandomEvent(width: Float, height: Float, isNight: Boolean) {
        val availableEvents = if (isNight) {
            listOf(
                RandomEventType.WALKING_VILLAGERS,
                RandomEventType.SUDDEN_RAIN,
                RandomEventType.DARK_OVERCAST,
                RandomEventType.EVENING_FIREFLIES
            )
        } else {
            listOf(
                RandomEventType.BIRD_FLOCK,
                RandomEventType.WALKING_VILLAGERS,
                RandomEventType.SUDDEN_RAIN,
                RandomEventType.DARK_OVERCAST,
                RandomEventType.FALLING_BAMBOO_LEAVES
            )
        }

        val chosen = availableEvents[random.nextInt(availableEvents.size)]
        currentEventType = chosen

        when (chosen) {
            RandomEventType.BIRD_FLOCK -> {
                activeEventDuration = 14f
                spawnBirdFlock(width, height)
            }
            RandomEventType.WALKING_VILLAGERS -> {
                activeEventDuration = 22f
                spawnVillagerGroup(width, height)
            }
            RandomEventType.SUDDEN_RAIN -> {
                activeEventDuration = 18f
                targetSuddenRain = 0.85f
                targetOvercast = 0.70f
            }
            RandomEventType.DARK_OVERCAST -> {
                activeEventDuration = 20f
                targetOvercast = 0.85f
            }
            RandomEventType.FALLING_BAMBOO_LEAVES -> {
                activeEventDuration = 15f
            }
            RandomEventType.EVENING_FIREFLIES -> {
                activeEventDuration = 16f
            }
        }
    }

    /**
     * Spawns a flock of 5 to 12 birds flying across the mountain and sky in V-formation or cluster.
     */
    fun spawnBirdFlock(width: Float, height: Float) {
        val birdCount = 6 + random.nextInt(7)
        val flyFromLeft = random.nextBoolean()
        val startBaseX = if (flyFromLeft) -80f else width + 80f
        val startBaseY = height * (0.12f + random.nextFloat() * 0.22f)
        val dir = if (flyFromLeft) 1f else -1f
        val baseSpeed = (65f + random.nextFloat() * 45f) * dir
        val baseSpeedY = (random.nextFloat() * 20f - 10f)

        for (i in 0 until birdCount) {
            val offsetX = -dir * (i * 24f + random.nextFloat() * 15f)
            val offsetY = (i % 3 - 1) * 22f + random.nextFloat() * 14f
            flockBirds.add(
                FlockBird(
                    x = startBaseX + offsetX,
                    y = startBaseY + offsetY,
                    speedX = baseSpeed + (random.nextFloat() * 10f - 5f),
                    speedY = baseSpeedY + (random.nextFloat() * 6f - 3f),
                    wingFreq = 8f + random.nextFloat() * 4f,
                    wingPhase = random.nextFloat() * 6.28f,
                    scale = 0.85f + random.nextFloat() * 0.45f
                )
            )
        }
    }

    /**
     * Spawns a group of villagers walking on the dirt road (e.g. going to or from the paddy/bamboo field).
     */
    fun spawnVillagerGroup(width: Float, height: Float) {
        val count = 2 + random.nextInt(3)
        val walkRight = random.nextBoolean()
        val startX = if (walkRight) -50f else width + 50f
        val direction = if (walkRight) 1 else -1
        val types = VillagerType.entries.toTypedArray()

        for (i in 0 until count) {
            val speedMag = 26f + random.nextFloat() * 16f
            val spacing = -direction * (i * 65f + random.nextFloat() * 20f)
            val groundY = height * 0.70f + 14f + (i % 2) * 12f
            villagers.add(
                VillagerPasserby(
                    id = ++villagerIdCounter,
                    x = startX + spacing,
                    y = groundY,
                    targetX = if (walkRight) width + 100f else -100f,
                    speed = speedMag * direction,
                    direction = direction,
                    walkPhase = random.nextFloat() * 6.28f,
                    villagerType = types[random.nextInt(types.size)],
                    active = true
                )
            )
        }
    }

    private fun spawnSingleVillager(width: Float, height: Float) {
        val walkRight = random.nextBoolean()
        val direction = if (walkRight) 1 else -1
        val types = VillagerType.entries.toTypedArray()
        val speedMag = 24f + random.nextFloat() * 18f
        val groundY = height * 0.70f + 18f
        villagers.add(
            VillagerPasserby(
                id = ++villagerIdCounter,
                x = if (walkRight) -40f else width + 40f,
                y = groundY,
                targetX = if (walkRight) width + 80f else -80f,
                speed = speedMag * direction,
                direction = direction,
                walkPhase = random.nextFloat() * 6.28f,
                villagerType = types[random.nextInt(types.size)],
                active = true
            )
        )
    }

    /**
     * Allows manual or instant trigger from UI/testing.
     */
    fun triggerEvent(type: RandomEventType, width: Float, height: Float) {
        currentEventType = type
        when (type) {
            RandomEventType.BIRD_FLOCK -> {
                activeEventDuration = 14f
                spawnBirdFlock(width, height)
            }
            RandomEventType.WALKING_VILLAGERS -> {
                activeEventDuration = 22f
                spawnVillagerGroup(width, height)
            }
            RandomEventType.SUDDEN_RAIN -> {
                activeEventDuration = 20f
                targetSuddenRain = 0.90f
                targetOvercast = 0.75f
            }
            RandomEventType.DARK_OVERCAST -> {
                activeEventDuration = 20f
                targetOvercast = 0.90f
            }
            RandomEventType.FALLING_BAMBOO_LEAVES -> {
                activeEventDuration = 15f
            }
            RandomEventType.EVENING_FIREFLIES -> {
                activeEventDuration = 16f
            }
        }
    }
}
