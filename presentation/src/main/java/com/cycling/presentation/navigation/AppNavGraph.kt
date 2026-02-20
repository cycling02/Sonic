package com.cycling.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cycling.presentation.albumdetail.AlbumDetailScreen
import com.cycling.presentation.albums.AlbumsScreen
import com.cycling.presentation.artistdetail.ArtistDetailScreen
import com.cycling.presentation.artists.ArtistsScreen
import com.cycling.presentation.excludefolders.ExcludeFoldersScreen
import com.cycling.presentation.favorites.FavoritesScreen
import com.cycling.presentation.home.HomeScreen
import com.cycling.presentation.librarystats.LibraryStatsScreen
import com.cycling.presentation.lyrics.LyricsScreen
import com.cycling.presentation.mostplayed.MostPlayedScreen
import com.cycling.presentation.player.PlayerScreen
import com.cycling.presentation.player.PlayerViewModel
import com.cycling.presentation.playlistdetail.PlaylistDetailScreen
import com.cycling.presentation.playlists.PlaylistsScreen
import com.cycling.presentation.recentlyplayed.RecentlyPlayedScreen
import com.cycling.presentation.scan.ScanScreen
import com.cycling.presentation.search.SearchScreen
import com.cycling.presentation.settings.ApiKeyConfigScreen
import com.cycling.presentation.settings.SettingsScreen
import com.cycling.presentation.songs.SongsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    playerViewModel: PlayerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToSongs = { navController.navigate(Screen.Songs) },
                onNavigateToAlbums = { navController.navigate(Screen.Albums) },
                onNavigateToArtists = { navController.navigate(Screen.Artists) },
                onNavigateToPlaylists = { navController.navigate(Screen.Playlists) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
                onNavigateToScan = { navController.navigate(Screen.Scan) },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) },
                onNavigateToArtistDetail = { artistId -> navController.navigate(Screen.ArtistDetail(artistId)) },
                onNavigateToPlayer = { songId -> navController.navigate(Screen.Player) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites) },
                onNavigateToRecentlyPlayed = { navController.navigate(Screen.RecentlyPlayed) },
                onNavigateToMostPlayed = { navController.navigate(Screen.MostPlayed) },
                onNavigateToSearch = { navController.navigate(Screen.Search) }
            )
        }

        composable<Screen.Songs> {
            SongsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = {  },
                playerViewModel = playerViewModel
            )
        }

        composable<Screen.Albums> {
            AlbumsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) }
            )
        }

        composable<Screen.Artists> {
            ArtistsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtistDetail = { artistId -> navController.navigate(Screen.ArtistDetail(artistId)) }
            )
        }

        composable<Screen.Playlists> {
            PlaylistsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlaylistDetail = { playlistId -> navController.navigate(Screen.PlaylistDetail(playlistId)) }
            )
        }

        composable<Screen.AlbumDetail> {
            AlbumDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToApiKeyConfig = { navController.navigate(Screen.ApiKeyConfig) }
            )
        }

        composable<Screen.ArtistDetail> {
            ArtistDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToApiKeyConfig = { navController.navigate(Screen.ApiKeyConfig) }
            )
        }

        composable<Screen.PlaylistDetail> {
            PlaylistDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<Screen.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScan = { navController.navigate(Screen.Scan) },
                onNavigateToExcludeFolders = { navController.navigate(Screen.ExcludeFolders) },
                onNavigateToApiKeyConfig = { navController.navigate(Screen.ApiKeyConfig) },
                onNavigateToLibraryStats = { navController.navigate(Screen.LibraryStats) }
            )
        }

        composable<Screen.ExcludeFolders> {
            ExcludeFoldersScreen(
                onNavigateBack = { navController.popBackStack() },
                onShowToast = onShowToast
            )
        }

        composable<Screen.Scan> {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onShowToast = onShowToast
            )
        }

        composable<Screen.Player> {
            val uiState by playerViewModel.uiState.collectAsStateWithLifecycle()
            PlayerScreen(
                uiState = uiState,
                onIntent = playerViewModel::handleIntent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLyrics = { navController.navigate(Screen.Lyrics) }
            )
        }

        composable<Screen.ApiKeyConfig> {
            ApiKeyConfigScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Favorites> {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<Screen.RecentlyPlayed> {
            RecentlyPlayedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<Screen.MostPlayed> {
            MostPlayedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<Screen.Search> {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(Screen.AlbumDetail(albumId)) },
                onNavigateToArtistDetail = { artistId -> navController.navigate(Screen.ArtistDetail(artistId)) },
                playerViewModel = playerViewModel
            )
        }

        composable<Screen.LibraryStats> {
            LibraryStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Lyrics> {
            LyricsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("返回 - $title 页面开发中")
        }
    }
}
