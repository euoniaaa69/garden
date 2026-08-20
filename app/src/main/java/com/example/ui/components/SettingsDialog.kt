package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TimeOfDay
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticEmeraldGlow
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

import com.example.ui.util.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    isPerformanceMode: Boolean,
    timeScaleMultiplier: Float,
    currentTimeOfDayOverride: TimeOfDay?,
    activeTimeOfDay: TimeOfDay,
    languageCode: String,
    onPerformanceModeToggle: (Boolean) -> Unit,
    onTimeScaleChange: (Float) -> Unit,
    onTimeOfDaySelect: (TimeOfDay?) -> Unit,
    onLanguageSelect: (String) -> Unit,
    onSaveProgress: () -> Unit = {},
    onResetGarden: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current

    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFF94A3B8),
            cornerAccentColor = Color(0xFFCBD5E1),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelIcons.Gear(size = 22.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ArtisticTextPrimary
                            )
                        )
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

                // Time of Day & Lighting Palette Tinting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.lightingScreenTint,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = activeTimeOfDay.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = activeTimeOfDay.cardBorderColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                Text(
                    text = strings.dynamicColorGrade,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ArtisticTextSecondary,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. Auto Clock Preset
                    val isAutoSelected = currentTimeOfDayOverride == null
                    Box(
                        modifier = Modifier
                            .background(if (isAutoSelected) Color(0x336366F1) else Color(0xFF0F172A))
                            .border(
                                1.dp,
                                if (isAutoSelected) ArtisticIndigoPrimary else Color(0xFF334155)
                            )
                            .clickable { onTimeOfDaySelect(null) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAutoSelected) Color(0xFF10B981) else Color(0xFF64748B))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.autoClock,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAutoSelected) Color(0xFFA5B4FC) else ArtisticTextSecondary,
                                    fontWeight = if (isAutoSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // 2. Presets
                    listOf(
                        TimeOfDay.MORNING,
                        TimeOfDay.AFTERNOON,
                        TimeOfDay.GOLDEN_HOUR,
                        TimeOfDay.SUNSET,
                        TimeOfDay.DUSK,
                        TimeOfDay.NIGHT
                    ).forEach { tod ->
                        val isSelected = currentTimeOfDayOverride == tod
                        Box(
                            modifier = Modifier
                                .background(if (isSelected) tod.cardBackgroundColor else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSelected) tod.cardBorderColor else Color(0xFF334155)
                                )
                                .clickable { onTimeOfDaySelect(tod) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(tod.skyBottomColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tod.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) tod.cardCornerColor else ArtisticTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Language Selection
                Text(
                    text = strings.language,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFFA5B4FC),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isEn = languageCode == "en"
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isEn) Color(0xFF1E1B4B) else Color(0xFF0F172A))
                            .border(1.dp, if (isEn) Color(0xFF6366F1) else Color(0xFF334155))
                            .clickable { onLanguageSelect("en") }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = strings.english,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isEn) Color(0xFFE0E7FF) else ArtisticTextSecondary,
                                fontWeight = if (isEn) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    val isId = languageCode == "id"
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isId) Color(0xFF1E1B4B) else Color(0xFF0F172A))
                            .border(1.dp, if (isId) Color(0xFF6366F1) else Color(0xFF334155))
                            .clickable { onLanguageSelect("id") }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = strings.indonesian,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isId) Color(0xFFE0E7FF) else ArtisticTextSecondary,
                                fontWeight = if (isId) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Performance & Battery Optimization
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BatterySaver,
                                contentDescription = null,
                                tint = ArtisticEmeraldGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = strings.ecoPerformanceMode,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = ArtisticTextPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = strings.ecoDesc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ArtisticTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Switch(
                            checked = isPerformanceMode,
                            onCheckedChange = onPerformanceModeToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Growth Progression Scale
                Text(
                    text = strings.growthTimeScale,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = strings.growthDesc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ArtisticTextSecondary,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val scales = listOf(
                        Pair(1.0f, "1x Real-Time"),
                        Pair(100.0f, "100x Fast"),
                        Pair(1000.0f, "1000x Demo")
                    )

                    scales.forEach { (scaleValue, label) ->
                        val isSelected = kotlin.math.abs(timeScaleMultiplier - scaleValue) < 0.1f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) Color(0x336366F1) else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSelected) ArtisticIndigoPrimary else Color(0xFF334155)
                                )
                                .clickable { onTimeScaleChange(scaleValue) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color(0xFFA5B4FC) else ArtisticTextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Manual Save Progress Action (Room DB)
                Button(
                    onClick = {
                        onSaveProgress()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF10B981))
                        .testTag("save_progress_button")
                ) {
                    PixelIcons.FloppyDisk(size = 16.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.saveProgress,
                        color = Color(0xFFD1FAE5),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reset Plant Action
                Button(
                    onClick = {
                        onResetGarden()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEF4444))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.plantFreshSeed,
                        color = Color(0xFFFCA5A5),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
