package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.DayNightContext
import com.example.model.TimeOfDay
import com.example.model.WeatherState
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoPrimary

/**
 * An overlay summary card that displays the current weather condition and the time of day,
 * updated periodically with real-time feedback and expandable detail views.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherTimeSummaryCard(
    dayNightContext: DayNightContext,
    weatherState: WeatherState,
    isAutoWeather: Boolean,
    modifier: Modifier = Modifier,
    onOpenWeatherDialog: () -> Unit = {},
    onOpenTimeDialog: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Pulsing live synchronization indicator
    val infiniteTransition = rememberInfiniteTransition(label = "periodic_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse_alpha"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = dayNightContext.cardBorderColor,
        animationSpec = tween(durationMillis = 500),
        label = "summary_card_border_color"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = dayNightContext.cardBackgroundColor.copy(alpha = 0.90f),
        animationSpec = tween(durationMillis = 500),
        label = "summary_card_bg_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = if (isExpanded) 14.dp else 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = animatedBorderColor.copy(alpha = 0.45f)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        animatedBorderColor.copy(alpha = 0.8f),
                        animatedBorderColor.copy(alpha = 0.3f),
                        animatedBorderColor.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
            .testTag("weather_time_summary_overlay_card"),
        color = animatedBgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // =====================================================================
            // 1. Header Bar: Compact Summary & Expand Toggle
            // =====================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .testTag("expand_collapse_summary_button"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & Sun/Moon Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Celestial Icon Orb (Sun or Moon)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (dayNightContext.isSun) Color(0xFFF59E0B).copy(alpha = 0.25f)
                                else Color(0xFF6366F1).copy(alpha = 0.25f)
                            )
                            .border(
                                1.dp,
                                if (dayNightContext.isSun) Color(0xFFFBBF24) else Color(0xFFA5B4FC),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dayNightContext.isSun) "☀️" else "🌙",
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.testTag("time_of_day_display")) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dayNightContext.localTimeWithSeconds.ifEmpty { dayNightContext.localTimeFormatted },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Time of day pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(animatedBorderColor.copy(alpha = 0.2f))
                                    .border(1.dp, animatedBorderColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = dayNightContext.timeOfDay.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFE2E8F0),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = dayNightContext.moodTitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Weather Condition Badge (Right Side)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("weather_condition_display")
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0284C7).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val weatherEmoji = when (weatherState) {
                                WeatherState.CLEAR -> if (dayNightContext.isSun) "☀️" else "✨"
                                WeatherState.CLOUDY -> "⛅"
                                WeatherState.LIGHT_RAIN -> "🌦️"
                                WeatherState.RAIN -> "🌧️"
                                WeatherState.FOG -> "🌫️"
                            }
                            Text(text = weatherEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weatherState.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFFE0F2FE),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Expand / Collapse Chevron indicator
                    Text(
                        text = if (isExpanded) "▲" else "▼",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            // =====================================================================
            // 2. Expanded Detail Overview (Periodic Dynamics & Atmospheric Data)
            // =====================================================================
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(250)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(
                        color = Color(0xFF334155).copy(alpha = 0.6f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Atmosphere Poetic Subtitle
                    Text(
                        text = "Atmosphere: \"${dayNightContext.moodDescription}\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Weather & Lighting Metrics Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Ambient Illumination
                        MetricChip(
                            label = "Ambient Light",
                            value = "${(dayNightContext.ambientLight * 100).toInt()}%",
                            icon = "💡",
                            accentColor = Color(0xFFFBBF24)
                        )

                        // 2. Weather Precipitation
                        MetricChip(
                            label = "Precipitation",
                            value = if (weatherState.rainDropCount > 0) "${weatherState.rainDropCount} drops/s" else "None",
                            icon = "💧",
                            accentColor = Color(0xFF38BDF8)
                        )

                        // 3. Cloud Coverage
                        MetricChip(
                            label = "Cloud Density",
                            value = "${weatherState.cloudDensity}/12 index",
                            icon = "☁️",
                            accentColor = Color(0xFFA5B4FC)
                        )

                        // 4. Fog & Atmospheric Mist
                        MetricChip(
                            label = "Mist / Fog",
                            value = if (weatherState.fogAlpha > 0f) "${(weatherState.fogAlpha * 100).toInt()}% mist" else "Clear",
                            icon = "🌫️",
                            accentColor = Color(0xFF94A3B8)
                        )

                        // 5. Fireflies / Starlight
                        if (dayNightContext.timeOfDay.isNight) {
                            MetricChip(
                                label = "Night Life",
                                value = "${dayNightContext.fireflyCount} fireflies • ${dayNightContext.timeOfDay.starCount}★",
                                icon = "✨",
                                accentColor = Color(0xFF34D399)
                            )
                        } else {
                            MetricChip(
                                label = "Solar Arc",
                                value = if (dayNightContext.sunMoonY < 0.35f) "High Zenith" else "Low Horizon",
                                icon = "🌅",
                                accentColor = Color(0xFFFB923C)
                            )
                        }

                        // 6. Simulation Mode
                        MetricChip(
                            label = "Weather Mode",
                            value = if (isAutoWeather) "Auto Natural Cycle" else "Manual Preset",
                            icon = "⚙️",
                            accentColor = Color(0xFF818CF8)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row & Live Pulse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Periodic Live Sync Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("periodic_update_badge")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = pulseAlpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE • Ticking 1s",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Quick Control Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Weather Dialog Button
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenWeatherDialog() }
                                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .testTag("summary_switch_weather_btn"),
                                color = Color(0x330284C7)
                            ) {
                                Text(
                                    text = "Weather",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFBAE6FD),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            // Time Dialog Button
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenTimeDialog() }
                                    .border(1.dp, animatedBorderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .testTag("summary_switch_time_btn"),
                                color = animatedBorderColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Time",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFF1F5F9),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    icon: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.7f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 9.sp
                    )
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFF8FAFC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
