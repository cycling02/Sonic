package com.cycling.data.repository

import com.cycling.data.local.mediastore.MediaStoreHelper
import com.cycling.domain.model.Album
import com.cycling.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val mediaStoreHelper: MediaStoreHelper
) : AlbumRepository {

    private var cachedAlbums: List<Album> = emptyList()

    override fun getAllAlbums(): Flow<List<Album>> = flow {
        if (cachedAlbums.isEmpty()) {
            cachedAlbums = mediaStoreHelper.queryAllAlbums()
        }
        emit(cachedAlbums)
    }

    override suspend fun getAlbumById(id: Long): Album? {
        if (cachedAlbums.isEmpty()) {
            cachedAlbums = mediaStoreHelper.queryAllAlbums()
        }
        return cachedAlbums.find { it.id == id }
    }

    override fun getAlbumsByArtist(artistId: Long): Flow<List<Album>> = flow {
        if (cachedAlbums.isEmpty()) {
            cachedAlbums = mediaStoreHelper.queryAllAlbums()
        }
        val songs = mediaStoreHelper.querySongsByArtist(artistId)
        val albumIds = songs.map { it.albumId }.distinct()
        val albums = cachedAlbums.filter { it.id in albumIds }
        emit(albums)
    }

    override suspend fun getAlbumCount(): Int {
        if (cachedAlbums.isEmpty()) {
            cachedAlbums = mediaStoreHelper.queryAllAlbums()
        }
        return cachedAlbums.size
    }
}
