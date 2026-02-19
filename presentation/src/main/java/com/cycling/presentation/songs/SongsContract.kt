package com.cycling.presentation.songs

import com.cycling.domain.model.Song
import com.cycling.domain.model.SortOrder
import com.cycling.domain.model.ViewMode

data class SongsUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val viewMode: ViewMode = ViewMode.LIST,
    val sortOrder: SortOrder = SortOrder.TITLE,
    val sortAscending: Boolean = true
)

sealed interface SongsIntent {
    data object LoadSongs : SongsIntent
    data object ToggleViewMode : SongsIntent
    data class ChangeSortOrder(val sortOrder: SortOrder) : SongsIntent
    data class SongClick(val song: Song) : SongsIntent
}

sealed interface SongsEffect {
    data class NavigateToPlayer(val songId: Long) : SongsEffect
}
