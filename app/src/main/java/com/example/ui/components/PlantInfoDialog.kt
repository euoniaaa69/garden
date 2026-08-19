package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GardenPlantEntity
import com.example.model.LivePlantState
import com.example.model.PlantCareLogEntity
import com.example.model.PlantSpecies
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticEmerald
import com.example.ui.theme.ArtisticEmeraldGlow
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextMuted
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun PlantInfoDialog(
    plant: GardenPlantEntity,
    species: PlantSpecies,
    liveState: LivePlantState,
    careLogs: List<PlantCareLogEntity> = emptyList(),
    onDismiss: () -> Unit,
    onWaterClick: () -> Unit,
    onReplantClick: () -> Unit,
    onSaveProgress: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFF6366F1),
            cornerAccentColor = Color(0xFFA5B4FC),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("plant_info_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header row with Pixel Icon and Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelIcons.SeedSprout(size = 26.dp, leafColor = Color(species.primaryColorHex))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = species.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary
                                )
                            )
                            Text(
                                text = species.scientificName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = FontStyle.Italic,
                                    color = ArtisticIndigoLight
                                )
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

                Spacer(modifier = Modifier.height(12.dp))

                // Health Status & Hydration Pill Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Health status
                    PixelBadge(
                        text = "Health: ${liveState.healthStatus.label}",
                        backgroundColor = Color(0xFF0F172A),
                        borderColor = Color(liveState.healthStatus.statusColorHex),
                        textColor = Color(liveState.healthStatus.statusColorHex),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color(liveState.healthStatus.statusColorHex),
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Hydration level
                    PixelBadge(
                        text = "Moisture: ${(liveState.hydrationLevel * 100).toInt()}%",
                        backgroundColor = Color(0xFF0F172A),
                        borderColor = if (liveState.isThirsty) Color(0xFFEF4444) else Color(0xFF38BDF8),
                        textColor = if (liveState.isThirsty) Color(0xFFFCA5A5) else Color(0xFFBAE6FD),
                        leadingIcon = {
                            PixelIcons.WaterDroplet(
                                size = 12.dp,
                                color = if (liveState.isThirsty) Color(0xFFEF4444) else Color(0xFF38BDF8)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Species Lore & Description in Pixel Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = species.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = ArtisticTextSecondary, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${species.lore}”",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                color = ArtisticIndigoLight.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stepped Pixel Progress Timeline
                Text(
                    text = "GROWTH TIMELINE (REAL TIME)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArtisticIndigoLight
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                PixelProgressBar(
                    progress = liveState.overallProgress,
                    barColor = Color(species.primaryColorHex),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Age: ${liveState.timeElapsedFormatted}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ArtisticTextSecondary, fontSize = 10.sp)
                    )
                    Text(
                        text = "Next: ${liveState.timeUntilNextStageFormatted}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ArtisticEmeraldGlow,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5 Growth Stages Visual Checklist
                Text(
                    text = "GROWTH PHASES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArtisticIndigoLight
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                species.stageNames.forEachIndexed { index, name ->
                    val stageNumber = index + 1
                    val isReached = liveState.stage >= stageNumber
                    val isCurrent = liveState.stage == stageNumber

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.5.dp)
                            .background(
                                if (isCurrent) Color(0x336366F1) else if (isReached) Color(0x1810B981) else Color(0x0AFFFFFF)
                            )
                            .border(
                                1.dp,
                                if (isCurrent) ArtisticIndigoPrimary else if (isReached) ArtisticEmerald.copy(alpha = 0.4f) else Color(0x15FFFFFF)
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isReached) Icons.Filled.CheckCircle else Icons.Filled.Eco,
                                contentDescription = null,
                                tint = if (isCurrent) ArtisticIndigoLight else if (isReached) ArtisticEmeraldGlow else ArtisticTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Phase $stageNumber: $name",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isReached) ArtisticTextPrimary else ArtisticTextMuted,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = species.stageDescriptions.getOrElse(index) { "" },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ArtisticTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Recent Care & Growth Activity Logs from Room
                if (careLogs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "RECENT CARE LOG (ROOM DB)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    careLogs.take(3).forEach { log ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = log.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ArtisticTextPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = log.healthStatus,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ArtisticEmeraldGlow,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Snapshot Action
                Button(
                    onClick = onSaveProgress,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF065F46)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, Color(0xFF10B981))
                        .testTag("save_progress_dialog_button")
                ) {
                    PixelIcons.FloppyDisk(size = 15.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Save Progress to Room DB",
                        color = Color(0xFFD1FAE5),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onWaterClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1)),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.5.dp, Color(0xFF38BDF8))
                    ) {
                        PixelIcons.WaterDroplet(size = 14.dp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Water Plant",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Button(
                        onClick = onReplantClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.5.dp, Color(0xFF64748B))
                    ) {
                        Text(
                            "New Seed",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = ArtisticTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}
