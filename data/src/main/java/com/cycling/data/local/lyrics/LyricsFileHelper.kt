package com.cycling.data.local.lyrics

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsFileHelper @Inject constructor() {

    private val supportedExtensions = listOf("lrc", "ttml", "xml")

    fun findLyricsFile(audioFilePath: String): File? {
        val audioFile = File(audioFilePath)
        if (!audioFile.exists()) return null

        val parentDir = audioFile.parentFile ?: return null
        val fileNameWithoutExt = audioFile.nameWithoutExtension

        for (ext in supportedExtensions) {
            val lyricsFile = File(parentDir, "$fileNameWithoutExt.$ext")
            if (lyricsFile.exists()) {
                return lyricsFile
            }
        }

        return null
    }

    fun readLyricsContent(lyricsFile: File): String? {
        return try {
            if (!lyricsFile.exists()) return null
            lyricsFile.readText()
        } catch (e: Exception) {
            null
        }
    }

    fun getLyricsContent(audioFilePath: String): String? {
        val lyricsFile = findLyricsFile(audioFilePath) ?: return null
        return readLyricsContent(lyricsFile)
    }
}
