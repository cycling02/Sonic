package com.cycling.presentation.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.lyrics.model.ISyncedLine
import com.cycling.domain.repository.PlayerRepository
import com.cycling.domain.usecase.GetLyricsUseCase
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
class LyricsViewModel @Inject constructor(
    private val getLyricsUseCase: GetLyricsUseCase,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LyricsUiState())
    val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LyricsEffect>()
    val uiEffect: SharedFlow<LyricsEffect> = _uiEffect.asSharedFlow()

    init {
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerRepository.playerState.collect { state ->
                val currentSong = state.currentSong
                _uiState.update { current ->
                    current.copy(
                        currentSong = currentSong,
                        isPlaying = state.isPlaying,
                        playbackPosition = state.playbackPosition,
                        duration = state.duration,
                        lastUpdateTime = System.currentTimeMillis()
                    )
                }

                if (currentSong != null && _uiState.value.lyrics == null) {
                    loadLyrics(currentSong.path)
                }
            }
        }
    }

    fun handleIntent(intent: LyricsIntent) {
        when (intent) {
            is LyricsIntent.LoadLyrics -> loadLyrics(intent.song.path)
            is LyricsIntent.SeekTo -> seekTo(intent.position)
            is LyricsIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadLyrics(songPath: String) {
        Timber.d("loadLyrics: songPath=$songPath")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = getLyricsUseCase(songPath)
            val syncedLyrics = result.syncedLyrics

            Timber.d("loadLyrics: hasLyrics=${syncedLyrics != null && syncedLyrics.lines.isNotEmpty()}, lines=${syncedLyrics?.lines?.size ?: 0}")
            _uiState.update {
                it.copy(
                    lyrics = syncedLyrics,
                    isLoading = false,
                    hasLyrics = syncedLyrics != null && syncedLyrics.lines.isNotEmpty()
                )
            }
        }
    }

    private fun seekTo(position: Long) {
        playerRepository.seekTo(position)
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(LyricsEffect.NavigateBack)
        }
    }

    fun onLineClicked(line: ISyncedLine) {
        seekTo(line.start.toLong())
    }
}
