package com.cycling.data.repository

import com.cycling.data.local.lyrics.EmbeddedLyricsHelper
import com.cycling.data.local.lyrics.LyricsFileHelper
import com.cycling.domain.lyrics.model.SyncedLyrics
import com.cycling.domain.lyrics.parser.AutoParser
import com.cycling.domain.lyrics.utils.LyricsFormatGuesser
import com.cycling.domain.model.Lyrics
import com.cycling.domain.model.LyricsSource
import com.cycling.domain.repository.LyricsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepositoryImpl @Inject constructor(
    private val embeddedLyricsHelper: EmbeddedLyricsHelper,
    private val lyricsFileHelper: LyricsFileHelper
) : LyricsRepository {

    private val parser = AutoParser(LyricsFormatGuesser())

    override suspend fun getLyrics(songPath: String): Lyrics {
        val fileLyrics = lyricsFileHelper.getLyricsContent(songPath)
        if (!fileLyrics.isNullOrBlank()) {
            val syncedLyrics = parseLyrics(fileLyrics)
            return Lyrics(
                syncedLyrics = syncedLyrics,
                rawContent = fileLyrics,
                source = LyricsSource.FILE
            )
        }

        val embeddedLyrics = embeddedLyricsHelper.extractLyrics(songPath)
        if (!embeddedLyrics.isNullOrBlank()) {
            val syncedLyrics = parseLyrics(embeddedLyrics)
            return Lyrics(
                syncedLyrics = syncedLyrics,
                rawContent = embeddedLyrics,
                source = LyricsSource.EMBEDDED
            )
        }

        return Lyrics(
            syncedLyrics = null,
            rawContent = null,
            source = LyricsSource.NONE
        )
    }

    private fun parseLyrics(content: String): SyncedLyrics? {
        return try {
            parser.parse(content)
        } catch (e: Exception) {
            null
        }
    }
}
