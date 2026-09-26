package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Playlist
import com.example.model.Track

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val isCustom: Boolean = false,
    val gradientStartHex: Long = 0xFF1DB954,
    val gradientEndHex: Long = 0xFF121212
) {
    fun toPlaylist(tracks: List<Track> = emptyList()): Playlist {
        return Playlist(
            id = id,
            title = title,
            description = description,
            coverUrl = coverUrl,
            tracks = tracks,
            isCustom = isCustom,
            gradientStartHex = gradientStartHex,
            gradientEndHex = gradientEndHex
        )
    }

    companion object {
        fun fromPlaylist(playlist: Playlist): PlaylistEntity {
            return PlaylistEntity(
                id = playlist.id,
                title = playlist.title,
                description = playlist.description,
                coverUrl = playlist.coverUrl,
                isCustom = playlist.isCustom,
                gradientStartHex = playlist.gradientStartHex,
                gradientEndHex = playlist.gradientEndHex
            )
        }
    }
}

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: String,
    val trackId: String,
    val sortOrder: Int = 0
)
