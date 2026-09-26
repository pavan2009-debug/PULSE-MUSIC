package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM tracks")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isLiked = 1")
    fun getLikedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isOfflineDownloaded = 1")
    fun getDownloadedTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :trackId LIMIT 1")
    suspend fun getTrackById(trackId: String): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("UPDATE tracks SET isLiked = :isLiked WHERE id = :trackId")
    suspend fun updateTrackLiked(trackId: String, isLiked: Boolean)

    @Query("UPDATE tracks SET isOfflineDownloaded = :isDownloaded WHERE id = :trackId")
    suspend fun updateTrackDownloaded(trackId: String, isDownloaded: Boolean)

    // Playlists
    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: String): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRefs(refs: List<PlaylistTrackCrossRef>)

    @Query("""
        SELECT t.* FROM tracks t
        INNER JOIN playlist_tracks pt ON t.id = pt.trackId
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.sortOrder ASC
    """)
    fun getTracksForPlaylist(playlistId: String): Flow<List<TrackEntity>>

    // Devices (Cross-Platform / Spotify Connect)
    @Query("SELECT * FROM devices ORDER BY isCurrent DESC, name ASC")
    fun getAllDevices(): Flow<List<ConnectedDeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<ConnectedDeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: ConnectedDeviceEntity)

    @Transaction
    suspend fun setActiveDevice(deviceId: String) {
        clearActiveDevice()
        markDeviceActive(deviceId)
    }

    @Query("UPDATE devices SET isCurrent = 0")
    suspend fun clearActiveDevice()

    @Query("UPDATE devices SET isCurrent = 1 WHERE id = :deviceId")
    suspend fun markDeviceActive(deviceId: String)

    @Query("UPDATE devices SET volumePercent = :volume WHERE id = :deviceId")
    suspend fun updateDeviceVolume(deviceId: String, volume: Int)
}
