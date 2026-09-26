package com.example.model

data class LyricLine(
    val timeMs: Long,
    val text: String
)

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val audioUrl: String,
    val coverUrl: String,
    val genre: String,
    val isLiked: Boolean = false,
    val isOfflineDownloaded: Boolean = false,
    val lyrics: List<LyricLine> = emptyList(),
    val releaseYear: Int = 2024
)
