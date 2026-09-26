package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MusicCatalog
import com.example.data.local.MusicDatabase
import com.example.data.repository.MusicRepository
import com.example.model.ConnectedDevice
import com.example.model.DeviceType
import com.example.model.EqualizerPreset
import com.example.model.PlaybackState
import com.example.model.RepeatMode
import com.example.model.Track
import com.example.playback.AudioPlaybackManager
import com.example.ui.components.formatTimeMs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AudioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MusicDatabase.getInstance(application)
    private val repository = MusicRepository(database.musicDao(), viewModelScope)
    private val playbackManager = AudioPlaybackManager(application, viewModelScope)

    private val _playbackState = MutableStateFlow(
        PlaybackState(
            currentTrack = MusicCatalog.sampleTracks.firstOrNull(),
            queue = MusicCatalog.sampleTracks,
            currentDevice = MusicCatalog.sampleDevices.firstOrNull { it.isCurrent }
        )
    )
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    val currentTrack: StateFlow<Track?> = _playbackState.map { it.currentTrack }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MusicCatalog.sampleTracks.firstOrNull())

    val isPlaying: StateFlow<Boolean> = _playbackState.map { it.isPlaying }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentPositionMs: StateFlow<Long> = _playbackState.map { it.currentPositionMs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val durationMs: StateFlow<Long> = _playbackState.map { it.durationMs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val progressFraction: StateFlow<Float> = _playbackState.map { state ->
        if (state.durationMs > 0) {
            (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
        } else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val formattedPosition: StateFlow<String> = currentPositionMs.map { formatTimeMs(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0:00")

    val formattedDuration: StateFlow<String> = durationMs.map { formatTimeMs(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0:00")

    private var sleepTimerJob: Job? = null

    init {
        viewModelScope.launch {
            playbackManager.isPlaying.collect { playing ->
                _playbackState.value = _playbackState.value.copy(isPlaying = playing)
            }
        }
        viewModelScope.launch {
            playbackManager.isBuffering.collect { buffering ->
                _playbackState.value = _playbackState.value.copy(isBuffering = buffering)
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
            onTrackCompletion()
        }
    }

    fun playTrack(track: Track, newQueue: List<Track> = emptyList()) {
        val queue = if (newQueue.isNotEmpty()) newQueue else _playbackState.value.queue.ifEmpty { MusicCatalog.sampleTracks }
        val index = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)

        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            queue = queue,
            queueIndex = index,
            durationMs = track.durationMs,
            isPlaying = true
        )
        playbackManager.playTrack(track)
    }

    fun play() {
        if (_playbackState.value.currentTrack == null) {
            val first = _playbackState.value.queue.firstOrNull() ?: MusicCatalog.sampleTracks.first()
            playTrack(first)
        } else {
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            playbackManager.resume()
        }
    }

    fun pause() {
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
        playbackManager.pause()
    }

    fun togglePlayPause() {
        if (_playbackState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun skipNext() {
        val state = _playbackState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val nextIndex = if (state.isShuffle) {
            queue.indices.random()
        } else {
            (state.queueIndex + 1) % queue.size
        }
        val nextTrack = queue[nextIndex]
        _playbackState.value = state.copy(
            currentTrack = nextTrack,
            queueIndex = nextIndex,
            currentPositionMs = 0L,
            durationMs = nextTrack.durationMs,
            isPlaying = true
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
            durationMs = prevTrack.durationMs,
            isPlaying = true
        )
        playbackManager.playTrack(prevTrack)
    }

    fun seekTo(positionMs: Long) {
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
        playbackManager.seekTo(positionMs)
    }

    fun seekToFraction(fraction: Float) {
        val targetMs = (fraction * _playbackState.value.durationMs).toLong()
        seekTo(targetMs)
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

    private fun onTrackCompletion() {
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

    fun switchActiveDevice(targetDevice: ConnectedDevice) {
        viewModelScope.launch {
            repository.setActiveDevice(targetDevice.id)
            val updated = targetDevice.copy(isCurrent = true)
            _playbackState.value = _playbackState.value.copy(currentDevice = updated)

            if (targetDevice.type != DeviceType.SMARTPHONE) {
                playbackManager.setVolume(0.1f)
            } else {
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

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
        sleepTimerJob?.cancel()
    }
}
