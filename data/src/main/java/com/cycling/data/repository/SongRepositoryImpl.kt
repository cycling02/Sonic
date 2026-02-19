package com.cycling.data.repository

import com.cycling.data.local.dao.SongDao
import com.cycling.data.local.mediastore.MediaStoreHelper
import com.cycling.data.mapper.toDomain
import com.cycling.data.mapper.toEntity
import com.cycling.domain.model.Song
import com.cycling.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val mediaStoreHelper: MediaStoreHelper
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSongById(id: Long): Song? {
        return songDao.getSongById(id)?.toDomain()
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        return songDao.getSongsByAlbum(albumId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        return songDao.getSongsByArtist(artistId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songDao.searchSongs(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshSongs() {
        val songs = mediaStoreHelper.queryAllSongs()
        val entities = songs.map { it.toEntity() }
        songDao.deleteAllSongs()
        songDao.insertSongs(entities)
    }

    override suspend fun getSongCount(): Int {
        return songDao.getSongCount()
    }
}
