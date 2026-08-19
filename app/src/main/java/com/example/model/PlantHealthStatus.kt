package com.example.model

/**
 * Health condition of a plant based on hydration history and care consistency over time.
 */
enum class PlantHealthStatus(
    val label: String,
    val description: String,
    val growthMultiplier: Float,
    val statusColorHex: Long
) {
    THRIVING(
        label = "Thriving",
        description = "Optimal soil hydration and care. Blossoming with vitality.",
        growthMultiplier = 1.0f,
        statusColorHex = 0xFF10B981 // Vibrant Emerald
    ),
    HEALTHY(
        label = "Healthy",
        description = "Balanced soil moisture and steady natural growth.",
        growthMultiplier = 0.9f,
        statusColorHex = 0xFF34D399 // Soft Mint
    ),
    THIRSTY(
        label = "Needs Water",
        description = "Soil is getting dry. Growth has slowed down gently.",
        growthMultiplier = 0.35f,
        statusColorHex = 0xFFFBBF24 // Warm Amber
    ),
    DORMANT(
        label = "Dormant",
        description = "Soil is parched. Plant is resting and awaiting rain or watering.",
        growthMultiplier = 0.05f,
        statusColorHex = 0xFFF87171 // Soft Coral
    );

    companion object {
        fun fromHydration(hydrationLevel: Float): PlantHealthStatus {
            return when {
                hydrationLevel >= 0.70f -> THRIVING
                hydrationLevel >= 0.35f -> HEALTHY
                hydrationLevel >= 0.15f -> THIRSTY
                else -> DORMANT
            }
        }
    }
}
