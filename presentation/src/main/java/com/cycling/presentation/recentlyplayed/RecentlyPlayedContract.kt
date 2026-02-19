package com.cycling.presentation.recentlyplayed

import com.cycling.domain.model.Song

data class RecentlyPlayedUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)

sealed interface RecentlyPlayedIntent {
    data object LoadData : RecentlyPlayedIntent
    data class SongClick(val song: Song) : RecentlyPlayedIntent
    data object PlayAll : RecentlyPlayedIntent
    data object ShuffleAll : RecentlyPlayedIntent
}

sealed interface RecentlyPlayedEffect {
    data class ShowToast(val message: String) : RecentlyPlayedEffect
    data class NavigateToPlayer(val songId: Long) : RecentlyPlayedEffect
}
