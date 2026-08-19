package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.render.PixelIcons
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary
import kotlinx.coroutines.delay

@Composable
fun RelaxModeOverlay(
    isRelaxMode: Boolean,
    localTimeFormatted: String,
    onExitRelaxMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHint by remember { mutableStateOf(true) }

    LaunchedEffect(isRelaxMode) {
        if (isRelaxMode) {
            showHint = true
            delay(3500)
            showHint = false
        }
    }

    AnimatedVisibility(
        visible = isRelaxMode,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onExitRelaxMode() }
                .testTag("relax_mode_overlay")
        ) {
            // Subtle Pixel Zen Clock at bottom center
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            ) {
                PixelBadge(
                    text = "$localTimeFormatted  •  Tap to resume",
                    backgroundColor = Color(0xF00B1120),
                    borderColor = Color(0xFF34D399),
                    textColor = Color(0xFFF8FAFC),
                    leadingIcon = {
                        PixelIcons.ZenLotus(size = 14.dp)
                    },
                    modifier = Modifier.testTag("exit_relax_mode_button")
                )
            }

            // Brief initial fade-out tip
            AnimatedVisibility(
                visible = showHint,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 28.dp)
            ) {
                PixelBadge(
                    text = "Zen Relax Mode • Pure ambient plant growth",
                    backgroundColor = Color(0xF00B1120),
                    borderColor = Color(0xFF6366F1),
                    textColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
