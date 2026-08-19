package com.example.model

/**
 * Metadata and growth parameters for plant species.
 * Real-world inspired growth durations ensure a slow, meditative, non-gamey pace.
 */
data class PlantSpecies(
    val id: String,
    val name: String,
    val scientificName: String,
    val description: String,
    val lore: String,
    val growthDurationMillis: Long, // Base duration for full maturity
    val stageNames: List<String>,
    val stageDescriptions: List<String>,
    val idealWaterIntervalHours: Int = 24,
    val primaryColorHex: Long = 0xFF4CAF50,
    val accentColorHex: Long = 0xFFFFEB3B,
    val fruitColorHex: Long = 0xFFE91E63
)

object PlantCatalogue {
    // 5 Distinct plant species with real-time inspired growth durations
    val WATERMELON = PlantSpecies(
        id = "watermelon",
        name = "Sugar Baby Watermelon",
        scientificName = "Citrullus lanatus",
        description = "A classic heirloom watermelon with lush sprawling vines and sweet, crisp dark green fruit.",
        lore = "Watermelons grow with unhurried patience, taking warmth from the sun each day to slowly sweeten.",
        growthDurationMillis = 5 * 24 * 60 * 60 * 1000L, // 5 days in real time
        stageNames = listOf(
            "Buried Seed",
            "Cotyledon Sprout",
            "Creeping Vine",
            "Golden Blossom",
            "Ripe Striped Melon"
        ),
        stageDescriptions = listOf(
            "Tucked gently beneath warm soil, awaiting the gentle touch of moisture.",
            "Two tender seed leaves emerge, greeting the morning sky.",
            "Sturdy green vines unfurl gracefully, reaching along the garden floor.",
            "Bright yellow flowers open to catch the daylight, with tiny melons forming.",
            "A heavy, sweet melon rests proudly amidst dark green leaves."
        ),
        idealWaterIntervalHours = 24,
        primaryColorHex = 0xFF2E7D32,
        accentColorHex = 0xFFFBC02D,
        fruitColorHex = 0xFFD81B60
    )

    val BONSAI = PlantSpecies(
        id = "bonsai",
        name = "Japanese Black Pine",
        scientificName = "Pinus thunbergii",
        description = "A stoic evergreen miniature tree that embodies harmony, balance, and the enduring flow of time.",
        lore = "Bonsai is the meditation of time itself—every needle and branch tells a quiet story of patience.",
        growthDurationMillis = 7 * 24 * 60 * 60 * 1000L, // 7 days in real time
        stageNames = listOf(
            "Conifer Seed",
            "Pine Needle Sprout",
            "Young Sapling",
            "Sculpted Branches",
            "Majestic Ancient Bonsai"
        ),
        stageDescriptions = listOf(
            "A tiny winged seed resting in mineral-rich akadama soil.",
            "A delicate whorl of tender green needles crowns the seedling stem.",
            "A miniature wooden trunk starts forming rustic bark texture.",
            "Layered foliage pads develop into a harmonious natural silhouette.",
            "A peaceful, weathered bonsai displaying graceful poise in its ceramic pot."
        ),
        idealWaterIntervalHours = 36,
        primaryColorHex = 0xFF1B5E20,
        accentColorHex = 0xFF5D4037,
        fruitColorHex = 0xFF33691E
    )

    val SUNFLOWER = PlantSpecies(
        id = "sunflower",
        name = "Velvet Queen Sunflower",
        scientificName = "Helianthus annuus",
        description = "A cheerful, sun-seeking giant that turns its head to follow the warmth of the sun throughout the day.",
        lore = "Sunflowers remind us to always turn our gaze towards the light, even on cloudy mornings.",
        growthDurationMillis = 3 * 24 * 60 * 60 * 1000L, // 3 days in real time
        stageNames = listOf(
            "Striped Seed",
            "Heart-leaf Sprout",
            "Tall Sturdy Stalk",
            "Swelling Star Bud",
            "Radiant Blooming Sun"
        ),
        stageDescriptions = listOf(
            "A sturdy striped seed nestling in cool earth.",
            "Broad heart-shaped leaves unfurl towards the morning rays.",
            "A thick fuzzy stalk rises skyward with strong serrated leaves.",
            "A tightly wrapped green crown begins to reveal fiery petals.",
            "A dazzling golden-amber head in full magnificent bloom."
        ),
        idealWaterIntervalHours = 20,
        primaryColorHex = 0xFF388E3C,
        accentColorHex = 0xFFFFA000,
        fruitColorHex = 0xFFFFD54F
    )

    val LAVENDER = PlantSpecies(
        id = "lavender",
        name = "French Lavender",
        scientificName = "Lavandula stoechas",
        description = "A fragrant Mediterranean herb with silver-green foliage and calming purple butterfly-like petals.",
        lore = "A gentle brush of the wind releases a soothing aroma that eases the busy mind.",
        growthDurationMillis = 4 * 24 * 60 * 60 * 1000L, // 4 days in real time
        stageNames = listOf(
            "Aromatic Seed",
            "Silver-leaf Sprout",
            "Bushy Herb Tuft",
            "Violet Flower Spikes",
            "Fragrant Purple Bloom"
        ),
        stageDescriptions = listOf(
            "A tiny fragrant seed sleeping quietly in aerated soil.",
            "Slender grey-green leaves emerge with a subtle sweet aroma.",
            "A compact fragrant bush develops soft textured stems.",
            "Tall floral spires rise above the foliage with budding bracts.",
            "Vibrant purple flower wands sway gently, whispering peace into the breeze."
        ),
        idealWaterIntervalHours = 48,
        primaryColorHex = 0xFF4A7C59,
        accentColorHex = 0xFF7E57C2,
        fruitColorHex = 0xFF9575CD
    )

    val SUCCULENT = PlantSpecies(
        id = "succulent",
        name = "Ghost Rose Echeveria",
        scientificName = "Echeveria lilacina",
        description = "A powdery pastel succulent forming a symmetrical lotus-like rosette with fleshy crystalline leaves.",
        lore = "Quiet and self-sufficient, holding moisture like a cherished memory.",
        growthDurationMillis = 4 * 24 * 60 * 60 * 1000L, // 4 days in real time
        stageNames = listOf(
            "Succulent Propagule",
            "Tiny Rosette Bud",
            "Layered Fleshy Leaves",
            "Blushing Pink Edges",
            "Pristine Geometric Lotus"
        ),
        stageDescriptions = listOf(
            "A single plump leaf taking root in coarse sandy soil.",
            "Microscopic rosettes form at the base of the leaf.",
            "Symmetrical tiers of mint-tinted fleshy leaves arrange themselves.",
            "Sunlight kisses the leaf margins with a soft lavender-pink blush.",
            "A timeless, perfectly balanced pastel jewel in steady stillness."
        ),
        idealWaterIntervalHours = 72,
        primaryColorHex = 0xFF00796B,
        accentColorHex = 0xFFB39DDB,
        fruitColorHex = 0xFF80CBC4
    )

    val SPECIES_LIST = listOf(BONSAI, WATERMELON, SUNFLOWER, LAVENDER, SUCCULENT)

    fun getSpeciesById(id: String): PlantSpecies {
        return SPECIES_LIST.find { it.id == id } ?: BONSAI
    }
}
