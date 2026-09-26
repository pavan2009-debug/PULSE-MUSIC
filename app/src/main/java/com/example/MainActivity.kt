package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Playlist
import com.example.ui.components.DeviceConnectSheet
import com.example.ui.components.NowPlayingScreen
import com.example.ui.components.PersistentMiniPlayer
import com.example.ui.screens.DevicesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AudioPlayerViewModel
import com.example.viewmodel.MusicViewModel

enum class NavTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "tab_home"),
    SEARCH("Search", Icons.Default.Search, "tab_search"),
    LIBRARY("Your Library", Icons.Default.LibraryMusic, "tab_library"),
    CONNECT("Connect", Icons.Default.CastConnected, "tab_devices")
}

class MainActivity : ComponentActivity() {

    private val musicViewModel: MusicViewModel by viewModels()
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by musicViewModel.themeMode.collectAsStateWithLifecycle()
            val glassEffect by musicViewModel.glassEffect.collectAsStateWithLifecycle()
            val accentColorHex by musicViewModel.accentColorHex.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = themeMode,
                glassEffect = glassEffect,
                accentColor = Color(accentColorHex)
            ) {
                PulseMusicApp(
                    musicViewModel = musicViewModel,
                    audioPlayerViewModel = audioPlayerViewModel
                )
            }
        }
    }
}

