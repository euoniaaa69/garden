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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.model.PlantCatalogue
import com.example.model.PlantSpecies
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticEmeraldGlow
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun SeedVaultDialog(
    currentSpeciesId: String,
    onSelectSpecies: (PlantSpecies) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFF4ADE80),
            cornerAccentColor = Color(0xFF86EFAC),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("seed_vault_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelIcons.SeedSprout(size = 24.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Seed Vault",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary
                                )
                            )
                            Text(
                                text = "Select a botanical seed to cultivate",
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

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(PlantCatalogue.SPECIES_LIST) { species ->
                        val isSelected = species.id == currentSpeciesId

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Color(0x334ADE80) else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF4ADE80) else Color(0xFF334155)
                                )
                                .clickable {
                                    onSelectSpecies(species)
                                    onDismiss()
                                }
                                .padding(12.dp)
                                .testTag("seed_item_${species.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PixelIcons.SeedSprout(size = 22.dp, leafColor = Color(species.primaryColorHex))

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = species.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = ArtisticTextPrimary,
                                                fontSize = 13.sp
                                            )
                                        )
                                        if (isSelected) {
                                            Text(
                                                text = "Active",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ArtisticEmeraldGlow,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Text(
                                        text = species.scientificName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontStyle = FontStyle.Italic,
                                            color = ArtisticIndigoLight,
                                            fontSize = 11.sp
                                        )
                                    )

                                    val days = species.growthDurationMillis / (24 * 60 * 60 * 1000L)
                                    Text(
                                        text = "Full Maturity: ~$days real-time days",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ArtisticTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
