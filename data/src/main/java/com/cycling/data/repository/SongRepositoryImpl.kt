package com.cycling.data.repository

import com.cycling.data.local.dao.SongDao
import com.cycling.data.local.mediastore.MediaStoreHelper
import com.cycling.data.mapper.toDomain
import com.cycling.data.mapper.toEntity
import com.cycling.domain.model.LibraryStats
import com.cycling.domain.model.Song
import com.cycling.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val mediaStoreHelper: MediaStoreHelper
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        Timber.d("getAllSongs: called")
        return songDao.getAllSongs().map { entities ->
            Timber.d("getAllSongs: found ${entities.size} songs")
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSongById(id: Long): Song? {
        val song = songDao.getSongById(id)?.toDomain()
        Timber.d("getSongById: id=$id, found=${song != null}")
        return song
    }

    override fun getSongsByAlbum(albumId: Long): Flow<List<Song>> {
        Timber.d("getSongsByAlbum: albumId=$albumId")
        return songDao.getSongsByAlbum(albumId).map { entities ->
            Timber.d("getSongsByAlbum: found ${entities.size} songs for albumId=$albumId")
            entities.map { it.toDomain() }
        }
    }

    override fun getSongsByArtist(artistId: Long): Flow<List<Song>> {
        Timber.d("getSongsByArtist: artistId=$artistId")
        return songDao.getSongsByArtist(artistId).map { entities ->
            Timber.d("getSongsByArtist: found ${entities.size} songs for artistId=$artistId")
            entities.map { it.toDomain() }
        }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        Timber.d("searchSongs: query=$query")
        return songDao.searchSongs(query).map { entities ->
            Timber.d("searchSongs: found ${entities.size} results for query=$query")
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshSongs() {
        Timber.d("refreshSongs: starting refresh")
        val songs = mediaStoreHelper.queryAllSongs()
        val entities = songs.map { it.toEntity() }
        Timber.d("refreshSongs: found ${entities.size} songs from MediaStore")
        songDao.deleteAllSongs()
        songDao.insertSongs(entities)
        Timber.d("refreshSongs: completed")
    }

    override suspend fun getSongCount(): Int {
        return songDao.getSongCount()
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao.getFavoriteSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMostPlayedSongs(): Flow<List<Song>> {
        return songDao.getMostPlayedSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentlyPlayedSongs(): Flow<List<Song>> {
        return songDao.getRecentlyPlayedSongs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(songId: Long): Boolean {
        val song = songDao.getSongById(songId)
        if (song == null) {
            Timber.w("toggleFavorite: song not found, songId=$songId")
            return false
        }
        val newFavoriteStatus = !song.isFavorite
        songDao.updateFavorite(songId, newFavoriteStatus)
        Timber.d("toggleFavorite: songId=$songId, newFavoriteStatus=$newFavoriteStatus")
        return newFavoriteStatus
    }

    override suspend fun incrementPlayCount(songId: Long) {
        Timber.d("incrementPlayCount: songId=$songId")
        songDao.incrementPlayCount(songId, System.currentTimeMillis())
    }

    override suspend fun updateLastPlayedAt(songId: Long) {
        songDao.updateLastPlayedAt(songId, System.currentTimeMillis())
    }

    override suspend fun getLibraryStats(): LibraryStats {
        val songsWithoutBitrate = songDao.getSongsWithoutBitrate()
        if (songsWithoutBitrate.isNotEmpty()) {
            songsWithoutBitrate.forEach { song ->
                val bitrate = mediaStoreHelper.extractBitrate(song.path)
                songDao.updateBitrate(song.id, bitrate)
            }
        }
        
        val totalSongs = songDao.getSongCount()
        val hrCount = songDao.getHrCount()
        val sqCount = songDao.getSqCount()
        val hqCount = songDao.getHqCount()
        val othersCount = songDao.getOthersCount()
        
        return LibraryStats(
            totalSongs = totalSongs,
            hrCount = hrCount,
            sqCount = sqCount,
            hqCount = hqCount,
            othersCount = othersCount
        )
    }
}
