package com.cycling.presentation.excludefolders

import com.cycling.domain.model.ExcludedFolder
import kotlinx.coroutines.flow.StateFlow

data class ExcludeFoldersUiState(
    val folders: List<ExcludedFolder> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface ExcludeFoldersIntent {
    data object LoadFolders : ExcludeFoldersIntent
    data class AddFolder(val path: String) : ExcludeFoldersIntent
    data class RemoveFolder(val path: String) : ExcludeFoldersIntent
}

sealed interface ExcludeFoldersEffect {
    data class ShowToast(val message: String) : ExcludeFoldersEffect
}
