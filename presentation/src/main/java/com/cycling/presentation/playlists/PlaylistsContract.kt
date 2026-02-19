package com.cycling.presentation.playlists

import com.cycling.domain.model.Playlist

data class PlaylistsUiState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface PlaylistsIntent {
    data class PlaylistClick(val playlist: Playlist) : PlaylistsIntent
    data class CreatePlaylist(val name: String) : PlaylistsIntent
    data class DeletePlaylist(val playlistId: Long) : PlaylistsIntent
    data class RenamePlaylist(val playlistId: Long, val newName: String) : PlaylistsIntent
}

sealed interface PlaylistsEffect {
    data class NavigateToPlaylistDetail(val playlistId: Long) : PlaylistsEffect
    data class ShowToast(val message: String) : PlaylistsEffect
}
