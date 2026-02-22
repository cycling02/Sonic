package com.cycling.presentation.songdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.repository.AudioMetadataRepository
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.repository.SongRepository
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
class SongDetailViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playerRepository: PlayerRepository,
    private val audioMetadataRepository: AudioMetadataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId: Long = savedStateHandle.get<Long>("songId") ?: -1L

    private val _uiState = MutableStateFlow(SongDetailUiState())
    val uiState: StateFlow<SongDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SongDetailEffect>()
    val uiEffect: SharedFlow<SongDetailEffect> = _uiEffect.asSharedFlow()

    init {
        loadSong()
    }

    fun handleIntent(intent: SongDetailIntent) {
        when (intent) {
            is SongDetailIntent.LoadSong -> loadSong()
            is SongDetailIntent.ToggleFavorite -> toggleFavorite()
            is SongDetailIntent.PlaySong -> playSong()
            is SongDetailIntent.AddToPlaylist -> addToPlaylist()
            is SongDetailIntent.CopyPath -> copyPath(intent.path)
            is SongDetailIntent.EditTags -> editTags()
        }
    }

    private fun loadSong() {
        Timber.d("loadSong: starting songId=$songId")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val song = songRepository.getSongById(songId)
            if (song != null) {
                Timber.d("loadSong: loaded song ${song.title}")
                val audioMetadata = audioMetadataRepository.getAudioMetadata(song.path)
                _uiState.update { 
                    it.copy(
                        song = song, 
                        isLoading = false, 
                        isFavorite = song.isFavorite,
                        audioMetadata = audioMetadata
                    ) 
                }
            } else {
                Timber.d("loadSong: song not found")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val currentSong = _uiState.value.song ?: return@launch
            val newFavoriteState = songRepository.toggleFavorite(currentSong.id)
            _uiState.update { it.copy(isFavorite = newFavoriteState) }
            val message = if (newFavoriteState) "已添加到喜欢" else "已从喜欢移除"
            _uiEffect.emit(SongDetailEffect.ShowCopiedMessage(message))
        }
    }

    private fun playSong() {
        viewModelScope.launch {
            val currentSong = _uiState.value.song ?: return@launch
            playerRepository.playSong(currentSong, listOf(currentSong))
            _uiEffect.emit(SongDetailEffect.NavigateToPlayer(currentSong.id))
        }
    }

    private fun addToPlaylist() {
        viewModelScope.launch {
            _uiEffect.emit(SongDetailEffect.ShowAddToPlaylistDialog)
        }
    }

    private fun copyPath(path: String) {
        viewModelScope.launch {
            _uiEffect.emit(SongDetailEffect.ShowCopiedMessage("路径已复制到剪贴板"))
        }
    }

    private fun editTags() {
        viewModelScope.launch {
            val currentSong = _uiState.value.song ?: return@launch
            _uiEffect.emit(SongDetailEffect.NavigateToTagEditor(currentSong.id))
        }
    }
}
