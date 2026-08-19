package com.example.ui.render

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pixel-art and stylized aesthetic canvas drawing routines for the Ambient Garden.
 */
object PixelArtDrawers {

    // -------------------------------------------------------------------------
    // 1. Ceramic & Stone Plant Pots
    // -------------------------------------------------------------------------
    fun drawGardenPot(
        drawScope: DrawScope,
        centerX: Float,
        bottomY: Float,
        potWidth: Float = 140f,
        potHeight: Float = 95f,
        potStyle: String = "ceramic",
        hydration: Float = 0.8f
    ) {
        val left = centerX - potWidth / 2
        val top = bottomY - potHeight
        val rimHeight = 16f
        val rimOverlap = 14f

        // Pot shadow
        drawScope.drawOval(
            color = Color(0x33000000),
            topLeft = Offset(centerX - potWidth * 0.65f, bottomY - 6f),
            size = Size(potWidth * 1.3f, 18f)
        )

        // Main pot body trapezoid
        val potPath = Path().apply {
            moveTo(left + 16f, bottomY)
            lineTo(centerX + potWidth / 2 - 16f, bottomY)
            lineTo(centerX + potWidth / 2, top + rimHeight)
            lineTo(left, top + rimHeight)
            close()
        }

        val potBrush = when (potStyle) {
            "stone" -> Brush.horizontalGradient(
                listOf(Color(0xFF5D6D7E), Color(0xFF85929E), Color(0xFF34495E)),
                startX = left,
                endX = left + potWidth
            )
            "terracotta" -> Brush.horizontalGradient(
                listOf(Color(0xFFD35400), Color(0xFFE59866), Color(0xFFA04000)),
                startX = left,
                endX = left + potWidth
            )
            else -> Brush.horizontalGradient(
                listOf(Color(0xFF2C3E50), Color(0xFF4CA1AF), Color(0xFF1B2A47)),
                startX = left,
                endX = left + potWidth
            )
        }

        drawScope.drawPath(potPath, potBrush)

        // Pot rim (top lip)
        drawScope.drawRoundRect(
            brush = potBrush,
            topLeft = Offset(left - rimOverlap / 2, top),
            size = Size(potWidth + rimOverlap, rimHeight + 2f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Rim highlight line
        drawScope.drawLine(
            color = Color(0x44FFFFFF),
            start = Offset(left, top + 3f),
            end = Offset(left + potWidth, top + 3f),
            strokeWidth = 2f
        )

        // Soil inside pot (color darkens with hydration)
        val soilColor = if (hydration > 0.4f) Color(0xFF2A1C12) else Color(0xFF4A3828)
        drawScope.drawOval(
            color = soilColor,
            topLeft = Offset(left + 6f, top + 2f),
            size = Size(potWidth - 12f, 16f)
        )

        // Soil pebble texture
        drawScope.drawCircle(Color(0xFF1E140C), 2f, Offset(centerX - 24f, top + 8f))
        drawScope.drawCircle(Color(0xFF1E140C), 2.5f, Offset(centerX + 18f, top + 9f))
        drawScope.drawCircle(Color(0xFF5D4037), 1.8f, Offset(centerX + 2f, top + 11f))
    }

    // -------------------------------------------------------------------------
    // 2. Japanese Stone Lantern (Tōrō)
    // -------------------------------------------------------------------------
    fun drawStoneLantern(
        drawScope: DrawScope,
        x: Float,
        groundY: Float,
        isNight: Boolean,
        ambientLight: Float
    ) {
        val w = 36f
        val h = 75f
        val baseTop = groundY - 14f

        // Shadow
        drawScope.drawOval(Color(0x33000000), Offset(x - 24f, groundY - 4f), Size(48f, 10f))

        // Base & pedestal
        drawScope.drawRoundRect(Color(0xFF455A64), Offset(x - 16f, baseTop), Size(32f, 14f), CornerRadius(3f, 3f))
        drawScope.drawRect(Color(0xFF546E7A), Offset(x - 6f, groundY - 42f), Size(12f, 28f))

        // Light chamber platform
        drawScope.drawRoundRect(Color(0xFF37474F), Offset(x - 18f, groundY - 46f), Size(36f, 6f), CornerRadius(2f, 2f))

        // Light chamber
        val chamberTop = groundY - 62f
        val lampLightColor = if (isNight) Color(0xFFFFD54F) else Color(0xFFFFF9C4)
        drawScope.drawRect(lampLightColor, Offset(x - 10f, chamberTop), Size(20f, 16f))

        if (isNight) {
            // Warm lantern glow halo
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x66FFA000), Color(0x22FFD54F), Color.Transparent),
                    center = Offset(x, chamberTop + 8f),
                    radius = 55f
                ),
                radius = 55f,
                center = Offset(x, chamberTop + 8f)
            )
        }

        // Lantern wooden grid posts
        drawScope.drawRect(Color(0xFF263238), Offset(x - 10f, chamberTop), Size(3f, 16f))
        drawScope.drawRect(Color(0xFF263238), Offset(x + 7f, chamberTop), Size(3f, 16f))
        drawScope.drawRect(Color(0xFF263238), Offset(x - 10f, chamberTop + 6f), Size(20f, 3f))

        // Pagoda roof
        val roofPath = Path().apply {
            moveTo(x - 22f, chamberTop)
            lineTo(x + 22f, chamberTop)
            lineTo(x + 12f, chamberTop - 12f)
            lineTo(x - 12f, chamberTop - 12f)
            close()
        }
        drawScope.drawPath(roofPath, Color(0xFF37474F))

        // Top jewel finial
        drawScope.drawCircle(Color(0xFF263238), 5f, Offset(x, chamberTop - 14f))
    }

    // -------------------------------------------------------------------------
    // 3. Plant Species Renderers
    // -------------------------------------------------------------------------

    fun drawPlant(
        drawScope: DrawScope,
        speciesId: String,
        stage: Int,
        progressInStage: Float,
        soilCenterX: Float,
        soilCenterY: Float,
        swayAngle: Float = 0f,
        bounceScale: Float = 1.0f
    ) {
        drawScope.scale(bounceScale, Offset(soilCenterX, soilCenterY)) {
            when (speciesId) {
                "bonsai" -> drawBonsai(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
                "watermelon" -> drawWatermelon(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
                "sunflower" -> drawSunflower(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
                "lavender" -> drawLavender(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
                "succulent" -> drawSucculent(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
                else -> drawBonsai(drawScope, stage, progressInStage, soilCenterX, soilCenterY, swayAngle)
            }
        }
    }

    // 3A. Japanese Bonsai Tree
    private fun drawBonsai(
        drawScope: DrawScope,
        stage: Int,
        progress: Float,
        cx: Float,
        cy: Float,
        sway: Float
    ) {
        val barkColor = Color(0xFF4E342E)
        val needleDark = Color(0xFF1B5E20)
        val needleLight = Color(0xFF43A047)

        when (stage) {
            1 -> { // Pine Seed
                drawScope.drawOval(barkColor, Offset(cx - 5f, cy - 4f), Size(10f, 8f))
                drawScope.drawLine(needleLight, Offset(cx, cy - 4f), Offset(cx + 2f, cy - 10f), strokeWidth = 2.5f)
            }
            2 -> { // Pine Sprout
                val h = 18f + progress * 10f
                drawScope.drawLine(Color(0xFF689F38), Offset(cx, cy), Offset(cx, cy - h), strokeWidth = 3f)
                // Whorl of needles
                drawScope.drawLine(needleLight, Offset(cx, cy - h), Offset(cx - 10f, cy - h - 8f), strokeWidth = 2f)
                drawScope.drawLine(needleDark, Offset(cx, cy - h), Offset(cx + 10f, cy - h - 8f), strokeWidth = 2f)
                drawScope.drawLine(needleLight, Offset(cx, cy - h), Offset(cx - 4f, cy - h - 12f), strokeWidth = 2f)
                drawScope.drawLine(needleDark, Offset(cx, cy - h), Offset(cx + 4f, cy - h - 12f), strokeWidth = 2f)
            }
            3 -> { // Young Bonsai Sapling
                val trunkH = 50f + progress * 18f
                val path = Path().apply {
                    moveTo(cx - 4f, cy)
                    quadraticTo(cx - 8f, cy - trunkH * 0.5f, cx + 5f + sway * 10f, cy - trunkH)
                    lineTo(cx + 1f + sway * 10f, cy - trunkH)
                    quadraticTo(cx - 4f, cy - trunkH * 0.5f, cx + 4f, cy)
                    close()
                }
                drawScope.drawPath(path, barkColor)

                // Left branch pad
                drawScope.drawOval(needleDark, Offset(cx - 30f + sway * 4f, cy - trunkH * 0.6f), Size(26f, 14f))
                drawScope.drawOval(needleLight, Offset(cx - 28f + sway * 4f, cy - trunkH * 0.6f - 2f), Size(22f, 10f))

                // Crown foliage pad
                drawScope.drawOval(needleDark, Offset(cx - 16f + sway * 10f, cy - trunkH - 12f), Size(38f, 18f))
                drawScope.drawOval(needleLight, Offset(cx - 14f + sway * 10f, cy - trunkH - 14f), Size(32f, 14f))
            }
            4 -> { // Layered Sculpted Branches
                val trunkH = 80f + progress * 15f
                val trunkPath = Path().apply {
                    moveTo(cx - 8f, cy)
                    cubicTo(cx - 22f, cy - 35f, cx + 20f, cy - 65f, cx - 10f + sway * 12f, cy - trunkH)
                    lineTo(cx - 4f + sway * 12f, cy - trunkH)
                    cubicTo(cx + 24f, cy - 65f, cx - 14f, cy - 35f, cx + 8f, cy)
                    close()
                }
                drawScope.drawPath(trunkPath, barkColor)

                // Lower left pad
                drawScope.drawOval(needleDark, Offset(cx - 48f + sway * 5f, cy - 45f), Size(42f, 18f))
                drawScope.drawOval(needleLight, Offset(cx - 45f + sway * 5f, cy - 47f), Size(36f, 14f))

                // Right middle pad
                drawScope.drawOval(needleDark, Offset(cx + 12f + sway * 8f, cy - 65f), Size(46f, 20f))
                drawScope.drawOval(needleLight, Offset(cx + 15f + sway * 8f, cy - 67f), Size(38f, 15f))

                // Top crown
                drawScope.drawOval(needleDark, Offset(cx - 32f + sway * 12f, cy - trunkH - 16f), Size(56f, 24f))
                drawScope.drawOval(needleLight, Offset(cx - 28f + sway * 12f, cy - trunkH - 18f), Size(48f, 18f))
            }
            5 -> { // Ancient Majestic Bonsai
                val trunkH = 110f
                val trunkPath = Path().apply {
                    moveTo(cx - 14f, cy)
                    cubicTo(cx - 36f, cy - 40f, cx + 28f, cy - 75f, cx - 16f + sway * 15f, cy - trunkH)
                    lineTo(cx - 8f + sway * 15f, cy - trunkH)
                    cubicTo(cx + 34f, cy - 75f, cx - 26f, cy - 40f, cx + 14f, cy)
                    close()
                }
                drawScope.drawPath(trunkPath, Brush.horizontalGradient(listOf(Color(0xFF3E2723), Color(0xFF5D4037), Color(0xFF271612))))

                // Branch connections
                drawScope.drawLine(barkColor, Offset(cx - 25f, cy - 42f), Offset(cx - 55f, cy - 52f), 6f)
                drawScope.drawLine(barkColor, Offset(cx + 15f, cy - 70f), Offset(cx + 48f, cy - 78f), 5f)

                // Majestic foliage clouds
                // Tier 1 Left
                drawScope.drawOval(needleDark, Offset(cx - 72f + sway * 5f, cy - 62f), Size(52f, 22f))
                drawScope.drawOval(needleLight, Offset(cx - 68f + sway * 5f, cy - 65f), Size(44f, 16f))

                // Tier 2 Right
                drawScope.drawOval(needleDark, Offset(cx + 18f + sway * 10f, cy - 90f), Size(58f, 24f))
                drawScope.drawOval(needleLight, Offset(cx + 22f + sway * 10f, cy - 93f), Size(50f, 18f))

                // Tier 3 Center Canopy
                drawScope.drawOval(needleDark, Offset(cx - 44f + sway * 15f, cy - trunkH - 22f), Size(75f, 30f))
                drawScope.drawOval(needleLight, Offset(cx - 40f + sway * 15f, cy - trunkH - 25f), Size(65f, 22f))

                // Delicate highlight tufts
                drawScope.drawOval(Color(0xFF81C784), Offset(cx - 24f + sway * 15f, cy - trunkH - 22f), Size(28f, 10f))
            }
        }
    }

    // 3B. Sugar Baby Watermelon
    private fun drawWatermelon(
        drawScope: DrawScope,
        stage: Int,
        progress: Float,
        cx: Float,
        cy: Float,
        sway: Float
    ) {
        val vineColor = Color(0xFF2E7D32)
        val leafColor = Color(0xFF388E3C)
        val melonDark = Color(0xFF1B5E20)
        val melonLight = Color(0xFF66BB6A)
        val flowerYellow = Color(0xFFFFD54F)

        when (stage) {
            1 -> { // Seed
                drawScope.drawOval(Color(0xFF212121), Offset(cx - 4f, cy - 4f), Size(8f, 6f))
                drawScope.drawOval(Color(0xFF424242), Offset(cx - 3f, cy - 3f), Size(4f, 3f))
            }
            2 -> { // Sprout
                val h = 16f + progress * 8f
                drawScope.drawLine(vineColor, Offset(cx, cy), Offset(cx, cy - h), strokeWidth = 3f)
                drawScope.drawOval(leafColor, Offset(cx - 12f, cy - h - 4f), Size(12f, 8f))
                drawScope.drawOval(leafColor, Offset(cx, cy - h - 4f), Size(12f, 8f))
            }
            3 -> { // Sprawling Vine
                val vinePath = Path().apply {
                    moveTo(cx, cy)
                    cubicTo(cx - 25f, cy - 10f, cx - 45f, cy + 2f, cx - 55f + sway * 4f, cy - 12f)
                }
                val rightVine = Path().apply {
                    moveTo(cx, cy)
                    cubicTo(cx + 20f, cy - 8f, cx + 45f, cy + 4f, cx + 55f - sway * 4f, cy - 10f)
                }
                drawScope.drawPath(vinePath, vineColor, style = Stroke(3.5f))
                drawScope.drawPath(rightVine, vineColor, style = Stroke(3.5f))

                // Big lobed leaves
                drawScope.drawOval(leafColor, Offset(cx - 35f, cy - 22f), Size(24f, 16f))
                drawScope.drawOval(leafColor, Offset(cx + 20f, cy - 20f), Size(24f, 16f))
                drawScope.drawOval(leafColor, Offset(cx - 6f, cy - 28f + sway * 6f), Size(26f, 18f))
            }
            4 -> { // Flowering with baby melon
                // Vine
                drawScope.drawLine(vineColor, Offset(cx - 40f, cy - 8f), Offset(cx + 40f, cy - 6f), strokeWidth = 4f)
                drawScope.drawOval(leafColor, Offset(cx - 45f, cy - 28f), Size(30f, 20f))
                drawScope.drawOval(leafColor, Offset(cx + 15f, cy - 30f), Size(32f, 22f))

                // Yellow blossom
                drawScope.drawCircle(flowerYellow, 8f, Offset(cx - 15f, cy - 25f))
                drawScope.drawCircle(Color(0xFFFFA000), 3f, Offset(cx - 15f, cy - 25f))

                // Small baby melon (egg size)
                val melonRadius = 14f + progress * 6f
                drawScope.drawOval(melonDark, Offset(cx + 10f, cy - 16f), Size(melonRadius * 2, melonRadius * 1.6f))
                // Light stripes
                drawScope.drawLine(melonLight, Offset(cx + 16f, cy - 14f), Offset(cx + 16f, cy + 2f), strokeWidth = 2.5f)
                drawScope.drawLine(melonLight, Offset(cx + 26f, cy - 14f), Offset(cx + 26f, cy + 2f), strokeWidth = 2.5f)
            }
            5 -> { // Large Mature Striped Watermelon
                // Lush vine bed
                drawScope.drawOval(leafColor, Offset(cx - 65f, cy - 28f), Size(40f, 25f))
                drawScope.drawOval(leafColor, Offset(cx + 25f, cy - 30f), Size(45f, 28f))
                drawScope.drawOval(Color(0xFF2E7D32), Offset(cx - 20f, cy - 38f + sway * 5f), Size(42f, 26f))

                // Curly tendril
                drawScope.drawLine(vineColor, Offset(cx - 15f, cy - 22f), Offset(cx - 30f, cy - 32f), strokeWidth = 2.5f)

                // Big Striped Watermelon
                val melonW = 68f
                val melonH = 50f
                val mx = cx - melonW / 2 + 5f
                val my = cy - melonH + 8f

                drawScope.drawOval(melonDark, Offset(mx, my), Size(melonW, melonH))

                // Classic wavy stripes
                val s1 = Path().apply {
                    moveTo(mx + 14f, my + 4f)
                    quadraticTo(mx + 10f, my + melonH * 0.5f, mx + 16f, my + melonH - 4f)
                }
                val s2 = Path().apply {
                    moveTo(mx + 28f, my + 2f)
                    quadraticTo(mx + 25f, my + melonH * 0.5f, mx + 30f, my + melonH - 2f)
                }
                val s3 = Path().apply {
                    moveTo(mx + 44f, my + 2f)
                    quadraticTo(mx + 48f, my + melonH * 0.5f, mx + 42f, my + melonH - 2f)
                }
                val s4 = Path().apply {
                    moveTo(mx + 56f, my + 6f)
                    quadraticTo(mx + 58f, my + melonH * 0.5f, mx + 54f, my + melonH - 6f)
                }

                drawScope.drawPath(s1, melonLight, style = Stroke(4f))
                drawScope.drawPath(s2, melonLight, style = Stroke(5f))
                drawScope.drawPath(s3, melonLight, style = Stroke(5f))
                drawScope.drawPath(s4, melonLight, style = Stroke(4f))

                // Stem connection
                drawScope.drawLine(Color(0xFF4CAF50), Offset(mx + melonW / 2, my), Offset(mx + melonW / 2 + 8f, my - 8f), strokeWidth = 3.5f)
            }
        }
    }

    // 3C. Velvet Queen Sunflower
    private fun drawSunflower(
        drawScope: DrawScope,
        stage: Int,
        progress: Float,
        cx: Float,
        cy: Float,
        sway: Float
    ) {
        val stemColor = Color(0xFF43A047)
        val leafColor = Color(0xFF2E7D32)
        val goldPetal = Color(0xFFFFB300)
        val amberCenter = Color(0xFF4E342E)

        when (stage) {
            1 -> { // Seed
                drawScope.drawOval(Color(0xFF263238), Offset(cx - 4f, cy - 6f), Size(8f, 10f))
                drawScope.drawLine(Color(0xFFECEFF1), Offset(cx, cy - 5f), Offset(cx, cy + 2f), 1.5f)
            }
            2 -> { // Heart Sprout
                val h = 20f + progress * 10f
                drawScope.drawLine(stemColor, Offset(cx, cy), Offset(cx, cy - h), strokeWidth = 3.5f)
                drawScope.drawOval(leafColor, Offset(cx - 14f, cy - h - 6f), Size(14f, 10f))
                drawScope.drawOval(leafColor, Offset(cx, cy - h - 6f), Size(14f, 10f))
            }
            3 -> { // Tall Stalk
                val h = 65f + progress * 25f
                val tipX = cx + sway * 8f
                drawScope.drawLine(stemColor, Offset(cx, cy), Offset(tipX, cy - h), strokeWidth = 5f)

                // Broad serrated leaves
                drawScope.drawOval(leafColor, Offset(cx - 26f, cy - h * 0.35f), Size(24f, 14f))
                drawScope.drawOval(leafColor, Offset(cx + 4f, cy - h * 0.55f), Size(26f, 15f))
                drawScope.drawOval(leafColor, Offset(tipX - 22f, cy - h * 0.75f), Size(22f, 12f))
            }
            4 -> { // Swelling Star Bud
                val h = 95f + progress * 15f
                val tipX = cx + sway * 12f
                drawScope.drawLine(stemColor, Offset(cx, cy), Offset(tipX, cy - h), strokeWidth = 6f)

                drawScope.drawOval(leafColor, Offset(cx - 30f, cy - 35f), Size(30f, 16f))
                drawScope.drawOval(leafColor, Offset(cx + 2f, cy - 60f), Size(32f, 18f))

                // Bud sepals
                drawScope.drawCircle(Color(0xFF388E3C), 16f, Offset(tipX, cy - h))
                // Yellow tips peeking
                for (i in 0..7) {
                    val angle = i * (Math.PI / 4.0)
                    val px = tipX + cos(angle).toFloat() * 18f
                    val py = cy - h + sin(angle).toFloat() * 18f
                    drawScope.drawCircle(goldPetal, 4f, Offset(px, py))
                }
            }
            5 -> { // Magnificent Blooming Sunflower
                val h = 125f
                val tipX = cx + sway * 16f
                val tipY = cy - h

                // Thick stalk
                drawScope.drawLine(stemColor, Offset(cx, cy), Offset(tipX, tipY), strokeWidth = 7f)

                // Large drooping leaves
                drawScope.drawOval(leafColor, Offset(cx - 38f, cy - 40f), Size(38f, 20f))
                drawScope.drawOval(leafColor, Offset(cx + 4f, cy - 70f), Size(42f, 22f))
                drawScope.drawOval(leafColor, Offset(tipX - 32f, tipY + 25f), Size(32f, 18f))

                // Blooming petals ring
                val petalRadius = 36f
                for (i in 0..15) {
                    val angle = i * (Math.PI / 8.0)
                    val px = tipX + cos(angle).toFloat() * petalRadius
                    val py = tipY + sin(angle).toFloat() * petalRadius
                    drawScope.drawOval(
                        goldPetal,
                        Offset(px - 9f, py - 9f),
                        Size(18f, 18f)
                    )
                }

                // Inner rich seed head
                drawScope.drawCircle(amberCenter, 22f, Offset(tipX, tipY))
                drawScope.drawCircle(Color(0xFF3E2723), 16f, Offset(tipX, tipY))
                // Seed texture
                for (r in listOf(6f, 11f, 16f)) {
                    drawScope.drawCircle(Color(0xFF271612), r, Offset(tipX, tipY), style = Stroke(1.5f))
                }
            }
        }
    }

    // 3D. French Lavender
    private fun drawLavender(
        drawScope: DrawScope,
        stage: Int,
        progress: Float,
        cx: Float,
        cy: Float,
        sway: Float
    ) {
        val stemColor = Color(0xFF66BB6A)
        val purpleDark = Color(0xFF512DA8)
        val purpleLight = Color(0xFF9575CD)

        when (stage) {
            1 -> { // Seed
                drawScope.drawCircle(Color(0xFF4A148C), 3.5f, Offset(cx, cy - 2f))
            }
            2 -> { // Silver sprout
                val h = 18f + progress * 8f
                drawScope.drawLine(stemColor, Offset(cx, cy), Offset(cx, cy - h), strokeWidth = 2.5f)
                drawScope.drawLine(Color(0xFF81C784), Offset(cx, cy - h), Offset(cx - 8f, cy - h - 6f), strokeWidth = 2f)
                drawScope.drawLine(Color(0xFF81C784), Offset(cx, cy - h), Offset(cx + 8f, cy - h - 6f), strokeWidth = 2f)
            }
            3 -> { // Bushy herb tuft
                for (offset in listOf(-12f, -4f, 4f, 12f)) {
                    val h = 35f + kotlin.math.abs(offset) * -0.5f + progress * 10f
                    drawScope.drawLine(stemColor, Offset(cx + offset * 0.4f, cy), Offset(cx + offset + sway * 5f, cy - h), 2.5f)
                }
                drawScope.drawOval(Color(0xFF4A7C59), Offset(cx - 20f, cy - 25f), Size(40f, 20f))
            }
            4 -> { // Violet Flower Spikes
                for (i in -2..2) {
                    val angleOffset = i * 8f
                    val h = 60f + kotlin.math.abs(i) * -6f + progress * 15f
                    val topX = cx + angleOffset + sway * (8f + i * 2f)
                    val topY = cy - h

                    drawScope.drawLine(stemColor, Offset(cx + i * 4f, cy), Offset(topX, topY), 2.5f)

                    // Floral spikes
                    for (step in 0..4) {
                        val sy = topY + step * 7f
                        drawScope.drawOval(purpleDark, Offset(topX - 6f, sy), Size(12f, 6f))
                        drawScope.drawOval(purpleLight, Offset(topX - 4f, sy - 1f), Size(8f, 4f))
                    }
                }
            }
            5 -> { // Full Fragrant Purple Bloom
                // Base silver-green shrub
                drawScope.drawOval(Color(0xFF388E3C), Offset(cx - 32f, cy - 28f), Size(64f, 24f))

                for (i in -3..3) {
                    val spread = i * 9f
                    val h = 85f - kotlin.math.abs(i) * 6f
                    val topX = cx + spread + sway * (10f + i * 2f)
                    val topY = cy - h

                    drawScope.drawLine(stemColor, Offset(cx + i * 3f, cy), Offset(topX, topY), 2.5f)

                    // Dense purple flower wand
                    for (step in 0..7) {
                        val sy = topY + step * 6.5f
                        val w = if (step == 0 || step == 7) 8f else 13f
                        drawScope.drawOval(purpleDark, Offset(topX - w / 2, sy), Size(w, 6f))
                        drawScope.drawOval(purpleLight, Offset(topX - w / 3, sy - 1f), Size(w * 0.7f, 4f))
                    }
                    // Butterfly bract at apex
                    drawScope.drawOval(Color(0xFFBA68C8), Offset(topX - 5f, topY - 4f), Size(10f, 5f))
                }
            }
        }
    }

    // 3E. Ghost Rose Echeveria Succulent
    private fun drawSucculent(
        drawScope: DrawScope,
        stage: Int,
        progress: Float,
        cx: Float,
        cy: Float,
        sway: Float
    ) {
        val mintDark = Color(0xFF00796B)
        val mintLight = Color(0xFF80CBC4)
        val pinkBlush = Color(0xFFF48FB1)

        when (stage) {
            1 -> { // Leaf Propagule
                drawScope.drawOval(mintLight, Offset(cx - 8f, cy - 6f), Size(16f, 10f))
                drawScope.drawCircle(Color(0xFFE91E63), 2.5f, Offset(cx - 7f, cy))
            }
            2 -> { // Small Rosette
                val r = 10f + progress * 6f
                for (i in 0..4) {
                    val angle = i * (Math.PI * 2.0 / 5.0)
                    val px = cx + cos(angle).toFloat() * r
                    val py = cy - 4f + sin(angle).toFloat() * r * 0.6f
                    drawScope.drawOval(mintLight, Offset(px - 5f, py - 4f), Size(10f, 8f))
                }
                drawScope.drawCircle(mintDark, 5f, Offset(cx, cy - 4f))
            }
            3 -> { // Layered Fleshy Rosette
                val outerR = 24f + progress * 8f
                // Outer ring
                for (i in 0..7) {
                    val angle = i * (Math.PI / 4.0)
                    val px = cx + cos(angle).toFloat() * outerR
                    val py = cy - 6f + sin(angle).toFloat() * outerR * 0.55f
                    drawScope.drawOval(mintDark, Offset(px - 8f, py - 6f), Size(16f, 12f))
                    drawScope.drawOval(mintLight, Offset(px - 6f, py - 5f), Size(12f, 9f))
                }
                // Center heart
                drawScope.drawCircle(Color(0xFF4DB6AC), 10f, Offset(cx, cy - 6f))
            }
            4 -> { // Blushing Rosette
                val outerR = 36f + progress * 8f
                // Tier 1 Outer
                for (i in 0..9) {
                    val angle = i * (Math.PI / 5.0)
                    val px = cx + cos(angle).toFloat() * outerR
                    val py = cy - 8f + sin(angle).toFloat() * outerR * 0.55f
                    drawScope.drawOval(mintDark, Offset(px - 10f, py - 8f), Size(20f, 16f))
                    drawScope.drawOval(mintLight, Offset(px - 8f, py - 7f), Size(16f, 12f))
                    // Pink tip
                    drawScope.drawCircle(pinkBlush, 3.5f, Offset(px, py - 4f))
                }
                // Tier 2 Inner
                for (i in 0..5) {
                    val angle = i * (Math.PI / 3.0) + 0.3
                    val px = cx + cos(angle).toFloat() * (outerR * 0.55f)
                    val py = cy - 8f + sin(angle).toFloat() * (outerR * 0.35f)
                    drawScope.drawOval(mintLight, Offset(px - 7f, py - 6f), Size(14f, 11f))
                    drawScope.drawCircle(pinkBlush, 2.5f, Offset(px, py - 3f))
                }
            }
            5 -> { // Full Symmetrical Geometric Lotus
                val maxR = 52f
                // Tier 1 Grand Outer Petals
                for (i in 0..11) {
                    val angle = i * (Math.PI / 6.0)
                    val px = cx + cos(angle).toFloat() * maxR
                    val py = cy - 10f + sin(angle).toFloat() * maxR * 0.52f
                    drawScope.drawOval(mintDark, Offset(px - 14f, py - 10f), Size(28f, 20f))
                    drawScope.drawOval(mintLight, Offset(px - 11f, py - 9f), Size(22f, 16f))
                    drawScope.drawCircle(pinkBlush, 4.5f, Offset(px, py - 5f))
                }
                // Tier 2 Mid Petals
                for (i in 0..8) {
                    val angle = i * (Math.PI / 4.5) + 0.35
                    val px = cx + cos(angle).toFloat() * (maxR * 0.65f)
                    val py = cy - 10f + sin(angle).toFloat() * (maxR * 0.38f)
                    drawScope.drawOval(mintLight, Offset(px - 10f, py - 8f), Size(20f, 15f))
                    drawScope.drawCircle(pinkBlush, 3.5f, Offset(px, py - 4f))
                }
                // Tier 3 Heart Rosette
                for (i in 0..5) {
                    val angle = i * (Math.PI / 3.0) + 0.7
                    val px = cx + cos(angle).toFloat() * (maxR * 0.32f)
                    val py = cy - 10f + sin(angle).toFloat() * (maxR * 0.20f)
                    drawScope.drawOval(Color(0xFFB2DFDB), Offset(px - 7f, py - 6f), Size(14f, 11f))
                }
                drawScope.drawCircle(pinkBlush, 5f, Offset(cx, cy - 10f))
            }
        }
    }

    // -------------------------------------------------------------------------
    // 4. Sun, Moon & Celestial Bodies
    // -------------------------------------------------------------------------
    fun drawCelestialBody(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        isSun: Boolean,
        ambientLight: Float
    ) {
        if (isSun) {
            // Sun corona & glow
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x88FFF59D), Color(0x33FFE082), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = 60f
                ),
                radius = 60f,
                center = Offset(cx, cy)
            )
            // Sun core
            drawScope.drawCircle(Color(0xFFFFF9C4), 22f, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFFDE7), 16f, Offset(cx, cy))
        } else {
            // Moon aura
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x44E0E0E0), Color(0x11B0BEC5), Color.Transparent),
                    center = Offset(cx, cy),
                    radius = 45f
                ),
                radius = 45f,
                center = Offset(cx, cy)
            )
            // Crescent / Full Moon core
            drawScope.drawCircle(Color(0xFFECEFF1), 18f, Offset(cx, cy))
            // Subtle crater markings
            drawScope.drawCircle(Color(0xFFCFD8DC), 3.5f, Offset(cx - 4f, cy - 3f))
            drawScope.drawCircle(Color(0xFFCFD8DC), 2.5f, Offset(cx + 5f, cy + 4f))
            drawScope.drawCircle(Color(0xFFCFD8DC), 2.0f, Offset(cx + 2f, cy - 6f))
        }
    }

    // -------------------------------------------------------------------------
    // 5. Cloud Rendering
    // -------------------------------------------------------------------------
    fun drawPixelCloud(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        tint: Color,
        alpha: Float
    ) {
        val cloudColor = tint.copy(alpha = alpha)
        // Fluffy layered pill shapes
        drawScope.drawRoundRect(cloudColor, Offset(x, y + h * 0.35f), Size(w, h * 0.65f), CornerRadius(h * 0.3f, h * 0.3f))
        drawScope.drawOval(cloudColor, Offset(x + w * 0.18f, y), Size(w * 0.45f, h * 0.85f))
        drawScope.drawOval(cloudColor, Offset(x + w * 0.45f, y + h * 0.1f), Size(w * 0.38f, h * 0.75f))
    }

    // -------------------------------------------------------------------------
    // 6. Flying Pixel Birds
    // -------------------------------------------------------------------------
    fun drawFlyingBird(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        wingPhase: Float,
        color: Color = Color(0x7737474F)
    ) {
        val wingY = sin(wingPhase.toDouble()).toFloat() * 5f
        val path = Path().apply {
            moveTo(x - 10f, y + wingY)
            quadraticTo(x - 5f, y - 4f, x, y)
            quadraticTo(x + 5f, y - 4f, x + 10f, y + wingY)
        }
        drawScope.drawPath(path, color, style = Stroke(2f))
    }
}
