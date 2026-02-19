package com.cycling.domain.repository

import com.cycling.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>

    suspend fun getSongById(id: Long): Song?

    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>

    fun getSongsByArtist(artistId: Long): Flow<List<Song>>

    fun searchSongs(query: String): Flow<List<Song>>

    suspend fun refreshSongs()

    suspend fun getSongCount(): Int
}
