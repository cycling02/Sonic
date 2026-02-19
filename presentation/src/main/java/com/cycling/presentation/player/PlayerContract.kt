package com.cycling.presentation.player

import com.cycling.domain.model.PlayerState
import com.cycling.domain.model.RepeatMode
import com.cycling.domain.model.Song

data class PlayerUiState(
    val currentSong: Song? = null,
    val playbackPosition: Long = 0,
    val duration: Long = 0,
    val isPlaying: Boolean = false,
    val playbackQueue: List<Song> = emptyList(),
    val queueIndex: Int = -1,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffleMode: Boolean = false,
    val showQueue: Boolean = false
)

sealed interface PlayerIntent {
    data class PlaySong(val song: Song, val queue: List<Song> = emptyList()) : PlayerIntent
    data object PlayPause : PlayerIntent
    data class SeekTo(val position: Long) : PlayerIntent
    data object SkipToNext : PlayerIntent
    data object SkipToPrevious : PlayerIntent
    data object ToggleRepeatMode : PlayerIntent
    data object ToggleShuffleMode : PlayerIntent
    data class AddToQueue(val song: Song) : PlayerIntent
    data class RemoveFromQueue(val index: Int) : PlayerIntent
    data object ClearQueue : PlayerIntent
    data object ToggleQueue : PlayerIntent
    data object UpdateProgress : PlayerIntent
}

sealed interface PlayerEffect {
    data class ShowToast(val message: String) : PlayerEffect
}

fun PlayerState.toUiState(): PlayerUiState {
    return PlayerUiState(
        currentSong = currentSong,
        playbackPosition = playbackPosition,
        duration = duration,
        isPlaying = isPlaying,
        playbackQueue = playbackQueue,
        queueIndex = queueIndex,
        repeatMode = repeatMode,
        shuffleMode = shuffleMode
    )
}
