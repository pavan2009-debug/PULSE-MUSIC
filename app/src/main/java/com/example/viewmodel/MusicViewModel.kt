package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.MusicCatalog
import com.example.data.local.MusicDatabase
import com.example.data.repository.MusicRepository
import com.example.model.AudioQuality
import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.EqualizerPreset
import com.example.model.GlassEffect
import com.example.model.PlaybackState
import com.example.model.Playlist
import com.example.model.RepeatMode
import com.example.model.StorageStats
import com.example.model.ThemeMode
import com.example.model.Track
import com.example.model.UserAccount
import com.example.playback.AudioPlaybackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MusicDatabase.getInstance(application)
    private val repository = MusicRepository(database.musicDao(), viewModelScope)
    private val playbackManager = AudioPlaybackManager(application, viewModelScope)
    private val authManager = AuthManager(application)

    val currentUser: StateFlow<UserAccount> = authManager.currentUser

    // Settings & Appearance
    val themeMode = MutableStateFlow(ThemeMode.DARK)
    val glassEffect = MutableStateFlow(GlassEffect.FROSTED_GLASS)
    val accentColorHex = MutableStateFlow(0xFF1DB954)

    // Audio & Storage Settings
    val downloadQuality = MutableStateFlow(AudioQuality.VERY_HIGH)
    val streamingQuality = MutableStateFlow(AudioQuality.LOSSLESS)
    val downloadOverWifiOnly = MutableStateFlow(true)
    val offlineModeOnly = MutableStateFlow(false)
    val storageStats = MutableStateFlow(StorageStats())

    val allTracks: StateFlow<List<Track>> = repository.getAllTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MusicCatalog.sampleTracks)

    val likedTracks: StateFlow<List<Track>> = repository.getLikedTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedTracks: StateFlow<List<Track>> = repository.getDownloadedTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<Playlist>> = repository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MusicCatalog.samplePlaylists)

    val devices: StateFlow<List<ConnectedDevice>> = repository.getAllDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MusicCatalog.sampleDevices)

    private val _playbackState = MutableStateFlow(
        PlaybackState(
            currentTrack = MusicCatalog.sampleTracks.firstOrNull(),
            queue = MusicCatalog.sampleTracks,
            currentDevice = MusicCatalog.sampleDevices.firstOrNull { it.isCurrent }
        )
    )
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Navigation and search states
    val searchQuery = MutableStateFlow("")
    val selectedGenre = MutableStateFlow<String?>(null)
    val selectedPlaylist = MutableStateFlow<Playlist?>(null)

    // Sleep timer job
    private var sleepTimerJob: Job? = null

    init {
        // Observe playback manager
        viewModelScope.launch {
            playbackManager.isPlaying.collect { isPlaying ->
                _playbackState.value = _playbackState.value.copy(isPlaying = isPlaying)
            }
        }
        viewModelScope.launch {
            playbackManager.isBuffering.collect { isBuffering ->
                _playbackState.value = _playbackState.value.copy(isBuffering = isBuffering)
            }
        }
        viewModelScope.launch {
            playbackManager.currentPositionMs.collect { position ->
                _playbackState.value = _playbackState.value.copy(currentPositionMs = position)
            }
        }
        viewModelScope.launch {
            playbackManager.durationMs.collect { duration ->
                if (duration > 0) {
                    _playbackState.value = _playbackState.value.copy(durationMs = duration)
                }
            }
        }
        playbackManager.setOnCompletionListener {
            onTrackFinished()
        }

        // Keep current device synced with database
        viewModelScope.launch {
            devices.collect { deviceList ->
                val active = deviceList.firstOrNull { it.isCurrent }
                if (active != null) {
                    _playbackState.value = _playbackState.value.copy(currentDevice = active)
                }
            }
        }
    }

    val searchResults: StateFlow<List<Track>> = combine(
        allTracks,
        searchQuery,
        selectedGenre
    ) { tracks, query, genre ->
        tracks.filter { track ->
            val matchesQuery = query.isBlank() ||
                    track.title.contains(query, ignoreCase = true) ||
                    track.artist.contains(query, ignoreCase = true) ||
                    track.album.contains(query, ignoreCase = true) ||
                    track.genre.contains(query, ignoreCase = true)
            val matchesGenre = genre == null || track.genre.equals(genre, ignoreCase = true)
            matchesQuery && matchesGenre
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun playTrack(track: Track, newQueue: List<Track> = emptyList()) {
        val queue = if (newQueue.isNotEmpty()) newQueue else allTracks.value.ifEmpty { MusicCatalog.sampleTracks }
        val index = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            queue = queue,
            queueIndex = index,
            durationMs = track.durationMs
        )
        playbackManager.playTrack(track)
    }

    fun togglePlayPause() {
        if (_playbackState.value.currentTrack == null) {
            val first = allTracks.value.firstOrNull() ?: MusicCatalog.sampleTracks.first()
            playTrack(first)
            return
        }
        playbackManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun skipNext() {
        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val nextIndex = if (state.isShuffle) {
            (queue.indices).random()
        } else {
            (state.queueIndex + 1) % queue.size
        }
        val nextTrack = queue[nextIndex]
        _playbackState.value = state.copy(
            currentTrack = nextTrack,
            queueIndex = nextIndex,
            currentPositionMs = 0L,
            durationMs = nextTrack.durationMs
        )
        playbackManager.playTrack(nextTrack)
    }

    fun skipPrevious() {
        val state = _playbackState.value
        if (state.currentPositionMs > 3000L) {
            seekTo(0L)
            return
        }
        val queue = state.queue
        if (queue.isEmpty()) return

        val prevIndex = if (state.queueIndex - 1 < 0) queue.size - 1 else state.queueIndex - 1
        val prevTrack = queue[prevIndex]
        _playbackState.value = state.copy(
            currentTrack = prevTrack,
            queueIndex = prevIndex,
            currentPositionMs = 0L,
            durationMs = prevTrack.durationMs
        )
        playbackManager.playTrack(prevTrack)
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(isShuffle = !_playbackState.value.isShuffle)
    }

    fun cycleRepeatMode() {
        val next = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = next)
    }

    private fun onTrackFinished() {
        when (_playbackState.value.repeatMode) {
            RepeatMode.ONE -> {
                _playbackState.value.currentTrack?.let {
                    playbackManager.playTrack(it, 0L)
                }
            }
            RepeatMode.ALL -> skipNext()
            RepeatMode.OFF -> {
                if (_playbackState.value.queueIndex < _playbackState.value.queue.size - 1) {
                    skipNext()
                } else {
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0L)
                }
            }
        }
    }

    fun toggleLike(track: Track) {
        viewModelScope.launch {
            repository.toggleLike(track.id, track.isLiked)
            if (_playbackState.value.currentTrack?.id == track.id) {
                _playbackState.value = _playbackState.value.copy(
                    currentTrack = track.copy(isLiked = !track.isLiked)
                )
            }
        }
    }

    fun toggleDownloaded(track: Track) {
        viewModelScope.launch {
            repository.toggleDownloaded(track.id, track.isOfflineDownloaded)
            if (_playbackState.value.currentTrack?.id == track.id) {
                _playbackState.value = _playbackState.value.copy(
                    currentTrack = track.copy(isOfflineDownloaded = !track.isOfflineDownloaded)
                )
            }
        }
    }

    // Spotify Connect / Cross-Device Switcher
    fun switchActiveDevice(targetDevice: ConnectedDevice) {
        viewModelScope.launch {
            repository.setActiveDevice(targetDevice.id)
            val updated = targetDevice.copy(isCurrent = true)
            _playbackState.value = _playbackState.value.copy(currentDevice = updated)

            // When switching to a remote device, current playback seamlessly handovers
            val currentMs = _playbackState.value.currentPositionMs
            if (targetDevice.type != DeviceType.SMARTPHONE) {
                // Remote playback active: phone acts as rich remote controller
                playbackManager.setVolume(0.1f) // lower local speaker to simulate remote
            } else {
                // Switched back to this phone: resume full volume local playback
                playbackManager.setVolume(_playbackState.value.volume)
            }
        }
    }

    fun updateDeviceVolume(device: ConnectedDevice, volumePercent: Int) {
        viewModelScope.launch {
            repository.updateDeviceVolume(device.id, volumePercent)
            val volumeNormalized = volumePercent / 100f
            _playbackState.value = _playbackState.value.copy(volume = volumeNormalized)
            playbackManager.setVolume(volumeNormalized)
        }
    }

    fun setEqualizerPreset(preset: EqualizerPreset) {
        _playbackState.value = _playbackState.value.copy(equalizerPreset = preset)
    }

    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        _playbackState.value = _playbackState.value.copy(sleepTimerMinutes = minutes)
        if (minutes > 0) {
            sleepTimerJob = viewModelScope.launch {
                var remaining = minutes
                while (remaining > 0) {
                    delay(60_000L)
                    remaining -= 1
                    _playbackState.value = _playbackState.value.copy(sleepTimerMinutes = remaining)
                }
                playbackManager.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false, sleepTimerMinutes = 0)
            }
        }
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
        }
    }

    fun selectPlaylist(playlist: Playlist?) {
        selectedPlaylist.value = playlist
    }

    fun setGenreFilter(genre: String?) {
        selectedGenre.value = if (selectedGenre.value == genre) null else genre
    }

    fun setThemeMode(mode: ThemeMode) {
        themeMode.value = mode
    }

    fun setGlassEffect(effect: GlassEffect) {
        glassEffect.value = effect
    }

    fun setAccentColor(hex: Long) {
        accentColorHex.value = hex
    }

    fun setDownloadQuality(quality: AudioQuality) {
        downloadQuality.value = quality
    }

    fun setStreamingQuality(quality: AudioQuality) {
        streamingQuality.value = quality
    }

    fun toggleDownloadOverWifiOnly(enabled: Boolean) {
        downloadOverWifiOnly.value = enabled
    }

    fun toggleOfflineModeOnly(enabled: Boolean) {
        offlineModeOnly.value = enabled
    }

    fun signInWithGoogle(displayName: String = "Alex Morgan", email: String = "alex.morgan@pulse.music") {
        authManager.signInWithGoogle(displayName, email)
    }

    fun signOut() {
        authManager.signOut()
    }

    fun triggerCloudSync(): String {
        return authManager.triggerCloudSync()
    }

    fun clearDownloadedStorage() {
        storageStats.value = storageStats.value.copy(downloadedBytes = 0L)
        viewModelScope.launch {
            allTracks.value.forEach { track ->
                if (track.isOfflineDownloaded) {
                    repository.toggleDownloaded(track.id, true)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
        sleepTimerJob?.cancel()
    }
}