@Composable
fun PulseMusicApp(
    musicViewModel: MusicViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }
    var showDeviceSheet by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var selectedPlaylistDetail by remember { mutableStateOf<Playlist?>(null) }

    val allTracks by musicViewModel.allTracks.collectAsStateWithLifecycle()
    val likedTracks by musicViewModel.likedTracks.collectAsStateWithLifecycle()
    val downloadedTracks by musicViewModel.downloadedTracks.collectAsStateWithLifecycle()
    val playlists by musicViewModel.playlists.collectAsStateWithLifecycle()
    val devices by musicViewModel.devices.collectAsStateWithLifecycle()
    val playbackState by audioPlayerViewModel.playbackState.collectAsStateWithLifecycle()
    val searchQuery by musicViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by musicViewModel.selectedGenre.collectAsStateWithLifecycle()
    val searchResults by musicViewModel.searchResults.collectAsStateWithLifecycle()

    // Settings & Account States
    val userAccount by musicViewModel.currentUser.collectAsStateWithLifecycle()
    val themeMode by musicViewModel.themeMode.collectAsStateWithLifecycle()
    val glassEffect by musicViewModel.glassEffect.collectAsStateWithLifecycle()
    val accentColorHex by musicViewModel.accentColorHex.collectAsStateWithLifecycle()
    val downloadQuality by musicViewModel.downloadQuality.collectAsStateWithLifecycle()
    val streamingQuality by musicViewModel.streamingQuality.collectAsStateWithLifecycle()
    val downloadOverWifiOnly by musicViewModel.downloadOverWifiOnly.collectAsStateWithLifecycle()
    val offlineModeOnly by musicViewModel.offlineModeOnly.collectAsStateWithLifecycle()
    val storageStats by musicViewModel.storageStats.collectAsStateWithLifecycle()

    val currentAccentColor = Color(accentColorHex)

    // Back handling
    BackHandler(enabled = isSettingsOpen || isNowPlayingExpanded || selectedPlaylistDetail != null) {
        if (isSettingsOpen) {
            isSettingsOpen = false
        } else if (isNowPlayingExpanded) {
            isNowPlayingExpanded = false
        } else if (selectedPlaylistDetail != null) {
            selectedPlaylistDetail = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!isSettingsOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                ) {
                    // Persistent Mini Player with Play, Pause, Skip Next, Skip Prev, and Progress Tracking
                    if (playbackState.currentTrack != null) {
                        PersistentMiniPlayer(
                            playbackState = playbackState,
                            onExpand = { isNowPlayingExpanded = true },
                            onTogglePlayPause = { audioPlayerViewModel.togglePlayPause() },
                            onSkipNext = { audioPlayerViewModel.skipNext() },
                            onSkipPrevious = { audioPlayerViewModel.skipPrevious() },
                            onSeekToFraction = { fraction -> audioPlayerViewModel.seekToFraction(fraction) },
                            onToggleLike = { track -> audioPlayerViewModel.toggleLike(track) },
                            onOpenDevices = { showDeviceSheet = true }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        NavTab.entries.forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    selectedTab = tab
                                    selectedPlaylistDetail = null
                                    isSettingsOpen = false
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) currentAccentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 10.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag(tab.tag)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isSettingsOpen) {
                SettingsScreen(
                    userAccount = userAccount,
                    themeMode = themeMode,
                    glassEffect = glassEffect,
                    accentColorHex = accentColorHex,
                    downloadQuality = downloadQuality,
                    streamingQuality = streamingQuality,
                    downloadOverWifiOnly = downloadOverWifiOnly,
                    offlineModeOnly = offlineModeOnly,
                    storageStats = storageStats,
                    onBack = { isSettingsOpen = false },
                    onThemeModeChange = { musicViewModel.setThemeMode(it) },
                    onGlassEffectChange = { musicViewModel.setGlassEffect(it) },
                    onAccentColorChange = { musicViewModel.setAccentColor(it) },
                    onDownloadQualityChange = { musicViewModel.setDownloadQuality(it) },
                    onStreamingQualityChange = { musicViewModel.setStreamingQuality(it) },
                    onToggleDownloadOverWifi = { musicViewModel.toggleDownloadOverWifiOnly(it) },
                    onToggleOfflineMode = { musicViewModel.toggleOfflineModeOnly(it) },
                    onSignInWithGoogle = { name, email -> musicViewModel.signInWithGoogle(name, email) },
                    onSignOut = { musicViewModel.signOut() },
                    onSyncNow = { musicViewModel.triggerCloudSync() },
                    onClearStorage = { musicViewModel.clearDownloadedStorage() }
                )
            } else if (selectedPlaylistDetail != null) {
                val detail = selectedPlaylistDetail!!
                val playlistTracks = allTracks.filter { track ->
                    if (detail.id == "p_top_hits") true
                    else if (detail.id == "p_synth") track.genre == "Synthwave" || track.genre == "Chiptune"
                    else if (detail.id == "p_lofi") track.genre == "Lo-Fi" || track.genre == "Ambient"
                    else if (detail.id == "p_electronic") track.genre == "Electronic"
                    else if (detail.id == "p_acoustic") track.genre == "Indie"
                    else true
                }
                PlaylistDetailScreen(
                    playlist = detail,
                    tracks = playlistTracks,
                    playbackState = playbackState,
                    onBack = { selectedPlaylistDetail = null },
                    onTrackSelect = { track -> audioPlayerViewModel.playTrack(track, playlistTracks) },
                    onPlayAll = {
                        playlistTracks.firstOrNull()?.let {
                            audioPlayerViewModel.playTrack(it, playlistTracks)
                        }
                    },
                    onShuffleAll = {
                        playlistTracks.shuffled().firstOrNull()?.let {
                            audioPlayerViewModel.playTrack(it, playlistTracks.shuffled())
                        }
                    },
                    onToggleLike = { track -> audioPlayerViewModel.toggleLike(track) }
                )
            } else {
                when (selectedTab) {
                    NavTab.HOME -> {
                        HomeScreen(
                            playlists = playlists,
                            tracks = allTracks,
                            playbackState = playbackState,
                            onTrackSelect = { track -> audioPlayerViewModel.playTrack(track) },
                            onPlaylistSelect = { playlist -> selectedPlaylistDetail = playlist },
                            onToggleLike = { track -> audioPlayerViewModel.toggleLike(track) },
                            onOpenDevices = { showDeviceSheet = true },
                            onOpenSettings = { isSettingsOpen = true }
                        )
                    }
                    NavTab.SEARCH -> {
                        SearchScreen(
                            searchQuery = searchQuery,
                            onQueryChange = { musicViewModel.searchQuery.value = it },
                            selectedGenre = selectedGenre,
                            onGenreSelect = { musicViewModel.setGenreFilter(it) },
                            searchResults = searchResults,
                            playbackState = playbackState,
                            onTrackSelect = { track -> audioPlayerViewModel.playTrack(track) },
                            onToggleLike = { track -> audioPlayerViewModel.toggleLike(track) }
                        )
                    }
                    NavTab.LIBRARY -> {
                        LibraryScreen(
                            playlists = playlists,
                            likedTracks = likedTracks,
                            downloadedTracks = downloadedTracks,
                            playbackState = playbackState,
                            onPlaylistSelect = { playlist -> selectedPlaylistDetail = playlist },
                            onLikedSongsSelect = {
                                selectedPlaylistDetail = Playlist(
                                    id = "liked_songs",
                                    title = "Liked Songs",
                                    description = "All the tracks you love saved in one place.",
                                    coverUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
                                    gradientStartHex = 0xFF450AF5,
                                    gradientEndHex = 0xFF121212
                                )
                            },
                            onTrackSelect = { track -> audioPlayerViewModel.playTrack(track) },
                            onCreatePlaylist = { name, desc -> musicViewModel.createPlaylist(name, desc) },
                            onOpenSettings = { isSettingsOpen = true }
                        )
                    }
                    NavTab.CONNECT -> {
                        DevicesScreen(
                            devices = devices,
                            playbackState = playbackState,
                            onSelectDevice = { device -> audioPlayerViewModel.switchActiveDevice(device) },
                            onVolumeChange = { device, vol -> audioPlayerViewModel.updateDeviceVolume(device, vol) }
                        )
                    }
                }
            }

            // Full-screen Now Playing Overlay
            AnimatedVisibility(
                visible = isNowPlayingExpanded && playbackState.currentTrack != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NowPlayingScreen(
                    playbackState = playbackState,
                    onCollapse = { isNowPlayingExpanded = false },
                    onTogglePlayPause = { audioPlayerViewModel.togglePlayPause() },
                    onSkipNext = { audioPlayerViewModel.skipNext() },
                    onSkipPrevious = { audioPlayerViewModel.skipPrevious() },
                    onSeekTo = { pos -> audioPlayerViewModel.seekTo(pos) },
                    onToggleShuffle = { audioPlayerViewModel.toggleShuffle() },
                    onCycleRepeat = { audioPlayerViewModel.cycleRepeatMode() },
                    onToggleLike = { track -> audioPlayerViewModel.toggleLike(track) },
                    onToggleDownload = { track -> audioPlayerViewModel.toggleDownloaded(track) },
                    onOpenDevices = { showDeviceSheet = true },
                    onSetEqualizerPreset = { preset -> audioPlayerViewModel.setEqualizerPreset(preset) },
                    onSetSleepTimer = { mins -> audioPlayerViewModel.setSleepTimer(mins) },
                    onSelectQueueTrack = { track -> audioPlayerViewModel.playTrack(track) }
                )
            }
        }
    }

    // Modal Device Switcher Bottom Sheet
    if (showDeviceSheet) {
        DeviceConnectSheet(
            devices = devices,
            playbackState = playbackState,
            onSelectDevice = { targetDevice ->
                audioPlayerViewModel.switchActiveDevice(targetDevice)
                showDeviceSheet = false
            },
            onVolumeChange = { dev, vol ->
                audioPlayerViewModel.updateDeviceVolume(dev, vol)
            },
            onDismiss = { showDeviceSheet = false }
        )
    }
}
