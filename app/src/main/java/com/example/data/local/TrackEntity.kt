package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.LyricLine
import com.example.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val audioUrl: String,
    val coverUrl: String,
    val genre: String,
    val isLiked: Boolean = false,
    val isOfflineDownloaded: Boolean = false,
    val releaseYear: Int = 2024
) {
    fun toTrack(lyrics: List<LyricLine> = emptyList()): Track {
        return Track(
            id = id,
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            audioUrl = audioUrl,
            coverUrl = coverUrl,
            genre = genre,
            isLiked = isLiked,
            isOfflineDownloaded = isOfflineDownloaded,
            lyrics = lyrics,
            releaseYear = releaseYear
        )
    }

    companion object {
        fun fromTrack(track: Track): TrackEntity {
            return TrackEntity(
                id = track.id,
                title = track.title,
                artist = track.artist,
                album = track.album,
                durationMs = track.durationMs,
                audioUrl = track.audioUrl,
                coverUrl = track.coverUrl,
                genre = track.genre,
                isLiked = track.isLiked,
                isOfflineDownloaded = track.isOfflineDownloaded,
                releaseYear = track.releaseYear
            )
        }
    }
}
