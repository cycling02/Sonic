package com.cycling.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    val uiState = playlistRepository.getAllPlaylists()
        .map { playlists ->
            PlaylistsUiState(
                playlists = playlists,
                isLoading = false
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlaylistsUiState()
        )

    private val _uiEffect = kotlinx.coroutines.channels.Channel<PlaylistsEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun handleIntent(intent: PlaylistsIntent) {
        when (intent) {
            is PlaylistsIntent.PlaylistClick -> {
                viewModelScope.launch {
                    _uiEffect.send(PlaylistsEffect.NavigateToPlaylistDetail(intent.playlist.id))
                }
            }
            is PlaylistsIntent.CreatePlaylist -> {
                viewModelScope.launch {
                    playlistRepository.createPlaylist(intent.name)
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已创建"))
                }
            }
            is PlaylistsIntent.DeletePlaylist -> {
                viewModelScope.launch {
                    playlistRepository.deletePlaylist(intent.playlistId)
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已删除"))
                }
            }
            is PlaylistsIntent.RenamePlaylist -> {
                viewModelScope.launch {
                    playlistRepository.renamePlaylist(intent.playlistId, intent.newName)
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已重命名"))
                }
            }
        }
    }
}
