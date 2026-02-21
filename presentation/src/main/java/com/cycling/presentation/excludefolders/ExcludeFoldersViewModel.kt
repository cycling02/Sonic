package com.cycling.presentation.excludefolders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.ExcludedFolder
import com.cycling.domain.repository.ExcludedFolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExcludeFoldersViewModel @Inject constructor(
    private val excludedFolderRepository: ExcludedFolderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExcludeFoldersUiState())
    val uiState: StateFlow<ExcludeFoldersUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ExcludeFoldersEffect>()
    val uiEffect: SharedFlow<ExcludeFoldersEffect> = _uiEffect.asSharedFlow()

    init {
        loadFolders()
    }

    fun handleIntent(intent: ExcludeFoldersIntent) {
        Timber.d("handleIntent: $intent")
        when (intent) {
            is ExcludeFoldersIntent.LoadFolders -> loadFolders()
            is ExcludeFoldersIntent.AddFolder -> addFolder(intent.path)
            is ExcludeFoldersIntent.RemoveFolder -> removeFolder(intent.path)
        }
    }

    private fun loadFolders() {
        Timber.d("loadFolders: starting")
        viewModelScope.launch {
            excludedFolderRepository.excludedFolders.collect { folders ->
                Timber.d("loadFolders: loaded ${folders.size} folders")
                _uiState.update { it.copy(folders = folders, isLoading = false) }
            }
        }
    }

    private fun addFolder(path: String) {
        viewModelScope.launch {
            val exists = _uiState.value.folders.any { it.path == path }
            if (exists) {
                _uiEffect.emit(ExcludeFoldersEffect.ShowToast("该文件夹已在排除列表中"))
                return@launch
            }
            excludedFolderRepository.addExcludedFolder(ExcludedFolder(path = path))
            _uiEffect.emit(ExcludeFoldersEffect.ShowToast("已添加到排除列表"))
        }
    }

    private fun removeFolder(path: String) {
        viewModelScope.launch {
            excludedFolderRepository.removeExcludedFolder(path)
            _uiEffect.emit(ExcludeFoldersEffect.ShowToast("已从排除列表移除"))
        }
    }
}
