package com.cycling.presentation.favorites

import com.cycling.domain.model.Song

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)

sealed interface FavoritesIntent {
    data object LoadData : FavoritesIntent
    data class SongClick(val song: Song) : FavoritesIntent
    data class ToggleFavorite(val song: Song) : FavoritesIntent
    data object PlayAll : FavoritesIntent
    data object ShuffleAll : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class ShowToast(val message: String) : FavoritesEffect
    data class NavigateToPlayer(val songId: Long) : FavoritesEffect
}
