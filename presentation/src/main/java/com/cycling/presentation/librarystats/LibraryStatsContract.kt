package com.cycling.presentation.librarystats

import com.cycling.domain.model.LibraryStats

data class LibraryStatsUiState(
    val isLoading: Boolean = false,
    val stats: LibraryStats? = null,
    val error: String? = null
)

sealed interface LibraryStatsIntent {
    data object LoadData : LibraryStatsIntent
}

sealed interface LibraryStatsEffect {
    data class ShowToast(val message: String) : LibraryStatsEffect
}
