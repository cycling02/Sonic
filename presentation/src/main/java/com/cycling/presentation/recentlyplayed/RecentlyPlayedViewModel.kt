package com.cycling.presentation.recentlyplayed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.Song
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.usecase.GetRecentlyPlayedSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    private val getRecentlyPlayedSongsUseCase: GetRecentlyPlayedSongsUseCase,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentlyPlayedUiState())
    val uiState: StateFlow<RecentlyPlayedUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<RecentlyPlayedEffect>()
    val uiEffect: SharedFlow<RecentlyPlayedEffect> = _uiEffect.asSharedFlow()

    init {
        loadData()
    }

    fun handleIntent(intent: RecentlyPlayedIntent) {
        when (intent) {
            is RecentlyPlayedIntent.LoadData -> loadData()
            is RecentlyPlayedIntent.SongClick -> handleSongClick(intent.song)
            is RecentlyPlayedIntent.PlayAll -> playAll()
            is RecentlyPlayedIntent.ShuffleAll -> shuffleAll()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getRecentlyPlayedSongsUseCase().collectLatest { songs ->
                _uiState.update { it.copy(
                    songs = songs,
                    isLoading = false
                )}
            }
        }
    }

    private fun handleSongClick(song: Song) {
        viewModelScope.launch {
            playerRepository.playSong(song, _uiState.value.songs)
            _uiEffect.emit(RecentlyPlayedEffect.NavigateToPlayer(song.id))
        }
    }

    private fun playAll() {
        val songs = _uiState.value.songs
        if (songs.isNotEmpty()) {
            viewModelScope.launch {
                playerRepository.playSong(songs.first(), songs)
            }
        }
    }

    private fun shuffleAll() {
        val songs = _uiState.value.songs.shuffled()
        if (songs.isNotEmpty()) {
            viewModelScope.launch {
                playerRepository.playSong(songs.first(), songs)
            }
        }
    }
}
