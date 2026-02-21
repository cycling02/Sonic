package com.cycling.presentation.songdetail

import com.cycling.domain.model.AudioMetadata
import com.cycling.domain.model.Song

data class SongDetailUiState(
    val song: Song? = null,
    val audioMetadata: AudioMetadata? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false
)

sealed interface SongDetailIntent {
    data class LoadSong(val songId: Long) : SongDetailIntent
    data object ToggleFavorite : SongDetailIntent
    data object PlaySong : SongDetailIntent
    data object AddToPlaylist : SongDetailIntent
    data class CopyPath(val path: String) : SongDetailIntent
}

sealed interface SongDetailEffect {
    data class NavigateToPlayer(val songId: Long) : SongDetailEffect
    data object ShowAddToPlaylistDialog : SongDetailEffect
    data class ShowCopiedMessage(val message: String) : SongDetailEffect
    data class ShowError(val message: String) : SongDetailEffect
}
