package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun SoundMixerDialog(
    musicVolume: Float,
    ambientVolume: Float,
    effectsVolume: Float,
    chordPresetIndex: Int,
    onMusicVolumeChange: (Float) -> Unit,
    onAmbientVolumeChange: (Float) -> Unit,
    onEffectsVolumeChange: (Float) -> Unit,
    onChordPresetChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFFFACC15),
            cornerAccentColor = Color(0xFFFEF08A),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("sound_mixer_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelIcons.LoFiRadio(size = 24.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lo-Fi Soundscape",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary
                                )
                            )
                            Text(
                                text = "Calming ambient audio mixer",
                                style = MaterialTheme.typography.bodySmall.copy(color = ArtisticTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF475569))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = ArtisticTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chord progression style presets
                Text(
                    text = "MELODIC PROGRESSION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFFDE047),
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("Serene Dawn", "Moonlight Jazz")
                    presets.forEachIndexed { index, name ->
                        val isSelected = chordPresetIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) Color(0x33FACC15) else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFFFACC15) else Color(0xFF334155)
                                )
                                .clickable { onChordPresetChange(index) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color(0xFFFEF08A) else ArtisticTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Lo-Fi Music Volume Slider
                PixelVolumeRow(
                    label = "Lo-Fi Chords & Melodies",
                    value = musicVolume,
                    barColor = Color(0xFFFACC15),
                    onValueChange = onMusicVolumeChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Ambience Volume Slider
                PixelVolumeRow(
                    label = "Natural Ambience (Rain / Wind)",
                    value = ambientVolume,
                    barColor = Color(0xFF38BDF8),
                    onValueChange = onAmbientVolumeChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Effects Volume Slider
                PixelVolumeRow(
                    label = "Water & Interaction FX",
                    value = effectsVolume,
                    barColor = Color(0xFF4ADE80),
                    onValueChange = onEffectsVolumeChange
                )
            }
        }
    }
}

@Composable
private fun PixelVolumeRow(
    label: String,
    value: Float,
    barColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = ArtisticTextSecondary, fontSize = 11.sp)
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = barColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = barColor,
                activeTrackColor = barColor,
                inactiveTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
