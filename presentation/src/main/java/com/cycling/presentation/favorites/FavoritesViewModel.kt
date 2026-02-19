package com.cycling.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.Song
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.repository.SongRepository
import com.cycling.domain.usecase.GetFavoriteSongsUseCase
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
class FavoritesViewModel @Inject constructor(
    private val getFavoriteSongsUseCase: GetFavoriteSongsUseCase,
    private val songRepository: SongRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<FavoritesEffect>()
    val uiEffect: SharedFlow<FavoritesEffect> = _uiEffect.asSharedFlow()

    init {
        loadData()
    }

    fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadData -> loadData()
            is FavoritesIntent.SongClick -> handleSongClick(intent.song)
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.song)
            is FavoritesIntent.PlayAll -> playAll()
            is FavoritesIntent.ShuffleAll -> shuffleAll()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getFavoriteSongsUseCase().collectLatest { songs ->
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
            _uiEffect.emit(FavoritesEffect.NavigateToPlayer(song.id))
        }
    }

    private fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            songRepository.toggleFavorite(song.id)
            _uiEffect.emit(FavoritesEffect.ShowToast("已从喜欢移除"))
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
