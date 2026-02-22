package com.cycling.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.presentation.components.IOSScreenWithLargeTitle
import com.cycling.presentation.components.IOSInsetGrouped
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.player.MiniPlayer
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSongs: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onNavigateToArtists: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAlbumDetail: (Long) -> Unit,
    onNavigateToArtistDetail: (Long) -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToMyMusic: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToFolders: () -> Unit = {},
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle(
        initialValue = com.cycling.domain.model.PlayerState()
    )
    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToSongs -> onNavigateToSongs()
                is HomeEffect.NavigateToAlbums -> onNavigateToAlbums()
                is HomeEffect.NavigateToArtists -> onNavigateToArtists()
                is HomeEffect.NavigateToPlaylists -> onNavigateToPlaylists()
                is HomeEffect.NavigateToSettings -> onNavigateToSettings()
                is HomeEffect.NavigateToAlbumDetail -> onNavigateToAlbumDetail(effect.albumId)
                is HomeEffect.NavigateToArtistDetail -> onNavigateToArtistDetail(effect.artistId)
                is HomeEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
                is HomeEffect.NavigateToMyMusic -> onNavigateToMyMusic()
                is HomeEffect.NavigateToSearch -> onNavigateToSearch()
                is HomeEffect.NavigateToFolders -> onNavigateToFolders()
                is HomeEffect.ShowToast -> {}
            }
        }
    }

    IOSScreenWithLargeTitle(
        title = "音乐",
        scrollState = scrollState,
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
        },
        showMiniPlayer = playerState.currentSong != null,
        miniPlayer = {
            MiniPlayer(
                currentSong = playerState.currentSong,
                isPlaying = playerState.isPlaying,
                playbackPosition = playerState.playbackPosition,
                duration = playerState.duration,
                onPlayPause = { viewModel.handleIntent(HomeIntent.PlayPause) },
                onClick = { viewModel.handleIntent(HomeIntent.MiniPlayerClick) }
            )
        },
        isLoading = uiState.isLoading,
        loadingContent = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    ) {
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sectionSpacing)
        ) {
            item {
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
            }

            item {
                QuickAccessSection(
                    onSongsClick = { viewModel.handleIntent(HomeIntent.NavigateToSongs) },
                    onAlbumsClick = { viewModel.handleIntent(HomeIntent.NavigateToAlbums) },
                    onArtistsClick = { viewModel.handleIntent(HomeIntent.NavigateToArtists) },
                    onPlaylistsClick = { viewModel.handleIntent(HomeIntent.NavigateToPlaylists) },
                    onMyMusicClick = { viewModel.handleIntent(HomeIntent.NavigateToMyMusic) },
                    onFoldersClick = { viewModel.handleIntent(HomeIntent.NavigateToFolders) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(bottomPadding))
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
    onMyMusicClick: () -> Unit,
    onFoldersClick: () -> Unit
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
            title = "文件夹",
            icon = Icons.Default.Folder,
            iconBackgroundColor = SonicColors.Indigo,
            onClick = onFoldersClick,
            showDivider = true
        )
        IOSListItem(
            title = "我的音乐",
            subtitle = "喜欢、最近播放、最常播放",
            icon = Icons.Default.Favorite,
            iconBackgroundColor = SonicColors.Pink,
            onClick = onMyMusicClick,
            showDivider = false
        )
    }
}
