package com.cycling.data.repository

import com.cycling.data.local.dao.PlaylistDao
import com.cycling.data.local.entity.PlaylistEntity
import com.cycling.data.mapper.toDomain
import com.cycling.domain.model.Playlist
import com.cycling.domain.model.Song
import com.cycling.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)?.toDomain()
    }

    override fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getSongsInPlaylist(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createPlaylist(name: String): Long {
        val playlist = PlaylistEntity(
            name = name,
            dateAdded = System.currentTimeMillis() / 1000,
            dateModified = System.currentTimeMillis() / 1000
        )
        return playlistDao.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.deletePlaylist(playlist)
        }
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        playlistDao.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    override fun getPlaylistSongCount(playlistId: Long): Flow<Int> {
        return playlistDao.getPlaylistSongCount(playlistId)
    }

    override suspend fun renamePlaylist(playlistId: Long, newName: String) {
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            val updated = playlist.copy(
                name = newName,
                dateModified = System.currentTimeMillis() / 1000
            )
            playlistDao.insertPlaylist(updated)
        }
    }
}
