package com.example.ui.render

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Procedural retro anime pixel-art glyphs drawn directly on Compose Canvas.
 */
object PixelIcons {

    /**
     * Cute Pixel Art Watering Can
     */
    @Composable
    fun WateringCan(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        bodyColor: Color = Color(0xFF38BDF8),
        accentColor: Color = Color(0xFF0288D1),
        highlightColor: Color = Color(0xFFE0F2FE)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 16f

            fun px(gridX: Int, gridY: Int, col: Color, w: Int = 1, h: Int = 1) {
                drawRect(col, Offset(gridX * s, gridY * s), Size(w * s, h * s))
            }

            // Can Body
            px(5, 7, bodyColor, 7, 7)
            px(5, 7, highlightColor, 6, 1)
            px(5, 7, highlightColor, 1, 6)
            px(11, 8, accentColor, 1, 6)
            px(6, 13, accentColor, 6, 1)

            // Spout
            px(12, 8, bodyColor, 2, 2)
            px(13, 6, bodyColor, 2, 2)
            px(14, 4, highlightColor, 2, 2)
            // Water droplets from spout
            px(15, 2, Color(0xFFBAE6FD), 1, 1)

            // Handle
            px(3, 8, bodyColor, 2, 5)
            px(2, 9, accentColor, 1, 3)
            px(3, 7, bodyColor, 2, 1)

            // Top Opening
            px(7, 5, accentColor, 3, 2)
        }
    }

    /**
     * Pixel Art Water Droplet
     */
    @Composable
    fun WaterDroplet(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFF38BDF8),
        highlight: Color = Color(0xFFE0F2FE)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 12f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Tip
            px(5, 1, color, 2, 2)
            px(4, 3, color, 4, 2)
            px(3, 5, color, 6, 2)
            px(2, 7, color, 8, 3)
            px(3, 10, color, 6, 1)
            px(4, 11, color, 4, 1)
            // Highlight shine
            px(4, 4, highlight, 2, 1)
            px(4, 5, highlight, 1, 2)
        }
    }

    /**
     * Pixel Art Gear / Settings Cog
     */
    @Composable
    fun Gear(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFFCBD5E1),
        accent: Color = Color(0xFF64748B)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 14f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Center ring
            px(4, 4, color, 6, 6)
            px(6, 6, Color(0xFF0F172A), 2, 2) // Hole

            // Teeth
            px(6, 1, accent, 2, 3) // Top
            px(6, 10, accent, 2, 3) // Bottom
            px(1, 6, accent, 3, 2) // Left
            px(10, 6, accent, 3, 2) // Right

            // Diagonals
            px(2, 2, color, 2, 2)
            px(10, 2, color, 2, 2)
            px(2, 10, color, 2, 2)
            px(10, 10, color, 2, 2)
        }
    }

    /**
     * Pixel Art Zen / Moon / Lotus Icon for Relax Mode
     */
    @Composable
    fun ZenLotus(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        petalColor: Color = Color(0xFF34D399),
        accentColor: Color = Color(0xFF059669)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 14f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Center Petal
            px(6, 2, Color(0xFFA7F3D0), 2, 2)
            px(6, 4, petalColor, 2, 5)

            // Left Petal
            px(3, 5, Color(0xFFA7F3D0), 2, 2)
            px(4, 7, petalColor, 2, 3)

            // Right Petal
            px(9, 5, Color(0xFFA7F3D0), 2, 2)
            px(8, 7, petalColor, 2, 3)

            // Base Leaf
            px(2, 10, accentColor, 10, 2)
            px(4, 12, accentColor, 6, 1)
        }
    }

    /**
     * Pixel Art Sprout / Seed Vault Icon
     */
    @Composable
    fun SeedSprout(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        leafColor: Color = Color(0xFF4ADE80),
        stemColor: Color = Color(0xFF16A34A)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 14f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Left Leaf
            px(2, 3, leafColor, 3, 2)
            px(1, 4, leafColor, 2, 2)
            px(3, 5, leafColor, 2, 1)

            // Right Leaf
            px(9, 2, leafColor, 3, 2)
            px(10, 3, leafColor, 3, 2)
            px(9, 5, leafColor, 2, 1)

            // Stem
            px(6, 5, stemColor, 2, 6)
            px(7, 4, stemColor, 1, 2)

            // Soil base
            px(4, 11, Color(0xFF78350F), 6, 2)
        }
    }

    /**
     * Pixel Art Sound / Lo-Fi Radio Icon
     */
    @Composable
    fun LoFiRadio(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        bodyColor: Color = Color(0xFFFDE047),
        accentColor: Color = Color(0xFFCA8A04)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 14f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Antenna
            px(3, 1, accentColor, 1, 3)
            px(4, 3, accentColor, 1, 2)

            // Body
            px(2, 5, bodyColor, 10, 7)
            px(2, 5, Color(0xFFFEF08A), 9, 1) // top highlight
            px(2, 11, accentColor, 10, 1) // bottom shade

            // Speaker Grill
            px(4, 7, Color(0xFF713F12), 3, 3)
            // Dial
            px(9, 7, Color(0xFF713F12), 2, 2)
        }
    }

    /**
     * Retro 8-bit Pixel Art Floppy Disk / Save Progress Glyph
     */
    @Composable
    fun FloppyDisk(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        bodyColor: Color = Color(0xFF10B981),
        accentColor: Color = Color(0xFF047857),
        labelColor: Color = Color(0xFFF1F5F9)
    ) {
        Canvas(modifier = modifier.size(size)) {
            val s = this.size.width / 14f
            fun px(gx: Int, gy: Int, c: Color, w: Int = 1, h: Int = 1) {
                drawRect(c, Offset(gx * s, gy * s), Size(w * s, h * s))
            }
            // Floppy Body
            px(2, 2, bodyColor, 9, 10)
            px(11, 4, bodyColor, 1, 8)
            // Top Cut Corner
            px(10, 2, accentColor, 1, 1)

            // Metal Shutter Slider at Top
            px(4, 2, Color(0xFFCBD5E1), 5, 4)
            px(5, 3, Color(0xFF334155), 2, 2)

            // Paper Label on Bottom
            px(3, 7, labelColor, 8, 4)
            px(4, 8, accentColor, 6, 1) // line on label
            px(4, 10, accentColor, 4, 1)

            // Edge Shadow
            px(2, 11, accentColor, 10, 1)
            px(11, 4, accentColor, 1, 8)
        }
    }
}

