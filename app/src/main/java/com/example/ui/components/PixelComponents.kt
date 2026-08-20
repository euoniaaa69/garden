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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArtisticDarkCard
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoPrimary

/**
 * Modern, organic rounded Glass Container.
 * Replaces the legacy Anime RPG Beveled Pixel Frame Container.
 */
@Composable
fun PixelFrame(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xEE0B1120),
    borderColor: Color = Color(0xFF6366F1),
    cornerAccentColor: Color = Color(0xFFA5B4FC), // Used as a subtle tint instead of a blocky accent
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp), spotColor = borderColor.copy(alpha = 0.5f))
            .border(1.5.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp)),
        color = backgroundColor.copy(alpha = 0.85f),
    ) {
        Box(
            modifier = Modifier
                .background(cornerAccentColor.copy(alpha = 0.05f))
                .padding(20.dp),
            contentAlignment = Alignment.TopStart
        ) {
            content()
        }
    }
}

/**
 * Modern floating button. Replaces old retro pixel button.
 */
@Composable
fun PixelFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    backgroundColor: Color = Color(0xFF1E293B),
    borderColor: Color = Color(0xFF38BDF8),
    highlightColor: Color = Color(0xFFBAE6FD),
    testTag: String = "",
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "fab_scale"
    )

    val shadowElevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = tween(durationMillis = 150),
        label = "fab_shadow"
    )

    Surface(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = shadowElevation.dp, 
                shape = CircleShape, 
                spotColor = highlightColor.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
            .border(1.dp, borderColor.copy(alpha = 0.5f), CircleShape),
        color = backgroundColor,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.background(highlightColor.copy(alpha = if (isPressed) 0.2f else 0.05f))
        ) {
            Box(modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }) {
                content()
            }
        }
    }
}

/**
 * Modern Glass/Rounded Chip. Replaces the retro UI Badge.
 */
@Composable
fun PixelBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF1E293B),
    borderColor: Color = Color(0xFF475569),
    textColor: Color = Color.White,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = borderColor)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        color = backgroundColor.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

/**
 * Smooth modern progress bar. Replaces the blocky step progress indicator.
 */
@Composable
fun PixelProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = Color(0xFF0F172A),
    barColor: Color = Color(0xFF38BDF8),
    height: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(height)
                .clip(CircleShape)
                .background(barColor)
        )
    }
}
