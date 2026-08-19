package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.domain.DayNightContext
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import com.example.ui.theme.ArtisticGlassBg
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary

@Composable
fun GardenTopBar(
    dayNightContext: DayNightContext,
    weatherState: WeatherState,
    isRelaxMode: Boolean,
    onWeatherClick: () -> Unit,
    onRelaxModeToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isRelaxMode,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Pill: Artistic Local Time & Celestial State
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = ArtisticGlassBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ArtisticGlassBorder),
                modifier = Modifier.testTag("time_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeIcon = when (dayNightContext.timeOfDay) {
                        TimeOfDay.DAWN, TimeOfDay.MORNING, TimeOfDay.AFTERNOON, TimeOfDay.GOLDEN_HOUR -> Icons.Filled.WbSunny
                        TimeOfDay.SUNSET, TimeOfDay.DUSK -> Icons.Filled.NightsStay
                        TimeOfDay.NIGHT, TimeOfDay.MIDNIGHT -> Icons.Filled.Bedtime
                    }
                    val iconTint = if (dayNightContext.timeOfDay.isNight) Color(0xFFA5B4FC) else Color(0xFFFBBF24)

                    Icon(
                        imageVector = timeIcon,
                        contentDescription = "Time of Day",
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LOCAL TIME",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.6.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArtisticIndigoLight.copy(alpha = 0.85f)
                            )
                        )
                        Text(
                            text = "${dayNightContext.localTimeFormatted} • ${dayNightContext.timeOfDay.label}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ArtisticTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Right Action Controls: Weather Pill, Relax Mode, Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weather Pill
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = ArtisticGlassBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArtisticGlassBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onWeatherClick() }
                        .testTag("weather_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val weatherIcon = when (weatherState) {
                            WeatherState.CLEAR -> Icons.Filled.WbSunny
                            WeatherState.CLOUDY, WeatherState.FOG -> Icons.Filled.Cloud
                            WeatherState.LIGHT_RAIN, WeatherState.RAIN -> Icons.Filled.WaterDrop
                        }
                        Icon(
                            imageVector = weatherIcon,
                            contentDescription = "Weather: ${weatherState.label}",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = weatherState.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = ArtisticTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Zen / Relax Mode Quick Button
                IconButton(
                    onClick = onRelaxModeToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .background(ArtisticGlassBg, CircleShape)
                        .border(1.dp, ArtisticGlassBorder, CircleShape)
                        .testTag("relax_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SelfImprovement,
                        contentDescription = "Zen Relaxation Mode",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(ArtisticGlassBg, CircleShape)
                        .border(1.dp, ArtisticGlassBorder, CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = ArtisticTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
