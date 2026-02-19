package com.cycling.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.Album
import com.cycling.domain.model.Artist
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSArtistCard
import com.cycling.presentation.components.IOSInsetGrouped
import com.cycling.presentation.components.IOSLargeTitleTopAppBar
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.components.IOSMediaCard
import com.cycling.presentation.components.IOSSectionHeader
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
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                QuickAccessSection(
                    onSongsClick = { viewModel.handleIntent(HomeIntent.NavigateToSongs) },
                    onAlbumsClick = { viewModel.handleIntent(HomeIntent.NavigateToAlbums) },
                    onArtistsClick = { viewModel.handleIntent(HomeIntent.NavigateToArtists) },
                    onPlaylistsClick = { viewModel.handleIntent(HomeIntent.NavigateToPlaylists) }
                )

                if (uiState.recentlyPlayed.isNotEmpty()) {
                    IOSSectionHeader(title = "最近播放")
                    HorizontalSongSection(
                        songs = uiState.recentlyPlayed,
                        onSongClick = { viewModel.handleIntent(HomeIntent.SongClick(it)) }
                    )
                }

                if (uiState.topAlbums.isNotEmpty()) {
                    IOSSectionHeader(title = "热门专辑")
                    HorizontalAlbumSection(
                        albums = uiState.topAlbums,
                        onAlbumClick = { viewModel.handleIntent(HomeIntent.AlbumClick(it)) }
                    )
                }

                if (uiState.topArtists.isNotEmpty()) {
                    IOSSectionHeader(title = "热门歌手")
                    HorizontalArtistSection(
                        artists = uiState.topArtists,
                        onArtistClick = { viewModel.handleIntent(HomeIntent.ArtistClick(it)) }
                    )
                }

                if (uiState.recentlyAdded.isNotEmpty()) {
                    IOSSectionHeader(title = "最近添加")
                    HorizontalSongSection(
                        songs = uiState.recentlyAdded,
                        onSongClick = { viewModel.handleIntent(HomeIntent.SongClick(it)) }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
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
    onPlaylistsClick: () -> Unit
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
            showDivider = false
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun HorizontalSongSection(
    songs: List<Song>,
    onSongClick: (Song) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            IOSMediaCard(
                title = song.title,
                subtitle = song.artist,
                artwork = song.albumArt,
                onClick = { onSongClick(song) }
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun HorizontalAlbumSection(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(albums, key = { it.id }) { album ->
            IOSMediaCard(
                title = album.name,
                subtitle = album.artist,
                artwork = album.albumArt,
                onClick = { onAlbumClick(album) }
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun HorizontalArtistSection(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(artists, key = { it.id }) { artist ->
            IOSArtistCard(
                name = artist.name,
                artwork = artist.artistArt,
                onClick = { onArtistClick(artist) }
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
