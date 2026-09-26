package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.DeviceType
import com.example.model.PlaybackState
import com.example.model.Track
import com.example.ui.theme.LocalAccentColor

@Composable
fun PersistentMiniPlayer(
    playbackState: PlaybackState,
    onExpand: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekToFraction: (Float) -> Unit,
    onToggleLike: (Track) -> Unit,
    onOpenDevices: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack ?: return
    val accentColor = LocalAccentColor.current

    val rawProgress = if (playbackState.durationMs > 0) {
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs).coerceIn(0f, 1f)
    } else 0f

    val isRemote = playbackState.currentDevice?.type != null &&
            playbackState.currentDevice.type != DeviceType.SMARTPHONE

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("persistent_mini_player"),
        shape = RoundedCornerShape(12.dp),
        onClick = onExpand
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = track.coverUrl,
                        contentDescription = "Cover for ${track.title}",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Track Title, Artist, and Device
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = track.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (playbackState.isPlaying) {
                            Spacer(modifier = Modifier.width(6.dp))
                            AudioEqualizerBars(
                                isPlaying = true,
                                barCount = 3,
                                barWidth = 2.dp,
                                maxHeight = 12.dp,
                                barColor = accentColor
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        if (isRemote) {
                            Icon(
                                imageVector = Icons.Default.CastConnected,
                                contentDescription = "Playing on remote device",
                                tint = accentColor,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${playbackState.currentDevice?.name}",
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "${track.artist} • ${formatTimeMs(playbackState.currentPositionMs)} / ${formatTimeMs(track.durationMs)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Device Connect button
                IconButton(
                    onClick = onOpenDevices,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_devices")
                ) {
                    Icon(
                        imageVector = if (isRemote) Icons.Default.CastConnected else Icons.Default.Devices,
                        contentDescription = "Devices",
                        tint = if (isRemote) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Like heart button
                IconButton(
                    onClick = { onToggleLike(track) },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_like")
                ) {
                    Icon(
                        imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (track.isLiked) "Unlike" else "Like",
                        tint = if (track.isLiked) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Skip Previous button
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_skip_prev")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Play / Pause button
                Surface(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    color = accentColor,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_play_pause")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Skip Next button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_skip_next")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Interactive Progress Tracking Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val width = size.width.toFloat()
                            if (width > 0) {
                                val fraction = (offset.x / width).coerceIn(0f, 1f)
                                onSeekToFraction(fraction)
                            }
                        }
                    }
                    .testTag("mini_player_progress")
            ) {
                LinearProgressIndicator(
                    progress = { rawProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            }
        }
    }
}
