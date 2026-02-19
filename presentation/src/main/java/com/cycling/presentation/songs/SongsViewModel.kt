package com.cycling.presentation.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.Song
import com.cycling.domain.model.SortOrder
import com.cycling.domain.model.ViewMode
import com.cycling.domain.repository.SongRepository
import com.cycling.domain.repository.SongsPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val songsPreferencesRepository: SongsPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongsUiState())
    val uiState: StateFlow<SongsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SongsEffect>()
    val uiEffect: SharedFlow<SongsEffect> = _uiEffect.asSharedFlow()

    init {
        loadPreferences()
        loadSongs()
    }

    fun handleIntent(intent: SongsIntent) {
        when (intent) {
            is SongsIntent.LoadSongs -> loadSongs()
            is SongsIntent.ToggleViewMode -> toggleViewMode()
            is SongsIntent.ChangeSortOrder -> changeSortOrder(intent.sortOrder)
            is SongsIntent.SongClick -> onSongClick(intent.song)
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val viewMode = songsPreferencesRepository.getViewMode()
            val sortOrder = songsPreferencesRepository.getSortOrder()
            val sortAscending = songsPreferencesRepository.getSortAscending()
            _uiState.update { it.copy(viewMode = viewMode, sortOrder = sortOrder, sortAscending = sortAscending) }
        }
    }

    private fun loadSongs() {
        viewModelScope.launch {
            songRepository.getAllSongs().collect { songs ->
                _uiState.update { it.copy(songs = songs, isLoading = false) }
            }
        }
    }

    private fun toggleViewMode() {
        viewModelScope.launch {
            val newViewMode = if (_uiState.value.viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
            songsPreferencesRepository.saveViewMode(newViewMode)
            _uiState.update { it.copy(viewMode = newViewMode) }
        }
    }

    private fun changeSortOrder(newSortOrder: SortOrder) {
        viewModelScope.launch {
            val currentSortOrder = _uiState.value.sortOrder
            val newSortAscending = if (currentSortOrder == newSortOrder) {
                !_uiState.value.sortAscending
            } else {
                true
            }
            songsPreferencesRepository.saveSortOrder(newSortOrder)
            songsPreferencesRepository.saveSortAscending(newSortAscending)
            _uiState.update { it.copy(sortOrder = newSortOrder, sortAscending = newSortAscending) }
        }
    }

    private fun onSongClick(song: Song) {
        viewModelScope.launch {
            _uiEffect.emit(SongsEffect.NavigateToPlayer(song.id))
        }
    }
}
