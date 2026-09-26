package com.example.data.repository

import com.example.data.MusicCatalog
import com.example.data.local.ConnectedDeviceEntity
import com.example.data.local.MusicDao
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistTrackCrossRef
import com.example.data.local.TrackEntity
import com.example.model.ConnectedDevice
import com.example.model.Playlist
import com.example.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MusicRepository(
    private val musicDao: MusicDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    init {
        scope.launch {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val existing = musicDao.getAllTracks().first()
        if (existing.isEmpty()) {
            val trackEntities = MusicCatalog.sampleTracks.map { TrackEntity.fromTrack(it) }
            musicDao.insertTracks(trackEntities)

            MusicCatalog.samplePlaylists.forEach { playlist ->
                musicDao.insertPlaylist(PlaylistEntity.fromPlaylist(playlist))
            }

            // Associate tracks to playlists
            val refs = mutableListOf<PlaylistTrackCrossRef>()
            MusicCatalog.sampleTracks.forEachIndexed { index, track ->
                refs.add(PlaylistTrackCrossRef(playlistId = "p_top_hits", trackId = track.id, sortOrder = index))
            }
            refs.add(PlaylistTrackCrossRef(playlistId = "p_synth", trackId = "t1", sortOrder = 0))
            refs.add(PlaylistTrackCrossRef(playlistId = "p_synth", trackId = "t6", sortOrder = 1))
            refs.add(PlaylistTrackCrossRef(playlistId = "p_synth", trackId = "t7", sortOrder = 2))

            refs.add(PlaylistTrackCrossRef(playlistId = "p_lofi", trackId = "t2", sortOrder = 0))
            refs.add(PlaylistTrackCrossRef(playlistId = "p_lofi", trackId = "t5", sortOrder = 1))

            refs.add(PlaylistTrackCrossRef(playlistId = "p_electronic", trackId = "t3", sortOrder = 0))
            refs.add(PlaylistTrackCrossRef(playlistId = "p_electronic", trackId = "t7", sortOrder = 1))

            refs.add(PlaylistTrackCrossRef(playlistId = "p_acoustic", trackId = "t4", sortOrder = 0))
            refs.add(PlaylistTrackCrossRef(playlistId = "p_acoustic", trackId = "t8", sortOrder = 1))

            musicDao.insertPlaylistTrackCrossRefs(refs)

            val deviceEntities = MusicCatalog.sampleDevices.map { ConnectedDeviceEntity.fromDevice(it) }
            musicDao.insertDevices(deviceEntities)
        }
    }

    fun getAllTracks(): Flow<List<Track>> {
        return musicDao.getAllTracks().map { entities ->
            entities.map { entity ->
                val sample = MusicCatalog.sampleTracks.find { it.id == entity.id }
                entity.toTrack(sample?.lyrics ?: emptyList())
            }
        }
    }

    fun getLikedTracks(): Flow<List<Track>> {
        return musicDao.getLikedTracks().map { entities ->
            entities.map { entity ->
                val sample = MusicCatalog.sampleTracks.find { it.id == entity.id }
                entity.toTrack(sample?.lyrics ?: emptyList())
            }
        }
    }

    fun getDownloadedTracks(): Flow<List<Track>> {
        return musicDao.getDownloadedTracks().map { entities ->
            entities.map { entity ->
                val sample = MusicCatalog.sampleTracks.find { it.id == entity.id }
                entity.toTrack(sample?.lyrics ?: emptyList())
            }
        }
    }

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return musicDao.getAllPlaylists().map { entities ->
            entities.map { it.toPlaylist() }
        }
    }

    fun getTracksForPlaylist(playlistId: String): Flow<List<Track>> {
        return musicDao.getTracksForPlaylist(playlistId).map { entities ->
            entities.map { entity ->
                val sample = MusicCatalog.sampleTracks.find { it.id == entity.id }
                entity.toTrack(sample?.lyrics ?: emptyList())
            }
        }
    }

    fun getAllDevices(): Flow<List<ConnectedDevice>> {
        return musicDao.getAllDevices().map { entities ->
            entities.map { it.toDevice() }
        }
    }

    suspend fun toggleLike(trackId: String, isLiked: Boolean) {
        musicDao.updateTrackLiked(trackId, !isLiked)
    }

    suspend fun toggleDownloaded(trackId: String, isDownloaded: Boolean) {
        musicDao.updateTrackDownloaded(trackId, !isDownloaded)
    }

    suspend fun setActiveDevice(deviceId: String) {
        musicDao.setActiveDevice(deviceId)
    }

    suspend fun updateDeviceVolume(deviceId: String, volume: Int) {
        musicDao.updateDeviceVolume(deviceId, volume)
    }

    suspend fun createPlaylist(title: String, description: String): String {
        val id = "custom_" + System.currentTimeMillis()
        val playlist = PlaylistEntity(
            id = id,
            title = title,
            description = description,
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            isCustom = true,
            gradientStartHex = 0xFF10B981,
            gradientEndHex = 0xFF064E3B
        )
        musicDao.insertPlaylist(playlist)
        return id
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: String) {
        musicDao.insertPlaylistTrackCrossRefs(
            listOf(PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId, sortOrder = 99))
        )
    }
}
