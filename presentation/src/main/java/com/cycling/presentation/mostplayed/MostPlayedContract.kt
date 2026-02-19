package com.cycling.presentation.mostplayed

import com.cycling.domain.model.Song

data class MostPlayedUiState(
    val isLoading: Boolean = false,
    val songs: List<Song> = emptyList(),
    val error: String? = null
)

sealed interface MostPlayedIntent {
    data object LoadData : MostPlayedIntent
    data class SongClick(val song: Song) : MostPlayedIntent
    data object PlayAll : MostPlayedIntent
    data object ShuffleAll : MostPlayedIntent
}

sealed interface MostPlayedEffect {
    data class ShowToast(val message: String) : MostPlayedEffect
    data class NavigateToPlayer(val songId: Long) : MostPlayedEffect
}
