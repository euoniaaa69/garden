package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlobalTintOverlay
import com.example.ui.components.PixelBadge
import com.example.ui.components.PixelFloatingButton
import com.example.ui.components.PixelFrame
import com.example.ui.components.PixelProgressBar
import com.example.ui.components.PlantInfoDialog
import com.example.ui.components.RelaxModeOverlay
import com.example.ui.components.SeedVaultDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SoundMixerDialog
import com.example.ui.components.WeatherDialog
import com.example.ui.components.WeatherParticleOverlay
import com.example.ui.render.PixelGardenCanvas
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticDarkCard
import com.example.ui.theme.ArtisticEmeraldGlow
import com.example.ui.theme.ArtisticGlassBg
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary
import kotlinx.coroutines.delay

@Composable
fun GardenScreen(
    viewModel: GardenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showPlantInfoDialog by remember { mutableStateOf(false) }
    var showSeedVaultDialog by remember { mutableStateOf(false) }
    var showSoundMixerDialog by remember { mutableStateOf(false) }
    var showWeatherDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Toast notification state
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var showToastBanner by remember { mutableStateOf(false) }

    // Auto-hiding state tracking
    var isUiVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }

    fun registerInteraction() {
        isUiVisible = true
        lastInteractionTime = System.currentTimeMillis()
    }

    fun triggerSaveProgress() {
        registerInteraction()
        viewModel.saveGardenProgress { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            toastMessage = msg
            showToastBanner = true
        }
    }

    // Auto-dismiss in-app toast notification after 3.2s
    LaunchedEffect(showToastBanner) {
        if (showToastBanner) {
            delay(3200)
            showToastBanner = false
        }
    }

    // Auto-hide floating UI after 4.5 seconds of inactivity
    LaunchedEffect(lastInteractionTime, uiState.isRelaxMode) {
        if (!uiState.isRelaxMode && isUiVisible) {
            delay(4500)
            isUiVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    registerInteraction()
                    // Tap on garden gives a gentle plant nourishment bounce
                    viewModel.waterPlant(screenWidthPx * 0.5f, screenHeightPx * 0.72f)
                }
            }
    ) {
        // =====================================================================
        // 1. Main Ambient Garden View Container
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ambient_garden_container")
        ) {
            PixelGardenCanvas(
                dayNightContext = uiState.dayNightContext,
                weatherState = uiState.weatherState,
                species = uiState.species,
                livePlantState = uiState.liveGrowthState,
                isPerformanceMode = uiState.settings.performanceMode,
                particleManager = viewModel.particleManager,
                onPlantTapped = {
                    registerInteraction()
                    viewModel.waterPlant(screenWidthPx * 0.5f, screenHeightPx * 0.72f)
                }
            )

            // Dynamic Global Screen Tinting Overlay for Time-of-Day Lighting
            GlobalTintOverlay(
                dayNightContext = uiState.dayNightContext,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic Weather Particle Overlay (Gentle Rain, God Rays, Sun Motes, Mist)
            WeatherParticleOverlay(
                weatherState = uiState.weatherState,
                isPerformanceMode = uiState.settings.performanceMode,
                sunPositionXRatio = uiState.dayNightContext.sunMoonX,
                sunPositionYRatio = uiState.dayNightContext.sunMoonY,
                modifier = Modifier.fillMaxSize()
            )
        }

        // =====================================================================
        // 2. Minimal Anime Pixel Header (Auto-Hiding)
        // =====================================================================
        AnimatedVisibility(
            visible = isUiVisible && !uiState.isRelaxMode,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(600)),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & Time-of-Day Pixel Badge (Adaptive Color Palette)
                PixelBadge(
                    text = "${uiState.dayNightContext.localTimeFormatted} • ${uiState.dayNightContext.timeOfDay.label}",
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = uiState.dayNightContext.cardBorderColor,
                    textColor = Color(0xFFF8FAFC),
                    modifier = Modifier
                        .clickable { registerInteraction(); showPlantInfoDialog = true }
                        .testTag("pixel_time_badge")
                )

                // Weather Pixel Badge (Clickable to switch weather)
                PixelBadge(
                    text = uiState.weatherState.label,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = Color(0xFF38BDF8),
                    textColor = Color(0xFFE0F2FE),
                    leadingIcon = {
                        PixelIcons.WaterDroplet(size = 14.dp, color = Color(0xFF38BDF8))
                    },
                    modifier = Modifier
                        .clickable { registerInteraction(); showWeatherDialog = true }
                        .testTag("pixel_weather_badge")
                )

                // Manual Save Progress Pixel Badge
                PixelBadge(
                    text = "Save",
                    backgroundColor = Color(0x33065F46),
                    borderColor = Color(0xFF10B981),
                    textColor = Color(0xFFD1FAE5),
                    leadingIcon = {
                        PixelIcons.FloppyDisk(size = 14.dp, bodyColor = Color(0xFF34D399), accentColor = Color(0xFF059669))
                    },
                    modifier = Modifier
                        .clickable { triggerSaveProgress() }
                        .testTag("save_progress_button")
                )
            }
        }

        // =====================================================================
        // 3. Floating Anime Pixel Status Card (Bottom Center, Auto-Hiding)
        // =====================================================================
        AnimatedVisibility(
            visible = isUiVisible && !uiState.isRelaxMode,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(600)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 86.dp)
        ) {
            PixelFrame(
                backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                borderColor = uiState.dayNightContext.cardBorderColor,
                cornerAccentColor = uiState.dayNightContext.cardCornerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { registerInteraction(); showPlantInfoDialog = true }
                    .testTag("pixel_plant_status_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PixelIcons.SeedSprout(size = 20.dp, leafColor = Color(uiState.species.primaryColorHex))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = uiState.species.name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ArtisticTextPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Stage ${uiState.liveGrowthState.stage}/5: ${uiState.liveGrowthState.stageName}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontStyle = FontStyle.Italic,
                                        color = ArtisticIndigoLight,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Hydration Pixel Badge
                        PixelBadge(
                            text = "${(uiState.liveGrowthState.hydrationLevel * 100).toInt()}%",
                            backgroundColor = if (uiState.liveGrowthState.isThirsty) Color(0x33EF4444) else Color(0x330288D1),
                            borderColor = if (uiState.liveGrowthState.isThirsty) Color(0xFFEF4444) else Color(0xFF38BDF8),
                            textColor = if (uiState.liveGrowthState.isThirsty) Color(0xFFFCA5A5) else Color(0xFFE0F2FE),
                            leadingIcon = {
                                PixelIcons.WaterDroplet(
                                    size = 12.dp,
                                    color = if (uiState.liveGrowthState.isThirsty) Color(0xFFEF4444) else Color(0xFF38BDF8)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stepped Anime Pixel Progress Bar
                    PixelProgressBar(
                        progress = uiState.liveGrowthState.overallProgress,
                        barColor = uiState.dayNightContext.cardBorderColor,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }

        // =====================================================================
        // 4. Floating Anime Action Icons (Auto-Hiding)
        // =====================================================================
        AnimatedVisibility(
            visible = isUiVisible && !uiState.isRelaxMode,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(600)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Floating Seed Vault / Garden Species Button
                PixelFloatingButton(
                    onClick = {
                        registerInteraction()
                        showSeedVaultDialog = true
                    },
                    size = 48.dp,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = Color(0xFF4ADE80),
                    highlightColor = Color(0xFF86EFAC),
                    testTag = "floating_seed_vault_button"
                ) {
                    PixelIcons.SeedSprout(size = 22.dp)
                }

                // 2. Floating Soundscape & Lo-Fi Mixer Button
                PixelFloatingButton(
                    onClick = {
                        registerInteraction()
                        showSoundMixerDialog = true
                    },
                    size = 48.dp,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = Color(0xFFFACC15),
                    highlightColor = Color(0xFFFEF08A),
                    testTag = "floating_sound_mixer_button"
                ) {
                    PixelIcons.LoFiRadio(size = 22.dp)
                }

                // 3. Main Floating Action Icon: Watering Can (Prominent)
                PixelFloatingButton(
                    onClick = {
                        registerInteraction()
                        viewModel.waterPlant(screenWidthPx * 0.5f, screenHeightPx * 0.72f)
                    },
                    size = 58.dp,
                    backgroundColor = Color(0xFF0369A1),
                    borderColor = Color(0xFF38BDF8),
                    highlightColor = Color(0xFFBAE6FD),
                    testTag = "floating_water_button"
                ) {
                    PixelIcons.WateringCan(size = 30.dp)
                }

                // 4. Floating Relax / Zen Mode Toggle Button
                PixelFloatingButton(
                    onClick = {
                        registerInteraction()
                        viewModel.toggleRelaxMode()
                    },
                    size = 48.dp,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = Color(0xFF34D399),
                    highlightColor = Color(0xFFA7F3D0),
                    testTag = "floating_relax_mode_button"
                ) {
                    PixelIcons.ZenLotus(size = 22.dp)
                }

                // 5. Floating Save Progress Button
                PixelFloatingButton(
                    onClick = {
                        triggerSaveProgress()
                    },
                    size = 48.dp,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = Color(0xFF10B981),
                    highlightColor = Color(0xFF6EE7B7),
                    testTag = "floating_save_button"
                ) {
                    PixelIcons.FloppyDisk(size = 22.dp)
                }

                // 6. Floating Settings Button
                PixelFloatingButton(
                    onClick = {
                        registerInteraction()
                        showSettingsDialog = true
                    },
                    size = 48.dp,
                    backgroundColor = uiState.dayNightContext.cardBackgroundColor,
                    borderColor = uiState.dayNightContext.cardBorderColor,
                    highlightColor = uiState.dayNightContext.cardCornerColor,
                    testTag = "floating_settings_button"
                ) {
                    PixelIcons.Gear(size = 22.dp)
                }
            }
        }

        // =====================================================================
        // 5. In-App Anime Pixel Toast Notification HUD Banner
        // =====================================================================
        AnimatedVisibility(
            visible = showToastBanner && toastMessage != null,
            enter = fadeIn(animationSpec = tween(250)) + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 58.dp, start = 20.dp, end = 20.dp)
        ) {
            PixelFrame(
                backgroundColor = Color(0xF5064E3B),
                borderColor = Color(0xFF34D399),
                cornerAccentColor = Color(0xFFA7F3D0),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showToastBanner = false }
                    .testTag("save_toast_notification")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelIcons.FloppyDisk(size = 20.dp, bodyColor = Color(0xFF34D399), accentColor = Color(0xFF059669))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = toastMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFECFDF5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // =====================================================================
        // 6. Relax Mode Fullscreen Immersive Overlay
        // =====================================================================
        RelaxModeOverlay(
            isRelaxMode = uiState.isRelaxMode,
            localTimeFormatted = uiState.dayNightContext.localTimeFormatted,
            onExitRelaxMode = {
                registerInteraction()
                viewModel.exitRelaxMode()
            }
        )

        // =====================================================================
        // 7. Anime Pixel Art Dialogs
        // =====================================================================
        if (showPlantInfoDialog && uiState.activePlant != null) {
            PlantInfoDialog(
                plant = uiState.activePlant!!,
                species = uiState.species,
                liveState = uiState.liveGrowthState,
                careLogs = uiState.careLogs,
                onDismiss = { showPlantInfoDialog = false },
                onWaterClick = {
                    registerInteraction()
                    viewModel.waterPlant(screenWidthPx * 0.5f, screenHeightPx * 0.72f)
                    showPlantInfoDialog = false
                },
                onReplantClick = {
                    showPlantInfoDialog = false
                    showSeedVaultDialog = true
                },
                onSaveProgress = {
                    triggerSaveProgress()
                }
            )
        }

        if (showSeedVaultDialog) {
            SeedVaultDialog(
                currentSpeciesId = uiState.species.id,
                onSelectSpecies = { selected ->
                    registerInteraction()
                    viewModel.plantNewSpecies(selected)
                    showSeedVaultDialog = false
                },
                onDismiss = { showSeedVaultDialog = false }
            )
        }

        if (showSoundMixerDialog) {
            SoundMixerDialog(
                musicVolume = uiState.settings.musicVolume,
                ambientVolume = uiState.settings.ambientVolume,
                effectsVolume = uiState.settings.effectsVolume,
                chordPresetIndex = uiState.settings.lofiChordPreset,
                onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                onAmbientVolumeChange = { viewModel.setAmbientVolume(it) },
                onEffectsVolumeChange = { viewModel.setEffectsVolume(it) },
                onChordPresetChange = { viewModel.setChordPreset(it) },
                onDismiss = { showSoundMixerDialog = false }
            )
        }

        if (showWeatherDialog) {
            WeatherDialog(
                currentWeather = uiState.weatherState,
                isAutoMode = uiState.isAutoWeather,
                onSelectWeather = {
                    registerInteraction()
                    viewModel.setWeather(it)
                },
                onSetAutoMode = {
                    registerInteraction()
                    viewModel.setAutoWeather()
                },
                onDismiss = { showWeatherDialog = false }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                isPerformanceMode = uiState.settings.performanceMode,
                timeScaleMultiplier = uiState.settings.timeScaleMultiplier,
                currentTimeOfDayOverride = uiState.timeOfDayOverride,
                activeTimeOfDay = uiState.dayNightContext.timeOfDay,
                onPerformanceModeToggle = { viewModel.setPerformanceMode(it) },
                onTimeScaleChange = { viewModel.setTimeScaleMultiplier(it) },
                onTimeOfDaySelect = {
                    registerInteraction()
                    viewModel.setTimeOfDayOverride(it)
                },
                onSaveProgress = {
                    triggerSaveProgress()
                },
                onResetGarden = {
                    registerInteraction()
                    viewModel.resetGardenPlot()
                    showSettingsDialog = false
                },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}
