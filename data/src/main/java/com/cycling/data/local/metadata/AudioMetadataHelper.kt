package com.cycling.data.local.metadata

import com.mpatric.mp3agic.ID3v2
import com.mpatric.mp3agic.Mp3File
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class AudioMetadataResult(
    val bitrate: Int = 0,
    val sampleRate: Int = 0,
    val channels: Int = 0,
    val encodingType: String = "",
    val year: String? = null,
    val genre: String? = null,
    val composer: String? = null,
    val publisher: String? = null,
    val comment: String? = null,
    val copyright: String? = null
)

@Singleton
class AudioMetadataHelper @Inject constructor() {

    fun extractMetadata(filePath: String): AudioMetadataResult? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null

            val extension = filePath.substringAfterLast('.').lowercase()
            
            when (extension) {
                "mp3" -> extractMp3Metadata(filePath)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractMp3Metadata(filePath: String): AudioMetadataResult {
        val mp3File = Mp3File(filePath)
        
        val id3v2Tag: ID3v2? = if (mp3File.hasId3v2Tag()) {
            mp3File.id3v2Tag
        } else {
            null
        }

        val encodingType = buildString {
            append("MP3")
            if (mp3File.isVbr) {
                append(" (VBR)")
            } else {
                append(" (CBR)")
            }
        }

        return AudioMetadataResult(
            bitrate = mp3File.bitrate,
            sampleRate = mp3File.sampleRate,
            channels = 2,
            encodingType = encodingType,
            year = id3v2Tag?.year,
            genre = id3v2Tag?.genreDescription,
            composer = id3v2Tag?.composer,
            publisher = id3v2Tag?.publisher,
            comment = id3v2Tag?.comment,
            copyright = id3v2Tag?.copyright
        )
    }
}
