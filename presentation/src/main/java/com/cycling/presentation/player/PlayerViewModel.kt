package com.cycling.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.RepeatMode
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
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PlayerEffect>()
    val uiEffect: SharedFlow<PlayerEffect> = _uiEffect.asSharedFlow()

    init {
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerRepository.playerState.collect { state ->
                _uiState.update { 
                    state.toUiState().copy(
                        isFavorite = state.currentSong?.isFavorite ?: false
                    )
                }
            }
        }
    }

    fun handleIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.PlaySong -> playSong(intent.song, intent.queue)
            is PlayerIntent.PlayFromQueue -> playFromQueue(intent.index)
            is PlayerIntent.PlayPause -> playPause()
            is PlayerIntent.SeekTo -> seekTo(intent.position)
            is PlayerIntent.SkipToNext -> skipToNext()
            is PlayerIntent.SkipToPrevious -> skipToPrevious()
            is PlayerIntent.ToggleRepeatMode -> toggleRepeatMode()
            is PlayerIntent.ToggleShuffleMode -> toggleShuffleMode()
            is PlayerIntent.AddToQueue -> addToQueue(intent.song)
            is PlayerIntent.RemoveFromQueue -> removeFromQueue(intent.index)
            is PlayerIntent.ClearQueue -> clearQueue()
            is PlayerIntent.ToggleQueue -> toggleQueue()
            is PlayerIntent.UpdateProgress -> { }
            is PlayerIntent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun playSong(song: com.cycling.domain.model.Song, queue: List<com.cycling.domain.model.Song>) {
        playerRepository.playSong(song, queue)
    }

    private fun playFromQueue(index: Int) {
        val queue = _uiState.value.playbackQueue
        if (index in queue.indices) {
            playerRepository.playSong(queue[index], queue)
        }
    }

    private fun playPause() {
        playerRepository.playPause()
    }

    private fun seekTo(position: Long) {
        playerRepository.seekTo(position)
    }

    private fun skipToNext() {
        playerRepository.skipToNext()
    }

    private fun skipToPrevious() {
        playerRepository.skipToPrevious()
    }

    private fun toggleRepeatMode() {
        val currentMode = _uiState.value.repeatMode
        val newMode = when (currentMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        playerRepository.setRepeatMode(newMode)
    }

    private fun toggleShuffleMode() {
        val newShuffleMode = !_uiState.value.shuffleMode
        playerRepository.setShuffleMode(newShuffleMode)
    }

    private fun addToQueue(song: com.cycling.domain.model.Song) {
        playerRepository.addToQueue(song)
        viewModelScope.launch {
            _uiEffect.emit(PlayerEffect.ShowToast("已添加到播放队列"))
        }
    }

    private fun removeFromQueue(index: Int) {
        playerRepository.removeFromQueue(index)
    }

    private fun clearQueue() {
        playerRepository.clearQueue()
    }

    private fun toggleQueue() {
        _uiState.update { it.copy(showQueue = !it.showQueue) }
    }

    private fun toggleFavorite() {
        val currentSong = _uiState.value.currentSong ?: return
        viewModelScope.launch {
            val newFavoriteStatus = songRepository.toggleFavorite(currentSong.id)
            _uiState.update { it.copy(isFavorite = newFavoriteStatus) }
            val message = if (newFavoriteStatus) "已添加到喜欢" else "已从喜欢移除"
            _uiEffect.emit(PlayerEffect.ShowToast(message))
        }
    }
}
