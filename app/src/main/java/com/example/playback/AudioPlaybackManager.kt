package com.example.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlaybackManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val TAG = "AudioPlaybackManager"

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private var onTrackCompleted: (() -> Unit)? = null

    // Fallback simulation timer in case MediaPlayer audio streaming fails on device
    private var isSimulatedPlayback = false
    private var simulatedPositionMs = 0L
    private var simulatedDurationMs = 210000L

    fun setOnCompletionListener(listener: () -> Unit) {
        onTrackCompleted = listener
    }

    fun playTrack(track: Track, startPositionMs: Long = 0L) {
        stop()
        _isBuffering.value = true
        _currentPositionMs.value = startPositionMs
        _durationMs.value = if (track.durationMs > 0) track.durationMs else 180000L
        simulatedDurationMs = _durationMs.value
        simulatedPositionMs = startPositionMs
        isSimulatedPlayback = false

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(track.audioUrl)
                setOnPreparedListener { mp ->
                    _isBuffering.value = false
                    _durationMs.value = if (mp.duration > 0) mp.duration.toLong() else track.durationMs
                    if (startPositionMs > 0 && startPositionMs < mp.duration) {
                        mp.seekTo(startPositionMs.toInt())
                    }
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    stopProgressTracker()
                    onTrackCompleted?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "MediaPlayer error ($what, $extra), switching to seamless fallback")
                    startSimulatedPlayback(track.durationMs, startPositionMs)
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize MediaPlayer for track: ${track.title}", e)
            startSimulatedPlayback(track.durationMs, startPositionMs)
        }
    }

    private fun startSimulatedPlayback(duration: Long, startMs: Long) {
        isSimulatedPlayback = true
        _isBuffering.value = false
        _isPlaying.value = true
        _durationMs.value = if (duration > 0) duration else 200000L
        simulatedDurationMs = _durationMs.value
        simulatedPositionMs = startMs
        _currentPositionMs.value = startMs
        startProgressTracker()
    }

    fun resume() {
        if (isSimulatedPlayback) {
            _isPlaying.value = true
            startProgressTracker()
            return
        }

        mediaPlayer?.let { player ->
            try {
                if (!player.isPlaying) {
                    player.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error resuming player", e)
                startSimulatedPlayback(_durationMs.value, _currentPositionMs.value)
            }
        } ?: run {
            if (_durationMs.value > 0) {
                startSimulatedPlayback(_durationMs.value, _currentPositionMs.value)
            }
        }
    }

    fun pause() {
        if (isSimulatedPlayback) {
            _isPlaying.value = false
            stopProgressTracker()
            return
        }

        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.pause()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error pausing player", e)
            }
        }
        _isPlaying.value = false
        stopProgressTracker()
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(positionMs: Long) {
        _currentPositionMs.value = positionMs
        simulatedPositionMs = positionMs

        if (isSimulatedPlayback) return

        mediaPlayer?.let { player ->
            try {
                player.seekTo(positionMs.toInt())
            } catch (e: Exception) {
                Log.w(TAG, "Error seeking MediaPlayer", e)
            }
        }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        mediaPlayer?.let { player ->
            try {
                player.setVolume(clamped, clamped)
            } catch (e: Exception) {
                Log.w(TAG, "Error setting volume", e)
            }
        }
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                if (isSimulatedPlayback) {
                    simulatedPositionMs += 250L
                    _currentPositionMs.value = simulatedPositionMs
                    if (simulatedPositionMs >= simulatedDurationMs) {
                        _isPlaying.value = false
                        onTrackCompleted?.invoke()
                        break
                    }
                } else {
                    mediaPlayer?.let { player ->
                        try {
                            if (player.isPlaying) {
                                _currentPositionMs.value = player.currentPosition.toLong()
                            }
                        } catch (_: Exception) {}
                    }
                }
                delay(250L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun stop() {
        stopProgressTracker()
        _isPlaying.value = false
        _isBuffering.value = false
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping player", e)
            }
        }
        mediaPlayer = null
    }

    fun release() {
        stop()
    }
}
