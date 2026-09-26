package com.example.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val tracks: List<Track> = emptyList(),
    val isCustom: Boolean = false,
    val gradientStartHex: Long = 0xFF1DB954,
    val gradientEndHex: Long = 0xFF121212
)
