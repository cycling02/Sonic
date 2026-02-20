package com.cycling.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.presentation.components.IOSInsetGrouped
import com.cycling.presentation.components.IOSLargeTitleTopAppBar
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.player.MiniPlayer
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSongs: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onNavigateToArtists: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToAlbumDetail: (Long) -> Unit,
    onNavigateToArtistDetail: (Long) -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToRecentlyPlayed: () -> Unit,
    onNavigateToMostPlayed: () -> Unit,
    onNavigateToSearch: () -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle(
        initialValue = com.cycling.domain.model.PlayerState()
    )
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToSongs -> onNavigateToSongs()
                is HomeEffect.NavigateToAlbums -> onNavigateToAlbums()
                is HomeEffect.NavigateToArtists -> onNavigateToArtists()
                is HomeEffect.NavigateToPlaylists -> onNavigateToPlaylists()
                is HomeEffect.NavigateToSettings -> onNavigateToSettings()
                is HomeEffect.NavigateToScan -> onNavigateToScan()
                is HomeEffect.NavigateToAlbumDetail -> onNavigateToAlbumDetail(effect.albumId)
                is HomeEffect.NavigateToArtistDetail -> onNavigateToArtistDetail(effect.artistId)
                is HomeEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
                is HomeEffect.NavigateToFavorites -> onNavigateToFavorites()
                is HomeEffect.NavigateToRecentlyPlayed -> onNavigateToRecentlyPlayed()
                is HomeEffect.NavigateToMostPlayed -> onNavigateToMostPlayed()
                is HomeEffect.NavigateToSearch -> onNavigateToSearch()
                is HomeEffect.ShowToast -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            IOSLargeTitleTopAppBar(
                title = "音乐",
                isLarge = scrollState.value < 10,
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(HomeIntent.NavigateToSearch) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewModel.handleIntent(HomeIntent.NavigateToSettings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    QuickAccessSection(
                        onSongsClick = { viewModel.handleIntent(HomeIntent.NavigateToSongs) },
                        onAlbumsClick = { viewModel.handleIntent(HomeIntent.NavigateToAlbums) },
                        onArtistsClick = { viewModel.handleIntent(HomeIntent.NavigateToArtists) },
                        onPlaylistsClick = { viewModel.handleIntent(HomeIntent.NavigateToPlaylists) },
                        onFavoritesClick = { viewModel.handleIntent(HomeIntent.NavigateToFavorites) },
                        onRecentlyPlayedClick = { viewModel.handleIntent(HomeIntent.NavigateToRecentlyPlayed) },
                        onMostPlayedClick = { viewModel.handleIntent(HomeIntent.NavigateToMostPlayed) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.height(bottomPadding))
                }

                if (playerState.currentSong != null) {
                    MiniPlayer(
                        currentSong = playerState.currentSong,
                        isPlaying = playerState.isPlaying,
                        playbackPosition = playerState.playbackPosition,
                        duration = playerState.duration,
                        onPlayPause = { viewModel.handleIntent(HomeIntent.PlayPause) },
                        onClick = { viewModel.handleIntent(HomeIntent.MiniPlayerClick) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessSection(
    onSongsClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onArtistsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onRecentlyPlayedClick: () -> Unit,
    onMostPlayedClick: () -> Unit
) {
    IOSInsetGrouped {
        IOSListItem(
            title = "歌曲",
            icon = Icons.Default.MusicNote,
            iconBackgroundColor = SonicColors.Red,
            onClick = onSongsClick,
            showDivider = true
        )
        IOSListItem(
            title = "专辑",
            icon = Icons.Default.Album,
            iconBackgroundColor = SonicColors.Teal,
            onClick = onAlbumsClick,
            showDivider = true
        )
        IOSListItem(
            title = "歌手",
            icon = Icons.Default.Person,
            iconBackgroundColor = SonicColors.Blue,
            onClick = onArtistsClick,
            showDivider = true
        )
        IOSListItem(
            title = "播放列表",
            icon = Icons.AutoMirrored.Filled.QueueMusic,
            iconBackgroundColor = SonicColors.Green,
            onClick = onPlaylistsClick,
            showDivider = true
        )
        IOSListItem(
            title = "喜欢的歌曲",
            icon = Icons.Default.Favorite,
            iconBackgroundColor = SonicColors.Pink,
            onClick = onFavoritesClick,
            showDivider = true
        )
        IOSListItem(
            title = "最近播放",
            icon = Icons.Default.History,
            iconBackgroundColor = SonicColors.Orange,
            onClick = onRecentlyPlayedClick,
            showDivider = true
        )
        IOSListItem(
            title = "最常播放",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            iconBackgroundColor = SonicColors.Purple,
            onClick = onMostPlayedClick,
            showDivider = false
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}
