package com.example.model

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

enum class EqualizerPreset(val title: String, val bass: Float, val mid: Float, val treble: Float) {
    BALANCED("Balanced", 0f, 0f, 0f),
    BASS_BOOST("Bass Boost", 6f, 1f, -2f),
    VOCAL("Vocal Clarity", -2f, 5f, 2f),
    ELECTRONIC("Electronic", 5f, 2f, 4f),
    ROCK("Rock & Metal", 4f, 1f, 5f),
    ACOUSTIC("Acoustic / Warm", 2f, 3f, 1f)
}

data class PlaybackState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = 0,
    val currentDevice: ConnectedDevice? = null,
    val equalizerPreset: EqualizerPreset = EqualizerPreset.BALANCED,
    val sleepTimerMinutes: Int = 0,
    val volume: Float = 0.85f
)
