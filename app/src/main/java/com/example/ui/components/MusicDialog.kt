package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.audio.MusicCatalogue
import com.example.model.AudioPlayerState
import com.example.model.Playlist
import com.example.model.PlaylistStatus
import com.example.ui.theme.ArtisticGlassBorder
import com.example.ui.theme.ArtisticIndigoLight
import com.example.ui.theme.ArtisticIndigoPrimary
import com.example.ui.theme.ArtisticTextPrimary
import com.example.ui.theme.ArtisticTextSecondary
import com.example.ui.util.LocalAppStrings

@Composable
fun MusicDialog(
    playerState: AudioPlayerState,
    onPlayPauseToggle: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onSelectPlaylist: (String) -> Unit,
    onAutoMusicToggle: (Boolean) -> Unit,
    onShuffleToggle: (Boolean) -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onAmbientVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val playlists = MusicCatalogue.ALL_PLAYLISTS

    Dialog(onDismissRequest = onDismiss) {
        PixelFrame(
            backgroundColor = Color(0xF80B1120),
            borderColor = Color(0xFFFACC15),
            cornerAccentColor = Color(0xFFFEF08A),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("music_menu_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                // 1. Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = Color(0x33FACC15),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFACC15))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.MusicNote,
                                    contentDescription = "Music",
                                    tint = Color(0xFFFACC15),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.musicTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ArtisticTextPrimary
                                )
                            )
                            Text(
                                text = strings.musicSubtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = ArtisticTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .border(1.dp, Color(0xFF475569), CircleShape)
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 2. Interactive Audio Player Card
                    item {
                        NowPlayingCard(
                            playerState = playerState,
                            onPlayPauseToggle = onPlayPauseToggle,
                            onNextTrack = onNextTrack,
                            onPrevTrack = onPrevTrack,
                            onShuffleToggle = onShuffleToggle
                        )
                    }

                    // 3. Independent Volume Controls (Music vs Ambience)
                    item {
                        VolumeControlSection(
                            musicVolume = playerState.musicVolume,
                            ambientVolume = playerState.ambientVolume,
                            onMusicVolumeChange = onMusicVolumeChange,
                            onAmbientVolumeChange = onAmbientVolumeChange
                        )
                    }

                    // 4. Auto Music Switch
                    item {
                        AutoMusicBanner(
                            isAutoMusic = playerState.isAutoMusic,
                            onToggle = onAutoMusicToggle
                        )
                    }

                    // 5. Playlist List Header
                    item {
                        Text(
                            text = "PLAYLISTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFFDE047),
                                fontSize = 10.sp,
                                letterSpacing = 1.4.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // 6. Playlists (Lofi, Rainy Day, Night, Nature, Sleep, Japanese Garden)
                    items(playlists) { playlist ->
                        val isActive = playerState.currentPlaylistId == playlist.id
                        PlaylistItemCard(
                            playlist = playlist,
                            isActive = isActive,
                            isPlaying = playerState.isPlaying && isActive,
                            onPlayPause = {
                                if (isActive) {
                                    onPlayPauseToggle()
                                } else {
                                    onSelectPlaylist(playlist.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern Now-Playing Audio Player with progress bar, transport controls, and shuffle toggle.
 */
@Composable
private fun NowPlayingCard(
    playerState: AudioPlayerState,
    onPlayPauseToggle: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onShuffleToggle: (Boolean) -> Unit
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFFFACC15).copy(alpha = 0.3f))
            .border(1.dp, Color(0xFF475569).copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
        color = Color(0xFF0F172A).copy(alpha = 0.9f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.nowPlaying,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFFACC15),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = playerState.currentTrackTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ArtisticTextPrimary,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = playerState.currentPlaylistName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ArtisticIndigoLight,
                            fontSize = 11.sp
                        )
                    )
                }

                // Shuffle button
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onShuffleToggle(!playerState.isShuffle) },
                    color = if (playerState.isShuffle) Color(0x33FACC15) else Color(0xFF1E293B),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (playerState.isShuffle) Color(0xFFFACC15) else Color(0xFF334155)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (playerState.isShuffle) Color(0xFFFEF08A) else ArtisticTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth Progress Bar
            val progress = if (playerState.totalDurationSeconds > 0) {
                (playerState.playbackPositionSeconds / playerState.totalDurationSeconds).coerceIn(0f, 1f)
            } else 0f

            PixelProgressBar(
                progress = progress,
                trackColor = Color(0xFF1E293B),
                barColor = Color(0xFFFACC15),
                height = 6.dp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Time stamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playerState.playbackPositionSeconds.toInt()),
                    style = MaterialTheme.typography.bodySmall.copy(color = ArtisticTextSecondary, fontSize = 10.sp)
                )
                Text(
                    text = formatTime(playerState.totalDurationSeconds.toInt()),
                    style = MaterialTheme.typography.bodySmall.copy(color = ArtisticTextSecondary, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback Controls (Prev, Play/Pause, Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrevTrack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = ArtisticTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0xFFFACC15))
                        .clip(CircleShape)
                        .clickable(onClick = onPlayPauseToggle)
                        .testTag("audio_player_play_pause_button"),
                    color = Color(0xFFFACC15)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onNextTrack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next Track",
                        tint = ArtisticTextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Independent volume control section separating Music and Environmental Ambience.
 */
@Composable
private fun VolumeControlSection(
    musicVolume: Float,
    ambientVolume: Float,
    onMusicVolumeChange: (Float) -> Unit,
    onAmbientVolumeChange: (Float) -> Unit
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
        color = Color(0xFF0F172A).copy(alpha = 0.7f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Music Volume
            VolumeRow(
                label = strings.musicVolume,
                value = musicVolume,
                barColor = Color(0xFFFACC15),
                leadingIcon = {
                    Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(16.dp))
                },
                onValueChange = onMusicVolumeChange
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Ambience Volume
            VolumeRow(
                label = strings.ambientVolume,
                value = ambientVolume,
                barColor = Color(0xFF38BDF8),
                leadingIcon = {
                    Icon(Icons.Rounded.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                },
                onValueChange = onAmbientVolumeChange
            )
        }
    }
}

@Composable
private fun VolumeRow(
    label: String,
    value: Float,
    barColor: Color,
    leadingIcon: @Composable () -> Unit,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = ArtisticTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = barColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
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

/**
 * Auto Music toggle card adapting to weather and day/night.
 */
@Composable
private fun AutoMusicBanner(
    isAutoMusic: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val strings = LocalAppStrings.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isAutoMusic) Color(0xFF6366F1).copy(alpha = 0.8f) else Color(0xFF334155),
                RoundedCornerShape(16.dp)
            ),
        color = if (isAutoMusic) Color(0x226366F1) else Color(0xFF0F172A).copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = if (isAutoMusic) Color(0xFFA5B4FC) else ArtisticTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = strings.autoMusic,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ArtisticTextPrimary,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = strings.autoMusicDesc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ArtisticTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            Switch(
                checked = isAutoMusic,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF6366F1),
                    uncheckedThumbColor = Color(0xFF94A3B8),
                    uncheckedTrackColor = Color(0xFF1E293B)
                )
            )
        }
    }
}

/**
 * Individual Playlist Item Card. Shows Available playlists with Play/Pause or Coming Soon state.
 */
@Composable
private fun PlaylistItemCard(
    playlist: Playlist,
    isActive: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isAvailable = playlist.status == PlaylistStatus.AVAILABLE
    val accentColor = Color(playlist.coverAccentHex)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isActive) 6.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isActive) accentColor else Color.Transparent
            )
            .border(
                width = if (isActive) 1.5.dp else 1.dp,
                color = if (isActive) accentColor else Color(0xFF334155),
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isActive) Color(0xFF1E293B) else Color(0xFF0F172A).copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isAvailable) ArtisticTextPrimary else ArtisticTextSecondary,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isAvailable) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isActive) accentColor.copy(alpha = 0.2f) else Color(0x3310B981)
                        ) {
                            Text(
                                text = if (isActive && isPlaying) strings.activeBadge else strings.available,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isActive) accentColor else Color(0xFF34D399),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x2294A3B8)
                        ) {
                            Text(
                                text = strings.comingSoon,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = playlist.subtitle ?: playlist.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isAvailable) ArtisticTextSecondary else Color(0xFF64748B),
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isAvailable) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${playlist.tracks.size} ${strings.tracksLabel}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button
            if (isAvailable) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onPlayPause),
                    color = if (isActive && isPlaying) accentColor.copy(alpha = 0.25f) else Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isActive) accentColor else Color(0xFF475569)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isActive && isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isActive && isPlaying) "Pause" else "Play",
                            tint = if (isActive) accentColor else ArtisticTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isActive && isPlaying) strings.pause else strings.play,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isActive) accentColor else ArtisticTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
