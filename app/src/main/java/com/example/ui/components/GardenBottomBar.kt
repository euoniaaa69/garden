package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LivePlantState
import com.example.model.PlantSpecies
import com.example.ui.theme.ArtisticEmeraldGlow
import com.example.ui.theme.ArtisticGlassBg
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextMuted
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun GardenBottomBar(
    species: PlantSpecies,
    liveState: LivePlantState,
    isRelaxMode: Boolean,
    onWaterClick: () -> Unit,
    onPlantInfoClick: () -> Unit,
    onSeedVaultClick: () -> Unit,
    onSoundMixerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isRelaxMode,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Floating Artistic Plant Card with Frosted Backdrop & Progress
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xCC0B1120),
                border = androidx.compose.foundation.BorderStroke(1.dp, ArtisticGlassBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .clickable { onPlantInfoClick() }
                    .testTag("plant_status_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                ArtisticIndigoPrimary.copy(alpha = 0.4f),
                                                Color(species.primaryColorHex).copy(alpha = 0.2f)
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .border(1.dp, Color(species.primaryColorHex).copy(alpha = 0.7f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Eco,
                                    contentDescription = "Plant Species",
                                    tint = Color(species.primaryColorHex),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = species.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ArtisticTextPrimary
                                    )
                                )
                                Text(
                                    text = "Stage ${liveState.stage} of 5 • ${liveState.stageName}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = FontStyle.Italic,
                                        color = ArtisticIndigoLight.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Water Droplet Button / Indicator
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (liveState.isThirsty) Color(0x33EF4444) else Color(0x2A6366F1),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (liveState.isThirsty) Color(0xFFEF4444) else Color(0xFF818CF8)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onWaterClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WaterDrop,
                                    contentDescription = "Hydration",
                                    tint = if (liveState.isThirsty) Color(0xFFF87171) else Color(0xFF93C5FD),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${(liveState.hydrationLevel * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ArtisticTextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Next Maturity Stage Status & Glowing Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEXT MATURITY STAGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.4.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArtisticTextMuted
                            )
                        )
                        Text(
                            text = if (liveState.isMature) "Fully Blossomed" else "${liveState.timeUntilNextStageFormatted} remaining",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ArtisticEmeraldGlow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { liveState.overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                        color = ArtisticIndigoPrimary,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Toolbar (My Garden, Lo-Fi Soundscape, Water Plant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "My Garden" pill button
                Button(
                    onClick = onSeedVaultClick,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xDD0B1120),
                        contentColor = ArtisticTextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArtisticGlassBorder),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("seed_vault_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Yard,
                        contentDescription = "My Garden",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Garden",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    )
                }

                // Soundscape Mixer button & Primary Water / Nourish button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onSoundMixerClick,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xDD0B1120), CircleShape)
                            .border(1.dp, ArtisticGlassBorder, CircleShape)
                            .testTag("sound_mixer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Radio,
                            contentDescription = "Lo-Fi & Soundscape Mixer",
                            tint = ArtisticTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Primary Artistic Nourish Button with Indigo Gradient
                    Button(
                        onClick = onWaterClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArtisticIndigoPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("water_plant_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = "Water Plant",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onPlantInfoClick,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xDD0B1120), CircleShape)
                            .border(1.dp, ArtisticGlassBorder, CircleShape)
                            .testTag("plant_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Plant Care Details",
                            tint = ArtisticTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
