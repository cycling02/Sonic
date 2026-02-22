package com.cycling.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.cycling.presentation.ai.AiInfoScreen
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
import com.cycling.presentation.songdetail.SongDetailScreen
import com.cycling.presentation.songs.SongsScreen
import com.cycling.presentation.tageditor.TagEditorScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onShowToast: (String) -> Unit,
    playerViewModel: PlayerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        enterTransition = { IOSNavAnimations.iosPushEnter() },
        exitTransition = { IOSNavAnimations.iosPushExit() },
        popEnterTransition = { IOSNavAnimations.iosPopEnter() },
        popExitTransition = { IOSNavAnimations.iosPopExit() }
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onNavigateToSongs = { navController.navigate(LibraryDestination.Songs) },
                onNavigateToAlbums = { navController.navigate(LibraryDestination.Albums) },
                onNavigateToArtists = { navController.navigate(LibraryDestination.Artists) },
                onNavigateToPlaylists = { navController.navigate(LibraryDestination.Playlists) },
                onNavigateToSettings = { navController.navigate(SettingsDestination.Main) },
                onNavigateToScan = { navController.navigate(SettingsDestination.Scan) },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(LibraryDestination.AlbumDetail(albumId)) },
                onNavigateToArtistDetail = { artistId -> navController.navigate(LibraryDestination.ArtistDetail(artistId)) },
                onNavigateToPlayer = { songId -> navController.navigate(Screen.Player) },
                onNavigateToFavorites = { navController.navigate(LibraryDestination.Favorites) },
                onNavigateToRecentlyPlayed = { navController.navigate(LibraryDestination.RecentlyPlayed) },
                onNavigateToMostPlayed = { navController.navigate(LibraryDestination.MostPlayed) },
                onNavigateToSearch = { navController.navigate(Screen.Search) }
            )
        }

        composable<Screen.Search>(
            enterTransition = { IOSNavAnimations.iosModalEnter() },
            exitTransition = { IOSNavAnimations.iosModalExit() },
            popEnterTransition = { IOSNavAnimations.iosFadeEnter() },
            popExitTransition = { IOSNavAnimations.iosModalExit() }
        ) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(LibraryDestination.AlbumDetail(albumId)) },
                onNavigateToArtistDetail = { artistId -> navController.navigate(LibraryDestination.ArtistDetail(artistId)) },
                playerViewModel = playerViewModel
            )
        }

        composable<Screen.Player>(
            enterTransition = { IOSNavAnimations.iosSheetEnter() },
            exitTransition = { IOSNavAnimations.iosSheetExit() },
            popEnterTransition = { IOSNavAnimations.iosFadeEnter() },
            popExitTransition = { IOSNavAnimations.iosSheetExit() }
        ) {
            val uiState by playerViewModel.uiState.collectAsStateWithLifecycle()
            PlayerScreen(
                uiState = uiState,
                onIntent = playerViewModel::handleIntent,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLyrics = { navController.navigate(Screen.Lyrics) }
            )
        }

        composable<Screen.Lyrics> {
            LyricsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.SongDetail> {
            SongDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { navController.navigate(Screen.Player) },
                onNavigateToTagEditor = { songId -> navController.navigate(Screen.TagEditor(songId)) }
            )
        }

        composable<Screen.TagEditor> {
            TagEditorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.AiInfo> {
            val route = it.toRoute<Screen.AiInfo>()
            AiInfoScreen(
                type = route.type,
                name = route.name,
                artist = route.artist,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToApiKeyConfig = { navController.navigate(SettingsDestination.ApiKeyConfig) }
            )
        }

        libraryNavGraph(
            navController = navController,
            playerViewModel = playerViewModel
        )

        settingsNavGraph(
            navController = navController,
            onShowToast = onShowToast
        )
    }
}

private fun NavGraphBuilder.libraryNavGraph(
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    navigation<NavGraph.Library>(
        startDestination = LibraryDestination.Songs
    ) {
        composable<LibraryDestination.Songs> {
            SongsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { },
                onNavigateToSongDetail = { songId -> navController.navigate(Screen.SongDetail(songId)) },
                playerViewModel = playerViewModel
            )
        }

        composable<LibraryDestination.Albums> {
            AlbumsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAlbumDetail = { albumId -> navController.navigate(LibraryDestination.AlbumDetail(albumId)) }
            )
        }

        composable<LibraryDestination.Artists> {
            ArtistsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToArtistDetail = { artistId -> navController.navigate(LibraryDestination.ArtistDetail(artistId)) }
            )
        }

        composable<LibraryDestination.Playlists> {
            PlaylistsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlaylistDetail = { playlistId -> navController.navigate(LibraryDestination.PlaylistDetail(playlistId)) }
            )
        }

        composable<LibraryDestination.Favorites> {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<LibraryDestination.RecentlyPlayed> {
            RecentlyPlayedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<LibraryDestination.MostPlayed> {
            MostPlayedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }

        composable<LibraryDestination.AlbumDetail> {
            AlbumDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToApiKeyConfig = { navController.navigate(SettingsDestination.ApiKeyConfig) },
                onNavigateToAiInfo = { type, name, artist -> navController.navigate(Screen.AiInfo(type, name, artist)) }
            )
        }

        composable<LibraryDestination.ArtistDetail> {
            ArtistDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> },
                onNavigateToApiKeyConfig = { navController.navigate(SettingsDestination.ApiKeyConfig) },
                onNavigateToAiInfo = { type, name, artist -> navController.navigate(Screen.AiInfo(type, name, artist)) }
            )
        }

        composable<LibraryDestination.PlaylistDetail> {
            PlaylistDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlayer = { songId -> }
            )
        }
    }
}

private fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController,
    onShowToast: (String) -> Unit
) {
    navigation<NavGraph.Settings>(
        startDestination = SettingsDestination.Main
    ) {
        composable<SettingsDestination.Main> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScan = { navController.navigate(SettingsDestination.Scan) },
                onNavigateToExcludeFolders = { navController.navigate(SettingsDestination.ExcludeFolders) },
                onNavigateToApiKeyConfig = { navController.navigate(SettingsDestination.ApiKeyConfig) },
                onNavigateToLibraryStats = { navController.navigate(SettingsDestination.LibraryStats) }
            )
        }

        composable<SettingsDestination.ExcludeFolders> {
            ExcludeFoldersScreen(
                onNavigateBack = { navController.popBackStack() },
                onShowToast = onShowToast
            )
        }

        composable<SettingsDestination.Scan> {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onShowToast = onShowToast
            )
        }

        composable<SettingsDestination.ApiKeyConfig> {
            ApiKeyConfigScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SettingsDestination.LibraryStats> {
            LibraryStatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
