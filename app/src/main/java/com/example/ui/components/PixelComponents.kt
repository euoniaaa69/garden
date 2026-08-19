package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArtisticDarkCard
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoPrimary

/**
 * Anime RPG Beveled Pixel Frame Container.
 */
@Composable
fun PixelFrame(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xEE0B1120),
    borderColor: Color = Color(0xFF6366F1),
    cornerAccentColor: Color = Color(0xFFA5B4FC),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val strokeW = 3.dp.toPx()
                // Drop shadow pixel block
                drawRect(
                    color = Color(0x88000000),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = size
                )
                // Main box background
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset.Zero,
                    size = size
                )
                // Pixelated Border Lines
                drawRect(
                    color = borderColor,
                    topLeft = Offset.Zero,
                    size = size,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
                )
                // Pixel Corner Accents (Top-Left, Top-Right, Bottom-Left, Bottom-Right)
                val cSize = 6.dp.toPx()
                drawRect(cornerAccentColor, Offset.Zero, Size(cSize, cSize))
                drawRect(cornerAccentColor, Offset(size.width - cSize, 0f), Size(cSize, cSize))
                drawRect(cornerAccentColor, Offset(0f, size.height - cSize), Size(cSize, cSize))
                drawRect(cornerAccentColor, Offset(size.width - cSize, size.height - cSize), Size(cSize, cSize))
            }
            .padding(14.dp),
        content = content
    )
}

/**
 * Floating Action Icon Button with Anime Pixel Art Aesthetic and Press Offset.
 */
@Composable
fun PixelFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    backgroundColor: Color = Color(0xEE0F172A),
    borderColor: Color = Color(0xFF6366F1),
    highlightColor: Color = Color(0xFF818CF8),
    testTag: String = "pixel_floating_button",
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffsetY by animateFloatAsState(
        targetValue = if (isPressed) 3f else 0f,
        animationSpec = tween(60),
        label = "PixelButtonPress"
    )

    Box(
        modifier = modifier
            .size(size)
            .offset(y = pressOffsetY.dp)
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .drawBehind {
                val strokeW = 2.5.dp.toPx()
                // Drop shadow
                if (!isPressed) {
                    drawRect(
                        color = Color(0xAA000000),
                        topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                        size = this.size
                    )
                }
                // Button Body
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset.Zero,
                    size = this.size
                )
                // Pixel Border
                drawRect(
                    color = borderColor,
                    topLeft = Offset.Zero,
                    size = this.size,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
                )
                // Top & Left Pixel Highlights
                drawRect(highlightColor, Offset(strokeW, strokeW), Size(this.size.width - 2 * strokeW, strokeW))
                drawRect(highlightColor, Offset(strokeW, strokeW), Size(strokeW, this.size.height - 2 * strokeW))
            },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

/**
 * Stepped Anime Pixel Progress Bar.
 */
@Composable
fun PixelProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF6366F1),
    trackColor: Color = Color(0xFF1E293B),
    totalSegments: Int = 14
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val filledSegments = (clampedProgress * totalSegments).toInt()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(trackColor)
            .border(1.5.dp, Color(0xFF475569))
            .padding(1.5.dp)
    ) {
        for (i in 0 until totalSegments) {
            val isFilled = i < filledSegments
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(7.dp)
                    .padding(horizontal = 0.5.dp)
                    .background(
                        if (isFilled) barColor else Color.Transparent
                    )
            )
        }
    }
}

/**
 * Anime Pixel Badge / Pill.
 */
@Composable
fun PixelBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xDD0F172A),
    borderColor: Color = Color(0xFF6366F1),
    textColor: Color = Color(0xFFF8FAFC),
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawRect(backgroundColor, Offset.Zero, size)
                drawRect(
                    borderColor,
                    Offset.Zero,
                    size,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx())
                )
            }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontSize = 11.sp,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}
