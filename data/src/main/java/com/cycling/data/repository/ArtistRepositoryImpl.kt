package com.cycling.data.repository

import com.cycling.data.local.mediastore.MediaStoreHelper
import com.cycling.domain.model.Artist
import com.cycling.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepositoryImpl @Inject constructor(
    private val mediaStoreHelper: MediaStoreHelper
) : ArtistRepository {

    private var cachedArtists: List<Artist> = emptyList()

    override fun getAllArtists(): Flow<List<Artist>> = flow {
        if (cachedArtists.isEmpty()) {
            cachedArtists = mediaStoreHelper.queryAllArtists()
        }
        emit(cachedArtists)
    }

    override suspend fun getArtistById(id: Long): Artist? {
        if (cachedArtists.isEmpty()) {
            cachedArtists = mediaStoreHelper.queryAllArtists()
        }
        return cachedArtists.find { it.id == id }
    }

    override suspend fun getArtistCount(): Int {
        if (cachedArtists.isEmpty()) {
            cachedArtists = mediaStoreHelper.queryAllArtists()
        }
        return cachedArtists.size
    }
}
