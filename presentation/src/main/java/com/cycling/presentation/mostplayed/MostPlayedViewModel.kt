package com.cycling.presentation.mostplayed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.Song
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.usecase.GetMostPlayedSongsUseCase
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
class MostPlayedViewModel @Inject constructor(
    private val getMostPlayedSongsUseCase: GetMostPlayedSongsUseCase,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MostPlayedUiState())
    val uiState: StateFlow<MostPlayedUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MostPlayedEffect>()
    val uiEffect: SharedFlow<MostPlayedEffect> = _uiEffect.asSharedFlow()

    init {
        loadData()
    }

    fun handleIntent(intent: MostPlayedIntent) {
        when (intent) {
            is MostPlayedIntent.LoadData -> loadData()
            is MostPlayedIntent.SongClick -> handleSongClick(intent.song)
            is MostPlayedIntent.PlayAll -> playAll()
            is MostPlayedIntent.ShuffleAll -> shuffleAll()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getMostPlayedSongsUseCase().collectLatest { songs ->
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
            _uiEffect.emit(MostPlayedEffect.NavigateToPlayer(song.id))
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
