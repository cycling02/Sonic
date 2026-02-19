package com.cycling.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Songs : Screen
    
    @Serializable
    data object Albums : Screen
    
    @Serializable
    data object Artists : Screen
    
    @Serializable
    data object Playlists : Screen
    
    @Serializable
    data class AlbumDetail(val albumId: Long) : Screen
    
    @Serializable
    data class ArtistDetail(val artistId: Long) : Screen
    
    @Serializable
    data class PlaylistDetail(val playlistId: Long) : Screen
    
    @Serializable
    data object Settings : Screen
    
    @Serializable
    data object ExcludeFolders : Screen
    
    @Serializable
    data object Scan : Screen

    @Serializable
    data object Player : Screen

    @Serializable
    data object ApiKeyConfig : Screen

    @Serializable
    data object Favorites : Screen

    @Serializable
    data object RecentlyPlayed : Screen

    @Serializable
    data object MostPlayed : Screen

    @Serializable
    data object Search : Screen
}
