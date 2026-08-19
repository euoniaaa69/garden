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
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.WeatherState
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun WeatherDialog(
    currentWeather: WeatherState,
    isAutoMode: Boolean,
    onSelectWeather: (WeatherState) -> Unit,
    onSetAutoMode: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFF38BDF8),
            cornerAccentColor = Color(0xFFBAE6FD),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("weather_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelIcons.WaterDroplet(size = 22.dp, color = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Garden Weather",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary
                                )
                            )
                            Text(
                                text = "Dynamic atmosphere cycle",
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

                // Auto Dynamic Simulation Option
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isAutoMode) Color(0x3338BDF8) else Color(0xFF0F172A))
                        .border(
                            1.dp,
                            if (isAutoMode) Color(0xFF38BDF8) else Color(0xFF334155)
                        )
                        .clickable {
                            onSetAutoMode()
                            onDismiss()
                        }
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoMode,
                            contentDescription = null,
                            tint = if (isAutoMode) Color(0xFF7DD3FC) else ArtisticTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Natural Weather Cycle",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "Gently shifts between clear skies, clouds, and rain over time.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ArtisticTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "MANUAL OVERRIDE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF7DD3FC),
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                WeatherState.entries.forEach { weather ->
                    val isSelected = !isAutoMode && currentWeather == weather
                    val icon = when (weather) {
                        WeatherState.CLEAR -> Icons.Filled.WbSunny
                        WeatherState.CLOUDY -> Icons.Filled.Cloud
                        WeatherState.LIGHT_RAIN, WeatherState.RAIN -> Icons.Filled.WaterDrop
                        WeatherState.FOG -> Icons.Filled.FilterDrama
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.5.dp)
                            .background(if (isSelected) Color(0x330288D1) else Color(0xFF0F172A))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B)
                            )
                            .clickable {
                                onSelectWeather(weather)
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF7DD3FC) else ArtisticTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = weather.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) ArtisticTextPrimary else ArtisticTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = weather.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ArtisticTextSecondary.copy(alpha = 0.8f),
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
