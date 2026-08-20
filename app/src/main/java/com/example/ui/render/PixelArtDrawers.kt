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
import kotlin.math.PI
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
    // 4. Sun, Moon & Celestial Bodies (Overhauled Artistry with Bloom Shader)
    // -------------------------------------------------------------------------
    fun drawCelestialBody(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        isSun: Boolean,
        ambientLight: Float = 1.0f,
        timeOfDay: com.example.model.TimeOfDay = com.example.model.TimeOfDay.AFTERNOON,
        animTime: Float = 0f
    ) {
        // Render high-intensity HDR celestial bloom shader first
        CelestialBloomShader.drawBloom(
            drawScope = drawScope,
            cx = cx,
            cy = cy,
            isSun = isSun,
            timeOfDay = timeOfDay,
            animTime = animTime,
            ambientLight = ambientLight
        )

        if (isSun) {
            drawOverhauledSun(drawScope, cx, cy, timeOfDay, animTime, ambientLight)
        } else {
            drawOverhauledMoon(drawScope, cx, cy, timeOfDay, animTime, ambientLight)
        }
    }

    private fun drawOverhauledSun(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        timeOfDay: com.example.model.TimeOfDay,
        animTime: Float,
        ambientLight: Float
    ) {
        val isSunriseOrDawn = timeOfDay == com.example.model.TimeOfDay.DAWN
        val isSunsetOrGolden = timeOfDay == com.example.model.TimeOfDay.GOLDEN_HOUR || timeOfDay == com.example.model.TimeOfDay.SUNSET
        val pulse = (sin(animTime.toDouble() * 1.8).toFloat() * 0.06f + 1.0f)

        if (isSunsetOrGolden) {
            // -----------------------------------------------------------------
            // A. CINEMATIC SUNSET / GOLDEN HOUR SUN
            // -----------------------------------------------------------------
            val outerRadius = 140f * pulse
            val midRadius = 75f * pulse
            val coreRadius = 38f

            // 1. Vast twilight radial aura
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66F97316),
                        Color(0x33EA580C),
                        Color(0x15991B1B),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = Offset(cx, cy)
            )

            // 2. Warm golden-orange mid corona
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xAAFFD54F),
                        Color(0x66F97316),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = midRadius
                ),
                radius = midRadius,
                center = Offset(cx, cy)
            )

            // 3. Sunset radiant god rays radiating upward
            val rayCount = 8
            val rayLength = 110f
            for (i in 0 until rayCount) {
                val angleDeg = -140f + (100f / (rayCount - 1)) * i + sin(animTime.toDouble() + i).toFloat() * 3f
                val angleRad = (angleDeg * Math.PI / 180.0).toFloat()
                val px = cx + cos(angleRad) * rayLength
                val py = cy + sin(angleRad) * rayLength
                drawScope.drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x66FDE047), Color(0x22F97316), Color.Transparent),
                        start = Offset(cx, cy),
                        end = Offset(px, py)
                    ),
                    start = Offset(cx, cy),
                    end = Offset(px, py),
                    strokeWidth = 4.5f
                )
            }

            // 4. Glowing Sun Core Disk with vertical color gradient
            drawScope.drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7),
                        Color(0xFFFFD54F),
                        Color(0xFFFF7043),
                        Color(0xFFEA580C)
                    ),
                    startY = cy - coreRadius,
                    endY = cy + coreRadius
                ),
                radius = coreRadius,
                center = Offset(cx, cy)
            )

            // 5. Classic Iconic Retro/Anime Horizon Atmospheric Refraction Slices
            val sliceColor = Color(0x991E1B4B)
            val sliceOffsets = listOf(0.20f, 0.45f, 0.70f)
            val sliceHeights = listOf(2.5f, 3.5f, 4.5f)
            for (idx in sliceOffsets.indices) {
                val sy = cy + coreRadius * sliceOffsets[idx]
                val halfW = kotlin.math.sqrt((coreRadius * coreRadius - (sy - cy) * (sy - cy)).coerceAtLeast(0f))
                if (halfW > 2f) {
                    drawScope.drawRoundRect(
                        color = sliceColor,
                        topLeft = Offset(cx - halfW, sy - sliceHeights[idx] / 2),
                        size = Size(halfW * 2, sliceHeights[idx]),
                        cornerRadius = CornerRadius(1.5f, 1.5f)
                    )
                }
            }
        } else if (isSunriseOrDawn) {
            // -----------------------------------------------------------------
            // B. DAWN / SUNRISE SUN (Soft Rose-Gold Bloom)
            // -----------------------------------------------------------------
            val outerRadius = 100f * pulse
            val midRadius = 55f * pulse
            val coreRadius = 26f

            // Rose-amber soft bloom
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x66FF8A80),
                        Color(0x35FFE082),
                        Color(0x10FF80AB),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = Offset(cx, cy)
            )

            // Radiant inner halo
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xAAFFF9C4),
                        Color(0x66FFAB91),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = midRadius
                ),
                radius = midRadius,
                center = Offset(cx, cy)
            )

            // Soft 8-point morning diamond sparkles
            val rotAngle = animTime * 15f
            drawScope.rotate(rotAngle, Offset(cx, cy)) {
                for (i in 0 until 4) {
                    val angleRad = (i * 45f * Math.PI / 180.0).toFloat()
                    val len = 38f * pulse
                    val p1x = cx + cos(angleRad) * len
                    val p1y = cy + sin(angleRad) * len
                    val p2x = cx - cos(angleRad) * len
                    val p2y = cy - sin(angleRad) * len
                    drawLine(
                        color = Color(0x55FFE082),
                        start = Offset(p1x, p1y),
                        end = Offset(p2x, p2y),
                        strokeWidth = if (i % 2 == 0) 3.5f else 2.0f
                    )
                }
            }

            // Morning glowing core
            drawScope.drawCircle(Color(0xFFFFE082), coreRadius + 2f, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFF8E1), coreRadius, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFFFFF), coreRadius - 7f, Offset(cx, cy))
        } else {
            // -----------------------------------------------------------------
            // C. BRILLIANT MIDDAY / AFTERNOON SUN
            // -----------------------------------------------------------------
            val outerRadius = 115f * pulse
            val midRadius = 60f * pulse
            val coreRadius = 26f

            // 1. Expansive ambient daylight bloom
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x55FFF59D),
                        Color(0x28FDE047),
                        Color(0x0CF59E0B),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = Offset(cx, cy)
            )

            // 2. Mid incandescent corona ring
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x99FFFDE7),
                        Color(0x44FFD54F),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = midRadius
                ),
                radius = midRadius,
                center = Offset(cx, cy)
            )

            // 3. Rotating 8-Point Diamond Starburst Corona Spikes
            val rotAngle = (animTime * 20f) % 360f
            drawScope.rotate(rotAngle, Offset(cx, cy)) {
                for (i in 0 until 8) {
                    val angleDeg = i * 45f
                    val angleRad = (angleDeg * Math.PI / 180.0).toFloat()
                    val isMajor = i % 2 == 0
                    val spikeLen = (if (isMajor) 46f else 34f) * pulse
                    val spikeWidth = if (isMajor) 9f else 6f

                    val tipX = cx + cos(angleRad) * spikeLen
                    val tipY = cy + sin(angleRad) * spikeLen

                    val perpAngleRad = angleRad + (Math.PI / 2.0).toFloat()
                    val side1x = cx + cos(angleRad) * (coreRadius + 2f) + cos(perpAngleRad) * spikeWidth
                    val side1y = cy + sin(angleRad) * (coreRadius + 2f) + sin(perpAngleRad) * spikeWidth
                    val side2x = cx + cos(angleRad) * (coreRadius + 2f) - cos(perpAngleRad) * spikeWidth
                    val side2y = cy + sin(angleRad) * (coreRadius + 2f) - sin(perpAngleRad) * spikeWidth

                    val spikePath = Path().apply {
                        moveTo(side1x, side1y)
                        lineTo(tipX, tipY)
                        lineTo(side2x, side2y)
                        close()
                    }

                    drawPath(
                        path = spikePath,
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xDDFFFDE7), Color(0x66FFD54F), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = spikeLen
                        )
                    )
                }
            }

            // 4. High-contrast incandescent solar core
            drawScope.drawCircle(Color(0xFFFFD54F), coreRadius + 4f, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFF9C4), coreRadius + 1f, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFFDE7), coreRadius - 4f, Offset(cx, cy))
            drawScope.drawCircle(Color(0xFFFFFFFF), coreRadius - 10f, Offset(cx, cy))

            // 5. Specular pixel shine dot
            drawScope.drawCircle(Color(0xFFFFFFFF), 4.5f, Offset(cx - 7f, cy - 7f))
        }
    }

    private fun drawOverhauledMoon(
        drawScope: DrawScope,
        cx: Float,
        cy: Float,
        timeOfDay: com.example.model.TimeOfDay,
        animTime: Float,
        ambientLight: Float
    ) {
        val pulse = (sin(animTime.toDouble() * 1.5).toFloat() * 0.04f + 1.0f)
        val outerRadius = 100f * pulse
        val haloRadius = 56f * pulse
        val moonRadius = 24f

        // 1. Ethereal 22° Lunar Atmospheric Halo
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x38818CF8),
                    Color(0x1838BDF8),
                    Color(0x061E1B4B),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = outerRadius
            ),
            radius = outerRadius,
            center = Offset(cx, cy)
        )

        // 2. Chromatic Diffraction Ring (Moon Corona)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x66E0F2FE),
                    Color(0x33C7D2FE),
                    Color(0x15F472B6),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = haloRadius
            ),
            radius = haloRadius,
            center = Offset(cx, cy)
        )

        // 3. Earthshine subtle back-silhouette for depth
        drawScope.drawCircle(
            color = Color(0x351E293B),
            radius = moonRadius + 1.5f,
            center = Offset(cx, cy)
        )

        // 4. Main Pearlescent Lunar Disc with spherical 3D shading
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF1F5F9),
                    Color(0xFFE2E8F0),
                    Color(0xFFCBD5E1)
                ),
                center = Offset(cx - 5f, cy - 5f),
                radius = moonRadius * 1.3f
            ),
            radius = moonRadius,
            center = Offset(cx, cy)
        )

        // 5. Detailed Lunar Maria & Basaltic Sea Craters (Kawah & Lautan Bulan)
        val mareDark = Color(0xFF64748B)
        val mareMedium = Color(0xFF94A3B8)
        val craterRim = Color(0xFFE2E8F0)

        // Oceanus Procellarum (Left Mare)
        drawScope.drawOval(mareMedium.copy(alpha = 0.55f), Offset(cx - 16f, cy - 9f), Size(11f, 16f))
        drawScope.drawOval(mareDark.copy(alpha = 0.45f), Offset(cx - 14f, cy - 7f), Size(7f, 10f))

        // Mare Imbrium (Top Left)
        drawScope.drawOval(mareMedium.copy(alpha = 0.50f), Offset(cx - 9f, cy - 17f), Size(10f, 9f))

        // Mare Serenitatis & Tranquillitatis (Upper Right / Center Right)
        drawScope.drawOval(mareMedium.copy(alpha = 0.55f), Offset(cx + 1f, cy - 12f), Size(9f, 8f))
        drawScope.drawOval(mareDark.copy(alpha = 0.50f), Offset(cx + 3f, cy - 2f), Size(10f, 9f))

        // Mare Fecunditatis & Nectaris (Lower Right)
        drawScope.drawOval(mareMedium.copy(alpha = 0.45f), Offset(cx + 4f, cy + 7f), Size(8f, 7f))

        // Tycho Crater with brilliant ray ejecta (Bottom South Crater)
        val tychoX = cx - 2f
        val tychoY = cy + 13f
        drawScope.drawCircle(mareDark, 3.2f, Offset(tychoX, tychoY))
        drawScope.drawCircle(craterRim, 2.0f, Offset(tychoX, tychoY))
        drawScope.drawCircle(Color(0xFFFFFFFF), 1.0f, Offset(tychoX, tychoY))

        // Tycho Ray streaks
        drawScope.drawLine(Color(0x66FFFFFF), Offset(tychoX, tychoY), Offset(tychoX - 8f, tychoY - 10f), 1.0f)
        drawScope.drawLine(Color(0x66FFFFFF), Offset(tychoX, tychoY), Offset(tychoX + 7f, tychoY - 9f), 1.0f)
        drawScope.drawLine(Color(0x66FFFFFF), Offset(tychoX, tychoY), Offset(tychoX + 4f, tychoY - 16f), 0.8f)

        // Copernicus Crater
        drawScope.drawCircle(mareDark.copy(alpha = 0.6f), 2.2f, Offset(cx - 6f, cy - 1f))
        drawScope.drawCircle(Color(0xFFFFFFFF), 1.0f, Offset(cx - 6f, cy - 1f))

        // Kepler Crater
        drawScope.drawCircle(mareDark.copy(alpha = 0.6f), 1.8f, Offset(cx - 12f, cy + 1f))

        // 6. Brilliant Limb Highlight (Top-left Specular Crescent Curve)
        drawScope.drawArc(
            color = Color(0xFFFFFFFF),
            startAngle = 140f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(cx - moonRadius + 1f, cy - moonRadius + 1f),
            size = Size((moonRadius - 1f) * 2, (moonRadius - 1f) * 2),
            style = Stroke(width = 2.2f)
        )

        // 7. Ambient Lunar Diamond Sparkles
        val sparkleAlpha = (sin(animTime.toDouble() * 2.5).toFloat() * 0.4f + 0.6f).coerceIn(0f, 1f)
        drawDiamondSparkle(drawScope, cx + 34f, cy - 20f, 4f, Color(0xFFC7D2FE).copy(alpha = sparkleAlpha * 0.8f))
        drawDiamondSparkle(drawScope, cx - 28f, cy + 24f, 3.2f, Color(0xFFBAE6FD).copy(alpha = (1f - sparkleAlpha) * 0.7f))
    }

    private fun drawDiamondSparkle(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        size: Float,
        color: Color
    ) {
        val path = Path().apply {
            moveTo(x, y - size)
            lineTo(x + size * 0.5f, y)
            lineTo(x, y + size)
            lineTo(x - size * 0.5f, y)
            close()
        }
        drawScope.drawPath(path, color)
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

    // -------------------------------------------------------------------------
    // 7. Mountain Silhouettes (Gunung Siluet di Belakang Pohon Bambu)
    // -------------------------------------------------------------------------
    fun drawMountainSilhouettes(
        drawScope: DrawScope,
        width: Float,
        height: Float,
        horizonY: Float,
        isNight: Boolean,
        ambientLight: Float,
        fogAlpha: Float = 0f
    ) {
        // Far Atmospheric Mountain Ridge
        val farMountainColor = if (isNight) {
            Color(0xFF0F172A).copy(alpha = 0.65f)
        } else {
            Color(0xFF5A738E).copy(alpha = 0.55f)
        }

        val farPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(0f, horizonY - 95f)
            lineTo(width * 0.12f, horizonY - 145f) // Peak 1
            lineTo(width * 0.26f, horizonY - 75f)
            lineTo(width * 0.42f, horizonY - 170f) // Grand Peak 2
            lineTo(width * 0.58f, horizonY - 90f)
            lineTo(width * 0.72f, horizonY - 155f) // Peak 3
            lineTo(width * 0.88f, horizonY - 80f)
            lineTo(width, horizonY - 110f)
            lineTo(width, horizonY + 60f)
            lineTo(0f, horizonY + 60f)
            close()
        }
        drawScope.drawPath(farPath, farMountainColor)

        // Near Mountain Ridge with Valleys & Ridgeline Depth
        val nearMountainColor = if (isNight) {
            Color(0xFF09111E).copy(alpha = 0.88f)
        } else {
            Color(0xFF384F66).copy(alpha = 0.82f)
        }

        val nearPath = Path().apply {
            moveTo(0f, horizonY)
            lineTo(0f, horizonY - 55f)
            lineTo(width * 0.18f, horizonY - 110f)
            lineTo(width * 0.34f, horizonY - 40f)
            lineTo(width * 0.52f, horizonY - 125f)
            lineTo(width * 0.68f, horizonY - 50f)
            lineTo(width * 0.85f, horizonY - 100f)
            lineTo(width, horizonY - 45f)
            lineTo(width, horizonY + 80f)
            lineTo(0f, horizonY + 80f)
            close()
        }
        drawScope.drawPath(nearPath, nearMountainColor)

        // Subtle mountain mist band at base
        val mistColor = if (isNight) Color(0x221E293B) else Color(0x33CFD8DC)
        drawScope.drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, mistColor, Color.Transparent),
                startY = horizonY - 60f,
                endY = horizonY + 20f
            ),
            topLeft = Offset(0f, horizonY - 60f),
            size = Size(width, 80f)
        )
    }

    // -------------------------------------------------------------------------
    // 8. Dense Lush Bamboo Grove (Pohon Bambu Banyak & Rimbun)
    // -------------------------------------------------------------------------
    fun drawDenseBambooForest(
        drawScope: DrawScope,
        width: Float,
        baseY: Float,
        windSway: Float,
        isNight: Boolean,
        ambientLight: Float
    ) {
        // Bamboo Color Palette
        val bambooDark = if (isNight) Color(0xFF0D2818) else Color(0xFF1B4332)
        val bambooStem = if (isNight) Color(0xFF133C24) else Color(0xFF2D6A4F)
        val bambooLight = if (isNight) Color(0xFF1B4D30) else Color(0xFF40916C)
        val bambooHighlight = if (isNight) Color(0xFF235E3B) else Color(0xFF74C69D)
        val leafDark = if (isNight) Color(0xFF0B2114) else Color(0xFF1E4D2B)
        val leafMid = if (isNight) Color(0xFF163E27) else Color(0xFF2D6A4F)
        val leafLight = if (isNight) Color(0xFF1E5234) else Color(0xFF52B788)
        val leafBright = if (isNight) Color(0xFF276742) else Color(0xFF95D5B2)

        // Stalk specs across background & midground
        // (xRatio, height, thickness, tiltAngle, isBackground)
        val bambooStalks = listOf(
            // Left dense grove
            BambooSpec(0.02f, 260f, 9f, -0.03f, false),
            BambooSpec(0.06f, 290f, 11f, 0.02f, false),
            BambooSpec(0.10f, 240f, 8f, -0.02f, true),
            BambooSpec(0.14f, 275f, 10f, 0.04f, false),
            BambooSpec(0.18f, 250f, 7f, -0.01f, true),
            BambooSpec(0.22f, 280f, 9f, 0.03f, false),
            BambooSpec(0.27f, 230f, 7f, -0.04f, true),
            // Background grove behind/near hut
            BambooSpec(0.35f, 220f, 6f, 0.02f, true),
            BambooSpec(0.40f, 240f, 7f, -0.02f, true),
            BambooSpec(0.58f, 235f, 6.5f, 0.03f, true),
            BambooSpec(0.64f, 255f, 8f, -0.02f, true),
            // Right dense lush grove
            BambooSpec(0.72f, 245f, 7f, 0.02f, true),
            BambooSpec(0.76f, 275f, 9.5f, -0.03f, false),
            BambooSpec(0.80f, 295f, 11f, 0.03f, false),
            BambooSpec(0.85f, 260f, 8f, -0.02f, true),
            BambooSpec(0.89f, 285f, 10.5f, 0.02f, false),
            BambooSpec(0.93f, 310f, 12f, -0.02f, false),
            BambooSpec(0.97f, 270f, 8.5f, 0.03f, false)
        )

        // 1. Draw Stalks (Buluh Bambu Beruas)
        for (stalk in bambooStalks) {
            val sx = width * stalk.xRatio
            val stalkH = stalk.height
            val stalkW = stalk.thickness
            val swayX = windSway * (stalkH * 0.35f) + stalk.tiltAngle * stalkH

            val topX = sx + swayX
            val topY = baseY - stalkH
            val botX = sx
            val botY = baseY

            // Bamboo segment/node calculation
            val nodeCount = (stalkH / 32f).toInt().coerceAtLeast(4)
            val stepY = stalkH / nodeCount

            for (n in 0 until nodeCount) {
                val segBotY = botY - n * stepY
                val segTopY = botY - (n + 1) * stepY
                val segRatio = n.toFloat() / nodeCount
                val segBotX = botX + swayX * segRatio
                val segTopX = botX + swayX * ((n + 1).toFloat() / nodeCount)

                val colorBody = if (stalk.isBackground) bambooStem.copy(alpha = 0.75f) else bambooStem
                val colorLight = if (stalk.isBackground) bambooLight.copy(alpha = 0.75f) else bambooLight
                val colorDark = if (stalk.isBackground) bambooDark.copy(alpha = 0.75f) else bambooDark

                // Main bamboo cane stalk segment
                drawScope.drawLine(
                    color = colorBody,
                    start = Offset(segBotX, segBotY),
                    end = Offset(segTopX, segTopY),
                    strokeWidth = stalkW
                )
                // Left highlight ridge on bamboo
                drawScope.drawLine(
                    color = colorLight,
                    start = Offset(segBotX - stalkW * 0.25f, segBotY),
                    end = Offset(segTopX - stalkW * 0.25f, segTopY),
                    strokeWidth = stalkW * 0.35f
                )
                // Right shadow ridge
                drawScope.drawLine(
                    color = colorDark,
                    start = Offset(segBotX + stalkW * 0.3f, segBotY),
                    end = Offset(segTopX + stalkW * 0.3f, segTopY),
                    strokeWidth = stalkW * 0.3f
                )

                // Bamboo Node / Joint Ring (Ruas Bambu)
                val nodeWidth = stalkW * 1.35f
                drawScope.drawLine(
                    color = bambooHighlight,
                    start = Offset(segTopX - nodeWidth / 2, segTopY + 1f),
                    end = Offset(segTopX + nodeWidth / 2, segTopY + 1f),
                    strokeWidth = 2.5f
                )
                drawScope.drawLine(
                    color = bambooDark,
                    start = Offset(segTopX - nodeWidth / 2, segTopY - 1.5f),
                    end = Offset(segTopX + nodeWidth / 2, segTopY - 1.5f),
                    strokeWidth = 2.0f
                )

                // Small branch shoots with bamboo leaves at nodes
                if (n >= 1 && (n + (stalk.xRatio * 10).toInt()) % 2 == 0) {
                    val branchDir = if ((n % 2) == 0) 1f else -1f
                    val branchStartX = segTopX
                    val branchStartY = segTopY
                    val branchEndX = branchStartX + branchDir * (20f + (n % 3) * 6f) + windSway * 15f
                    val branchEndY = branchStartY - 12f - (n % 2) * 6f

                    drawScope.drawLine(
                        color = bambooDark,
                        start = Offset(branchStartX, branchStartY),
                        end = Offset(branchEndX, branchEndY),
                        strokeWidth = 1.8f
                    )

                    // Cluster of 3-4 lancet bamboo leaves (Rumpun Daun Bambu)
                    drawBambooLeafCluster(
                        drawScope = drawScope,
                        tipX = branchEndX,
                        tipY = branchEndY,
                        direction = branchDir,
                        windSway = windSway,
                        darkColor = leafDark,
                        midColor = leafMid,
                        lightColor = leafLight,
                        brightColor = leafBright
                    )
                }
            }

            // Top Bamboo Crown Foliage (Tajuk Daun Bambu Paling Atas)
            for (leafIdx in -2..2) {
                val leafDir = if (leafIdx <= 0) -1f else 1f
                drawBambooLeafCluster(
                    drawScope = drawScope,
                    tipX = topX + leafIdx * 10f + windSway * 18f,
                    tipY = topY - kotlin.math.abs(leafIdx) * 5f,
                    direction = leafDir,
                    windSway = windSway,
                    darkColor = leafDark,
                    midColor = leafMid,
                    lightColor = leafLight,
                    brightColor = leafBright
                )
            }
        }
    }

    private fun drawBambooLeafCluster(
        drawScope: DrawScope,
        tipX: Float,
        tipY: Float,
        direction: Float,
        windSway: Float,
        darkColor: Color,
        midColor: Color,
        lightColor: Color,
        brightColor: Color
    ) {
        val leafAngles = listOf(
            Pair(-15f, 26f),
            Pair(5f, 32f),
            Pair(25f, 28f),
            Pair(45f, 22f)
        )

        for ((angleDeg, length) in leafAngles) {
            val effectiveAngle = (angleDeg * direction + windSway * 35f) * (PI / 180.0).toFloat()
            val leafEndX = tipX + cos(effectiveAngle) * length * direction
            val leafEndY = tipY + sin(effectiveAngle) * length + 8f

            val leafMidX = (tipX + leafEndX) * 0.5f + sin(effectiveAngle) * 4f * direction
            val leafMidY = (tipY + leafEndY) * 0.5f - 2f

            val leafPath = Path().apply {
                moveTo(tipX, tipY)
                quadraticTo(leafMidX, leafMidY - 3f, leafEndX, leafEndY)
                quadraticTo(leafMidX, leafMidY + 3f, tipX, tipY)
                close()
            }

            // Draw layered bamboo leaf
            drawScope.drawPath(leafPath, darkColor)
            drawScope.drawPath(leafPath, midColor)

            // Leaf central vein highlight
            drawScope.drawLine(
                color = brightColor,
                start = Offset(tipX, tipY),
                end = Offset(leafEndX, leafEndY),
                strokeWidth = 1.0f
            )
        }
    }

    // -------------------------------------------------------------------------
    // 9. Traditional Village Hut (Gubuk Desa / Saung Bambu Pedesaan)
    // -------------------------------------------------------------------------
    fun drawVillageHut(
        drawScope: DrawScope,
        hutCenterX: Float,
        groundY: Float,
        isNight: Boolean,
        ambientLight: Float,
        animTime: Float = 0f
    ) {
        val hutWidth = 135f
        val hutHeight = 110f
        val hutLeft = hutCenterX - hutWidth / 2
        val hutRight = hutCenterX + hutWidth / 2
        val hutBaseY = groundY + 8f
        val floorY = hutBaseY - 26f
        val wallTopY = floorY - 50f
        val roofPeakY = wallTopY - 45f

        // Hut Shadow on Ground
        drawScope.drawOval(
            color = Color(0x44000000),
            topLeft = Offset(hutLeft - 18f, hutBaseY - 8f),
            size = Size(hutWidth + 36f, 22f)
        )

        // 1. Bamboo Stilts / Wooden Foundation Posts (Tiang Bambu Penyangga)
        val stiltColor = if (isNight) Color(0xFF1C130E) else Color(0xFF4E342E)
        val stiltHighlight = if (isNight) Color(0xFF2A1C15) else Color(0xFF6D4C41)
        val stiltPositions = listOf(
            hutLeft + 8f,
            hutLeft + hutWidth * 0.35f,
            hutLeft + hutWidth * 0.65f,
            hutRight - 8f
        )
        for (sx in stiltPositions) {
            drawScope.drawRoundRect(
                color = stiltColor,
                topLeft = Offset(sx - 3.5f, floorY),
                size = Size(7f, hutBaseY - floorY + 4f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawScope.drawLine(
                color = stiltHighlight,
                start = Offset(sx - 2f, floorY),
                end = Offset(sx - 2f, hutBaseY + 2f),
                strokeWidth = 1.5f
            )
        }

        // Cross bracing beams between stilts
        drawScope.drawLine(
            color = stiltColor,
            start = Offset(hutLeft + 10f, hutBaseY - 10f),
            end = Offset(hutRight - 10f, hutBaseY - 10f),
            strokeWidth = 3.5f
        )

        // 2. Bamboo Floor Deck (Lantai Bambu)
        val floorColor = if (isNight) Color(0xFF271A12) else Color(0xFF5D4037)
        val floorHighlight = if (isNight) Color(0xFF38261A) else Color(0xFF8D6E63)
        drawScope.drawRoundRect(
            color = floorColor,
            topLeft = Offset(hutLeft - 4f, floorY),
            size = Size(hutWidth + 8f, 10f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawScope.drawLine(
            color = floorHighlight,
            start = Offset(hutLeft - 4f, floorY + 1f),
            end = Offset(hutRight + 4f, floorY + 1f),
            strokeWidth = 2f
        )

        // 3. Woven Bamboo Walls (Dinding Gedek / Bilik Bambu Tradisional)
        val wallColor = if (isNight) Color(0xFF2D2015) else Color(0xFF6D4C41)
        val wovenColor1 = if (isNight) Color(0xFF3B2A1C) else Color(0xFF8D6E63)
        val wovenColor2 = if (isNight) Color(0xFF24180E) else Color(0xFF4E342E)

        val wallPath = Path().apply {
            moveTo(hutLeft + 6f, floorY)
            lineTo(hutRight - 6f, floorY)
            lineTo(hutRight - 10f, wallTopY)
            lineTo(hutLeft + 10f, wallTopY)
            close()
        }
        drawScope.drawPath(wallPath, wallColor)

        // Detailed Woven Bamboo Weave Pattern (Anyaman Gedek)
        val weaveCols = 9
        val weaveRows = 6
        val colW = (hutWidth - 20f) / weaveCols
        val rowH = (floorY - wallTopY) / weaveRows

        for (r in 0 until weaveRows) {
            for (c in 0 until weaveCols) {
                val wx = hutLeft + 10f + c * colW
                val wy = wallTopY + r * rowH
                val isAlt = (r + c) % 2 == 0
                drawScope.drawRect(
                    color = if (isAlt) wovenColor1 else wovenColor2,
                    topLeft = Offset(wx + 1f, wy + 1f),
                    size = Size(colW - 2f, rowH - 2f)
                )
            }
        }

        // 4. Open Window with Cozy Oil Lamp Glow (Jendela Saung & Lampu Teplok)
        val winW = 28f
        val winH = 24f
        val winX = hutLeft + 22f
        val winY = wallTopY + 12f

        // Window Frame
        drawScope.drawRect(Color(0xFF24180E), Offset(winX - 2f, winY - 2f), Size(winW + 4f, winH + 4f))
        // Window Interior Darkness or Cozy Lamp Light
        val winBg = if (isNight) Color(0xFF100A05) else Color(0xFF332015)
        drawScope.drawRect(winBg, Offset(winX, winY), Size(winW, winH))

        // Warm Cozy Oil Lamp Glow inside window
        val lampFlicker = (sin(animTime.toDouble() * 6.0).toFloat() * 0.08f + 0.92f)
        val lampColor = if (isNight) Color(0xFFFFB74D) else Color(0xFFFFE082)
        drawScope.drawCircle(
            color = lampColor.copy(alpha = if (isNight) 0.85f * lampFlicker else 0.45f),
            radius = 6f,
            center = Offset(winX + winW / 2, winY + winH / 2 + 2f)
        )
        if (isNight) {
            drawScope.drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x99FFA726), Color(0x33FFB74D), Color.Transparent),
                    center = Offset(winX + winW / 2, winY + winH / 2 + 2f),
                    radius = 38f
                ),
                radius = 38f,
                center = Offset(winX + winW / 2, winY + winH / 2 + 2f)
            )
        }
        // Window wooden cross sill
        drawScope.drawLine(Color(0xFF24180E), Offset(winX + winW / 2, winY), Offset(winX + winW / 2, winY + winH), 2f)
        drawScope.drawLine(Color(0xFF24180E), Offset(winX, winY + winH / 2), Offset(winX + winW, winY + winH / 2), 2f)

        // 5. Open Doorway / Veranda Entrance (Pintu Saung)
        val doorW = 26f
        val doorH = 38f
        val doorX = hutRight - 38f
        val doorY = floorY - doorH
        drawScope.drawRect(Color(0xFF1A110A), Offset(doorX, doorY), Size(doorW, doorH))
        // Bamboo door frame
        drawScope.drawRect(
            color = if (isNight) Color(0xFF3B2A1C) else Color(0xFF8D6E63),
            topLeft = Offset(doorX - 2f, doorY - 2f),
            size = Size(doorW + 4f, doorH + 2f),
            style = Stroke(2.5f)
        )

        // 6. Traditional Layered Thatched Roof (Atap Rumbia / Ijuk Jerami Desa)
        val thatchDark = if (isNight) Color(0xFF1E150D) else Color(0xFF4E342E)
        val thatchMid = if (isNight) Color(0xFF2C1F14) else Color(0xFF795548)
        val thatchWarm = if (isNight) Color(0xFF3A291A) else Color(0xFFA1887F)
        val thatchStraw = if (isNight) Color(0xFF4D3823) else Color(0xFFD7CCC8)

        // Main Thatched Roof Pyramid/Trapezoid
        val roofOverhang = 22f
        val roofPath = Path().apply {
            moveTo(hutLeft - roofOverhang, wallTopY + 8f)
            lineTo(hutRight + roofOverhang, wallTopY + 8f)
            lineTo(hutCenterX + 16f, roofPeakY)
            lineTo(hutCenterX - 16f, roofPeakY)
            close()
        }
        drawScope.drawPath(roofPath, thatchMid)

        // Multi-tier thatched straw layers (Lapisan Jerami Rumbia)
        val tierCount = 5
        for (t in 0 until tierCount) {
            val tierRatio = t.toFloat() / tierCount
            val tierY = wallTopY + 8f - (wallTopY + 8f - roofPeakY) * tierRatio
            val tierWidth = (hutWidth + roofOverhang * 2) * (1.0f - tierRatio * 0.72f)
            val tierLeft = hutCenterX - tierWidth / 2

            // Straw tier band
            drawScope.drawRoundRect(
                color = if (t % 2 == 0) thatchWarm else thatchMid,
                topLeft = Offset(tierLeft, tierY - 4f),
                size = Size(tierWidth, 8f),
                cornerRadius = CornerRadius(3f, 3f)
            )

            // Textured thatched straw fringes (Ujung-ujung Jerami)
            val fringeCount = (tierWidth / 6f).toInt().coerceAtLeast(4)
            for (f in 0..fringeCount) {
                val fx = tierLeft + f * 6f
                val fy = tierY + 4f
                val strawLen = 4f + (f % 3) * 2.5f
                drawScope.drawLine(
                    color = if (f % 2 == 0) thatchStraw else thatchDark,
                    start = Offset(fx, fy - 2f),
                    end = Offset(fx + (f % 2 - 0.5f) * 3f, fy + strawLen),
                    strokeWidth = 1.6f
                )
            }
        }

        // Bamboo Roof Ridge Cap (Nok Atap Bambu)
        val ridgePath = Path().apply {
            moveTo(hutCenterX - 22f, roofPeakY - 2f)
            lineTo(hutCenterX + 22f, roofPeakY - 2f)
            lineTo(hutCenterX + 18f, roofPeakY - 8f)
            lineTo(hutCenterX - 18f, roofPeakY - 8f)
            close()
        }
        drawScope.drawPath(ridgePath, thatchDark)
        drawScope.drawLine(
            color = if (isNight) Color(0xFF5D4037) else Color(0xFFBCAAA4),
            start = Offset(hutCenterX - 20f, roofPeakY - 5f),
            end = Offset(hutCenterX + 20f, roofPeakY - 5f),
            strokeWidth = 2f
        )

        // 7. Bamboo Entry Ladder / Steps (Tangga Bambu)
        val ladderLeft = doorX + 2f
        val ladderTop = floorY + 4f
        val ladderHeight = hutBaseY - floorY + 8f
        val ladderColor = if (isNight) Color(0xFF2C1F14) else Color(0xFF6D4C41)
        // Two vertical bamboo rails
        drawScope.drawLine(ladderColor, Offset(ladderLeft, ladderTop), Offset(ladderLeft - 3f, ladderTop + ladderHeight), 3f)
        drawScope.drawLine(ladderColor, Offset(ladderLeft + 18f, ladderTop), Offset(ladderLeft + 15f, ladderTop + ladderHeight), 3f)
        // Horizontal bamboo rungs
        for (rung in 1..3) {
            val ry = ladderTop + rung * (ladderHeight / 3.5f)
            val rx = ladderLeft - (rung * 0.8f)
            drawScope.drawLine(
                color = if (isNight) Color(0xFF3E2C1D) else Color(0xFF8D6E63),
                start = Offset(rx, ry),
                end = Offset(rx + 18f, ry),
                strokeWidth = 2.2f
            )
        }
    }

    // -------------------------------------------------------------------------
    // 10. Rural Dirt Road & Earthen Terrain (Jalan Tanah Pedesaan)
    // -------------------------------------------------------------------------
    fun drawRuralDirtRoadAndGround(
        drawScope: DrawScope,
        width: Float,
        height: Float,
        groundY: Float,
        isNight: Boolean,
        ambientLight: Float,
        windSway: Float
    ) {
        // Earthen / Grass colors
        val grassBaseColor = if (isNight) Color(0xFF0F1E14) else Color(0xFF2D5A38)
        val grassHighlightColor = if (isNight) Color(0xFF162D1F) else Color(0xFF3F7A4D)
        val soilColor = if (isNight) Color(0xFF23180F) else Color(0xFF5C4033)
        val soilMidColor = if (isNight) Color(0xFF302216) else Color(0xFF795548)
        val soilLightColor = if (isNight) Color(0xFF3E2D1D) else Color(0xFF8D6E63)
        val pebbleColor = if (isNight) Color(0xFF1A130D) else Color(0xFF42332B)

        // 1. Natural Grass & Meadow Base
        drawScope.drawRect(
            color = grassBaseColor,
            topLeft = Offset(0f, groundY),
            size = Size(width, height - groundY)
        )

        // Subtle Meadow Gradient
        drawScope.drawRect(
            brush = Brush.verticalGradient(
                listOf(grassHighlightColor.copy(alpha = 0.4f), Color.Transparent),
                startY = groundY,
                endY = groundY + 45f
            ),
            topLeft = Offset(0f, groundY),
            size = Size(width, 45f)
        )

        // 2. Winding Rural Dirt Road (Jalan Tanah Pedesaan Berkelok)
        // Path curves from background left (near hut and bamboo) down toward the front
        val roadTopY = groundY + 4f
        val roadBotY = height

        val roadPath = Path().apply {
            // Left edge of winding dirt path
            moveTo(width * 0.28f, roadTopY)
            cubicTo(
                width * 0.32f, groundY + 30f,
                width * 0.18f, groundY + 70f,
                width * 0.12f, roadBotY
            )
            // Bottom span
            lineTo(width * 0.88f, roadBotY)
            // Right edge of winding dirt path
            cubicTo(
                width * 0.82f, groundY + 70f,
                width * 0.62f, groundY + 30f,
                width * 0.52f, roadTopY
            )
            close()
        }

        // Fill dirt road base
        drawScope.drawPath(roadPath, soilColor)

        // Inner lighter clay/earthen layer
        val innerRoadPath = Path().apply {
            moveTo(width * 0.32f, roadTopY + 2f)
            cubicTo(
                width * 0.35f, groundY + 30f,
                width * 0.23f, groundY + 70f,
                width * 0.18f, roadBotY
            )
            lineTo(width * 0.82f, roadBotY)
            cubicTo(
                width * 0.77f, groundY + 70f,
                width * 0.58f, groundY + 30f,
                width * 0.48f, roadTopY + 2f
            )
            close()
        }
        drawScope.drawPath(innerRoadPath, soilMidColor)

        // 3. Dirt Path Cart / Foot Rut Lines (Jejak Jalan Setapak Tanah)
        val rutPathLeft = Path().apply {
            moveTo(width * 0.36f, roadTopY + 5f)
            cubicTo(
                width * 0.38f, groundY + 32f,
                width * 0.28f, groundY + 72f,
                width * 0.26f, roadBotY
            )
        }
        drawScope.drawPath(rutPathLeft, soilLightColor, style = Stroke(3.5f))

        val rutPathRight = Path().apply {
            moveTo(width * 0.44f, roadTopY + 5f)
            cubicTo(
                width * 0.50f, groundY + 32f,
                width * 0.65f, groundY + 72f,
                width * 0.70f, roadBotY
            )
        }
        drawScope.drawPath(rutPathRight, soilLightColor, style = Stroke(3.0f))

        // 4. Textured Earth Pebbles, Stepping Stones & Soil Clods (Kerikil & Bebatuan Tanah)
        val pebbles = listOf(
            Triple(0.24f, groundY + 45f, 3.5f),
            Triple(0.35f, groundY + 25f, 2.5f),
            Triple(0.42f, groundY + 55f, 4.0f),
            Triple(0.55f, groundY + 38f, 3.0f),
            Triple(0.68f, groundY + 60f, 4.5f),
            Triple(0.78f, groundY + 42f, 3.2f),
            Triple(0.30f, groundY + 80f, 3.8f),
            Triple(0.62f, groundY + 85f, 4.2f)
        )
        for ((pxRatio, py, r) in pebbles) {
            val px = width * pxRatio
            drawScope.drawOval(
                color = pebbleColor,
                topLeft = Offset(px - r, py - r * 0.6f),
                size = Size(r * 2f, r * 1.2f)
            )
            drawScope.drawOval(
                color = soilLightColor,
                topLeft = Offset(px - r * 0.6f, py - r * 0.5f),
                size = Size(r * 0.9f, r * 0.5f)
            )
        }

        // 5. Grass Tufts Along Road Borders (Rumput Liar di Tepi Jalan Tanah)
        val tuftCount = 14
        for (i in 0..tuftCount) {
            val tx = (width / tuftCount) * i + (i % 3) * 8f
            val ty = groundY + 12f + (i % 4) * 14f
            val swayX = tx + windSway * 220f
            val bladeColor = if (isNight) Color(0xFF1B3826) else Color(0xFF4C8C5C)
            drawScope.drawLine(
                color = bladeColor,
                start = Offset(tx, ty),
                end = Offset(swayX, ty - 14f),
                strokeWidth = 2.4f
            )
            drawScope.drawLine(
                color = bladeColor,
                start = Offset(tx + 4f, ty + 2f),
                end = Offset(swayX + 6f, ty - 10f),
                strokeWidth = 1.8f
            )
        }
    }

    // -------------------------------------------------------------------------
    // 11. Village Passerby Characters (Orang-orang Desa Berlalu Lalang)
    // -------------------------------------------------------------------------
    fun drawVillager(
        drawScope: DrawScope,
        villager: com.example.domain.VillagerPasserby,
        isNight: Boolean,
        ambientLight: Float
    ) {
        val x = villager.x
        val y = villager.y
        val dir = villager.direction.toFloat()
        val walkPhase = villager.walkPhase

        // Leg walking cycle swing
        val legSwing = sin(walkPhase.toDouble()).toFloat() * 7f
        val armSwing = cos(walkPhase.toDouble()).toFloat() * 6f

        // Skin & Clothing Palette
        val skinColor = if (isNight) Color(0xFF6D4C41) else Color(0xFFBCAAA4)
        val capingDark = if (isNight) Color(0xFF3E2723) else Color(0xFF8D6E63)
        val capingLight = if (isNight) Color(0xFF5D4037) else Color(0xFFD7CCC8)
        val pantsColor = if (isNight) Color(0xFF1E293B) else Color(0xFF334155)
        val shadowColor = Color(0x33000000)

        // Drop shadow under feet
        drawScope.drawOval(
            color = shadowColor,
            topLeft = Offset(x - 12f, y - 2f),
            size = Size(24f, 6f)
        )

        when (villager.villagerType) {
            com.example.domain.VillagerType.FARMER_CAPING -> {
                val shirtColor = if (isNight) Color(0xFF1A365D) else Color(0xFF2B6CB0)

                // Legs
                drawScope.drawLine(pantsColor, Offset(x - 3f, y - 16f), Offset(x - 3f - legSwing, y), 3.5f)
                drawScope.drawLine(pantsColor, Offset(x + 3f, y - 16f), Offset(x + 3f + legSwing, y), 3.5f)

                // Torso / Shirt
                drawScope.drawRoundRect(
                    color = shirtColor,
                    topLeft = Offset(x - 7f, y - 30f),
                    size = Size(14f, 15f),
                    cornerRadius = CornerRadius(2f, 2f)
                )

                // Head
                drawScope.drawCircle(skinColor, 4.5f, Offset(x, y - 34f))

                // Traditional Conical Bamboo Hat (Topi Caping Tani)
                val capingPath = Path().apply {
                    moveTo(x - 14f, y - 35f)
                    lineTo(x + 14f, y - 35f)
                    lineTo(x, y - 44f)
                    close()
                }
                drawScope.drawPath(capingPath, capingDark)
                drawScope.drawLine(capingLight, Offset(x - 14f, y - 35f), Offset(x, y - 44f), 1.8f)

                // Hoe / Cangkul carried over shoulder
                val cangkulHandleEnd = Offset(x - dir * 16f - armSwing, y - 38f)
                val cangkulHandleStart = Offset(x + dir * 8f + armSwing, y - 24f)
                drawScope.drawLine(
                    color = if (isNight) Color(0xFF3E2723) else Color(0xFF795548),
                    start = cangkulHandleStart,
                    end = cangkulHandleEnd,
                    strokeWidth = 2.2f
                )
                // Cangkul metal blade
                drawScope.drawRect(
                    color = if (isNight) Color(0xFF475569) else Color(0xFF94A3B8),
                    topLeft = Offset(cangkulHandleEnd.x - 3f, cangkulHandleEnd.y - 2f),
                    size = Size(6f, 8f)
                )

                // Arm
                drawScope.drawLine(shirtColor, Offset(x, y - 28f), cangkulHandleStart, 3.0f)
            }

            com.example.domain.VillagerType.MARKET_VENDOR -> {
                val shirtColor = if (isNight) Color(0xFF2E4C33) else Color(0xFF48BB78)
                val sarongColor = if (isNight) Color(0xFF4A154B) else Color(0xFF805AD5)

                // Sarung / Pants
                drawScope.drawRoundRect(
                    color = sarongColor,
                    topLeft = Offset(x - 6f, y - 18f),
                    size = Size(12f, 16f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
                // Feet
                drawScope.drawLine(skinColor, Offset(x - 3f, y - 4f), Offset(x - 3f - legSwing * 0.7f, y), 2.5f)
                drawScope.drawLine(skinColor, Offset(x + 3f, y - 4f), Offset(x + 3f + legSwing * 0.7f, y), 2.5f)

                // Torso
                drawScope.drawRoundRect(shirtColor, Offset(x - 6f, y - 30f), Size(12f, 13f), CornerRadius(2f, 2f))
                // Head with bandana / udeng
                drawScope.drawCircle(skinColor, 4.2f, Offset(x, y - 34f))
                drawScope.drawArc(
                    color = sarongColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(x - 5f, y - 39f),
                    size = Size(10f, 6f)
                )

                // Shoulder Carrying Pole & Woven Baskets (Pikulan Keranjang Bambu)
                val poleStartX = x - 18f
                val poleEndX = x + 18f
                val poleY = y - 31f
                drawScope.drawLine(
                    color = if (isNight) Color(0xFF4E342E) else Color(0xFF8D6E63),
                    start = Offset(poleStartX, poleY),
                    end = Offset(poleEndX, poleY),
                    strokeWidth = 2.5f
                )
                // Basket ropes & baskets
                val basketColor = if (isNight) Color(0xFF3E2723) else Color(0xFFBCAAA4)
                for (bx in listOf(poleStartX, poleEndX)) {
                    drawScope.drawLine(Color(0x88000000), Offset(bx, poleY), Offset(bx, poleY + 12f), 1.2f)
                    drawScope.drawRoundRect(
                        color = basketColor,
                        topLeft = Offset(bx - 6f, poleY + 12f),
                        size = Size(12f, 8f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }
            }

            com.example.domain.VillagerType.VILLAGE_TRAVELER -> {
                val robeColor = if (isNight) Color(0xFF374151) else Color(0xFF6B7280)
                val scarfColor = if (isNight) Color(0xFF7C2D12) else Color(0xFFC2410C)

                // Legs
                drawScope.drawLine(pantsColor, Offset(x - 3f, y - 14f), Offset(x - 3f - legSwing, y), 3.0f)
                drawScope.drawLine(pantsColor, Offset(x + 3f, y - 14f), Offset(x + 3f + legSwing, y), 3.0f)

                // Torso & Traveler Robe
                drawScope.drawRoundRect(robeColor, Offset(x - 6f, y - 28f), Size(12f, 15f), CornerRadius(2f, 2f))
                // Shoulder wrap / kain sarung selempang
                drawScope.drawLine(scarfColor, Offset(x - 6f, y - 28f), Offset(x + 6f, y - 16f), 3.0f)

                // Head
                drawScope.drawCircle(skinColor, 4.0f, Offset(x, y - 32f))

                // Walking stick (Tongkat Bambu Pengembara)
                val stickX = x + dir * 10f
                drawScope.drawLine(
                    color = if (isNight) Color(0xFF5D4037) else Color(0xFFA1887F),
                    start = Offset(stickX, y - 26f),
                    end = Offset(stickX, y),
                    strokeWidth = 2.0f
                )
            }

            com.example.domain.VillagerType.VILLAGE_CHILD -> {
                val clothesColor = if (isNight) Color(0xFFB45309) else Color(0xFFF59E0B)

                // Fast running legs
                val runSwing = sin(walkPhase.toDouble() * 1.5).toFloat() * 9f
                drawScope.drawLine(skinColor, Offset(x - 2f, y - 10f), Offset(x - 2f - runSwing, y), 2.5f)
                drawScope.drawLine(skinColor, Offset(x + 2f, y - 10f), Offset(x + 2f + runSwing, y), 2.5f)

                // Smaller Torso
                drawScope.drawRoundRect(clothesColor, Offset(x - 5f, y - 22f), Size(10f, 12f), CornerRadius(2f, 2f))
                // Head
                drawScope.drawCircle(skinColor, 3.5f, Offset(x, y - 25f))

                // Child playing with a small spinning pinwheel or bamboo propeller
                val stickX = x + dir * 8f
                drawScope.drawLine(Color(0xFF8D6E63), Offset(x, y - 18f), Offset(stickX, y - 26f), 1.6f)
                drawScope.drawCircle(Color(0xFFEF4444), 3.0f, Offset(stickX, y - 26f))
            }
        }
    }

    // -------------------------------------------------------------------------
    // 12. Flock of Birds with Depth & Wing-Flapping (Kawanan Burung Berterbangan)
    // -------------------------------------------------------------------------
    fun drawFlockBird(
        drawScope: DrawScope,
        bird: com.example.domain.FlockBird,
        isNight: Boolean
    ) {
        val x = bird.x
        val y = bird.y
        val scale = bird.scale
        val wingY = sin(bird.wingPhase.toDouble()).toFloat() * (7f * scale)
        val dir = if (bird.speedX >= 0) 1f else -1f

        val birdColor = if (isNight) {
            Color(0xFF1E293B).copy(alpha = 0.75f)
        } else {
            Color(0xFF263238).copy(alpha = 0.85f)
        }

        val wingSpan = 12f * scale
        val path = Path().apply {
            moveTo(x - dir * wingSpan, y + wingY)
            quadraticTo(x - dir * (wingSpan * 0.4f), y - (5f * scale), x, y)
            quadraticTo(x + dir * (wingSpan * 0.4f), y - (5f * scale), x + dir * wingSpan, y + wingY)
        }
        drawScope.drawPath(path, birdColor, style = Stroke(width = 2.2f * scale))

        // Bird body dot
        drawScope.drawCircle(birdColor, 1.8f * scale, Offset(x, y - 1f))
    }
}

private data class BambooSpec(
    val xRatio: Float,
    val height: Float,
    val thickness: Float,
    val tiltAngle: Float,
    val isBackground: Boolean
)
