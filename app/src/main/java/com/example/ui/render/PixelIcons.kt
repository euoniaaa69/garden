package com.example.ui.render

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern, organic rounded vector icons to replace the legacy pixel-art.
 */
object PixelIcons {

    @Composable
    fun WateringCan(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        bodyColor: Color = Color(0xFF38BDF8),
        accentColor: Color = Color(0xFF0288D1),
        highlightColor: Color = Color(0xFFE0F2FE)
    ) {
        Icon(
            imageVector = Icons.Rounded.WaterDrop,
            contentDescription = "Water",
            tint = bodyColor,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun WaterDroplet(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFF38BDF8)
    ) {
        Icon(
            imageVector = Icons.Rounded.WaterDrop,
            contentDescription = "Droplet",
            tint = color,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun Gear(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFFA5B4FC)
    ) {
        Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = "Settings",
            tint = color,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun ZenLotus(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFF34D399),
        leafColor: Color = Color(0xFF34D399)
    ) {
        Icon(
            imageVector = Icons.Rounded.Spa,
            contentDescription = "Zen",
            tint = leafColor,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun SeedSprout(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFF4ADE80),
        leafColor: Color = Color(0xFF4ADE80)
    ) {
        Icon(
            imageVector = Icons.Rounded.Eco,
            contentDescription = "Seed",
            tint = leafColor,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun LoFiRadio(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        color: Color = Color(0xFFFACC15)
    ) {
        Icon(
            imageVector = Icons.Rounded.MusicNote,
            contentDescription = "Music",
            tint = color,
            modifier = modifier.size(size)
        )
    }

    @Composable
    fun FloppyDisk(
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        bodyColor: Color = Color(0xFF34D399),
        accentColor: Color = Color(0xFF059669)
    ) {
        Icon(
            imageVector = Icons.Rounded.Save,
            contentDescription = "Save",
            tint = bodyColor,
            modifier = modifier.size(size)
        )
    }
}
