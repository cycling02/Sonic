package com.cycling.presentation.lyrics

import com.cycling.domain.lyrics.model.SyncedLyrics
import com.cycling.domain.model.Song

data class LyricsUiState(
    val currentSong: Song? = null,
    val lyrics: SyncedLyrics? = null,
    val isLoading: Boolean = false,
    val hasLyrics: Boolean = false,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L,
    val duration: Long = 0L,
    val lastUpdateTime: Long = 0L
)

sealed interface LyricsIntent {
    data class LoadLyrics(val song: Song) : LyricsIntent
    data class SeekTo(val position: Long) : LyricsIntent
    data object NavigateBack : LyricsIntent
}

sealed interface LyricsEffect {
    data object NavigateBack : LyricsEffect
}
