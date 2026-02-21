package com.cycling.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlistToRename = MutableStateFlow<com.cycling.domain.model.Playlist?>(null)
    private val _showRenameDialog = MutableStateFlow(false)

    val uiState = combine(
        playlistRepository.getAllPlaylists(),
        _playlistToRename,
        _showRenameDialog
    ) { playlists, playlistToRename, showRenameDialog ->
        PlaylistsUiState(
            playlists = playlists,
            isLoading = false,
            playlistToRename = playlistToRename,
            showRenameDialog = showRenameDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistsUiState()
    )

    private val _uiEffect = kotlinx.coroutines.channels.Channel<PlaylistsEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun handleIntent(intent: PlaylistsIntent) {
        when (intent) {
            is PlaylistsIntent.PlaylistClick -> {
                Timber.d("handleIntent: PlaylistClick playlistId=${intent.playlist.id}")
                viewModelScope.launch {
                    _uiEffect.send(PlaylistsEffect.NavigateToPlaylistDetail(intent.playlist.id))
                }
            }
            is PlaylistsIntent.CreatePlaylist -> {
                Timber.d("handleIntent: CreatePlaylist name=${intent.name}")
                viewModelScope.launch {
                    playlistRepository.createPlaylist(intent.name)
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已创建"))
                }
            }
            is PlaylistsIntent.DeletePlaylist -> {
                Timber.d("handleIntent: DeletePlaylist playlistId=${intent.playlistId}")
                viewModelScope.launch {
                    playlistRepository.deletePlaylist(intent.playlistId)
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已删除"))
                }
            }
            is PlaylistsIntent.ShowRenameDialog -> {
                Timber.d("handleIntent: ShowRenameDialog playlistId=${intent.playlist.id}")
                _playlistToRename.value = intent.playlist
                _showRenameDialog.value = true
            }
            is PlaylistsIntent.RenamePlaylist -> {
                Timber.d("handleIntent: RenamePlaylist playlistId=${intent.playlistId} newName=${intent.newName}")
                viewModelScope.launch {
                    playlistRepository.renamePlaylist(intent.playlistId, intent.newName)
                    _showRenameDialog.value = false
                    _playlistToRename.value = null
                    _uiEffect.send(PlaylistsEffect.ShowToast("播放列表已重命名"))
                }
            }
            is PlaylistsIntent.DismissRenameDialog -> {
                Timber.d("handleIntent: DismissRenameDialog")
                _showRenameDialog.value = false
                _playlistToRename.value = null
            }
        }
    }
}
