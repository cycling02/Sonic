package com.cycling.domain.repository

import com.cycling.domain.model.AiInfo
import com.cycling.domain.model.AiInfoType

interface AiRepository {
    suspend fun getApiKey(): String?
    
    suspend fun setApiKey(apiKey: String)
    
    suspend fun hasApiKey(): Boolean
    
    suspend fun getSongInfo(songTitle: String, artist: String): Result<AiInfo>
    
    suspend fun getArtistInfo(artistName: String): Result<AiInfo>
    
    suspend fun getAlbumInfo(albumTitle: String, artist: String): Result<AiInfo>
    
    suspend fun getCachedSongInfo(songTitle: String, artist: String): AiInfo?
    
    suspend fun getCachedArtistInfo(artistName: String): AiInfo?
    
    suspend fun getCachedAlbumInfo(albumTitle: String, artist: String): AiInfo?
}
