package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.Track
import com.example.ui.components.AudioEqualizerBars
import com.example.ui.components.formatTimeMs
import com.example.ui.theme.SpotifyBlack
import com.example.ui.theme.SpotifyCardGray
import com.example.ui.theme.SpotifyDarkCharcoal
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Calendar

@Composable
fun HomeScreen(
    playlists: List<Playlist>,
    tracks: List<Track>,
    playbackState: PlaybackState,
    onTrackSelect: (Track) -> Unit,
    onPlaylistSelect: (Playlist) -> Unit,
    onToggleLike: (Track) -> Unit,
    onOpenDevices: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val isRemote = playbackState.currentDevice?.type != null &&
            playbackState.currentDevice.type != DeviceType.SMARTPHONE

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF283E34), SpotifyBlack)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = greeting,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Spotify Connect / Cross-Device Quick Pill
                        Surface(
                            onClick = onOpenDevices,
                            shape = RoundedCornerShape(16.dp),
                            color = if (isRemote) SpotifyGreen.copy(alpha = 0.2f) else Color(0xFF2A2A2A),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isRemote) Icons.Default.CastConnected else Icons.Default.Devices,
                                    contentDescription = "Devices",
                                    tint = if (isRemote) SpotifyGreen else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                if (isRemote) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Connected",
                                        color = SpotifyGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier.testTag("home_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Music", "Podcasts", "Audiobooks").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SpotifyGreen else Color(0xFF2A2A2A)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Play 6-Grid (Spotify's signature 2x3 cards)
                val quickItems = playlists.take(6)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in quickItems.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickPlayCard(
                                playlist = quickItems[i],
                                isPlayingThis = playbackState.isPlaying && playbackState.currentTrack?.genre == quickItems[i].title,
                                onClick = { onPlaylistSelect(quickItems[i]) },
                                modifier = Modifier.weight(1f)
                            )
                            if (i + 1 < quickItems.size) {
                                QuickPlayCard(
                                    playlist = quickItems[i + 1],
                                    isPlayingThis = false,
                                    onClick = { onPlaylistSelect(quickItems[i + 1]) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Made For You / Featured Playlists Carousel
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "Made For You",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistCard(
                            playlist = playlist,
                            onClick = { onPlaylistSelect(playlist) }
                        )
                    }
                }
            }
        }

        // Jump Back In (Recently played / curated tracks)
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "Jump Back In",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(tracks) { track ->
                        TrackCarouselCard(
                            track = track,
                            isCurrent = playbackState.currentTrack?.id == track.id,
                            isPlaying = playbackState.isPlaying && playbackState.currentTrack?.id == track.id,
                            onClick = { onTrackSelect(track) }
                        )
                    }
                }
            }
        }

        // Trending Tracks List
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trending Right Now",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Global Charts",
                        color = SpotifyGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        itemsIndexed(tracks) { index, track ->
            val isCurrent = playbackState.currentTrack?.id == track.id
            TrackListRow(
                track = track,
                rank = index + 1,
                isCurrent = isCurrent,
                isPlaying = isCurrent && playbackState.isPlaying,
                onSelect = { onTrackSelect(track) },
                onToggleLike = { onToggleLike(track) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun QuickPlayCard(
    playlist: Playlist,
    isPlayingThis: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(6.dp)),
        color = Color(0xFF282828)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = playlist.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = playlist.title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            )

            if (isPlayingThis) {
                AudioEqualizerBars(
                    isPlaying = true,
                    barCount = 3,
                    barWidth = 2.dp,
                    maxHeight = 12.dp,
                    barColor = SpotifyGreen,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = playlist.coverUrl,
            contentDescription = playlist.title,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = playlist.title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = playlist.description,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun TrackCarouselCard(
    track: Track,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = track.coverUrl,
                contentDescription = track.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    AudioEqualizerBars(
                        isPlaying = true,
                        barCount = 4,
                        barWidth = 3.dp,
                        maxHeight = 22.dp,
                        barColor = SpotifyGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = track.title,
            color = if (isCurrent) SpotifyGreen else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = track.artist,
            color = TextSecondary,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TrackListRow(
    track: Track,
    rank: Int,
    isCurrent: Boolean,
    isPlaying: Boolean,
    onSelect: () -> Unit,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onSelect,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .testTag("track_row_${track.id}"),
        color = if (isCurrent) Color(0xFF222222) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank or Equalizer
            Box(
                modifier = Modifier.width(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    AudioEqualizerBars(
                        isPlaying = true,
                        barCount = 3,
                        barWidth = 2.dp,
                        maxHeight = 14.dp,
                        barColor = SpotifyGreen
                    )
                } else {
                    Text(
                        text = "$rank",
                        color = if (isCurrent) SpotifyGreen else TextTertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Cover
            AsyncImage(
                model = track.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Title & Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = if (isCurrent) SpotifyGreen else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${track.genre}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Duration
            Text(
                text = formatTimeMs(track.durationMs),
                color = TextTertiary,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Like heart
            IconButton(
                onClick = onToggleLike,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (track.isLiked) SpotifyGreen else TextTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
