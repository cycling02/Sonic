package com.cycling.domain.repository

import com.cycling.domain.model.AudioMetadata

interface AudioMetadataRepository {
    suspend fun getAudioMetadata(filePath: String): AudioMetadata?
}
