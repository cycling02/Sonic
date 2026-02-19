package com.cycling.presentation.playlistdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.components.MenuItem
import com.cycling.presentation.components.PlayActionButtons
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.components.formatDuration
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PlaylistDetailEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
                is PlaylistDetailEffect.ShowToast -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "播放列表详情",
                onNavigateBack = onNavigateBack
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    PlaylistHeader(
                        playlist = uiState.playlist,
                        songCount = uiState.songs.size
                    )
                }

                item {
                    PlayActionButtons(
                        onPlayAll = {
                            uiState.songs.firstOrNull()?.let { song ->
                                viewModel.handleIntent(PlaylistDetailIntent.SongClick(song))
                            }
                        },
                        onShuffle = {
                            uiState.songs.randomOrNull()?.let { song ->
                                viewModel.handleIntent(PlaylistDetailIntent.SongClick(song))
                            }
                        }
                    )
                }

                if (uiState.songs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "播放列表为空",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    itemsIndexed(uiState.songs) { index, song ->
                        SongItemWithMenu(
                            song = song,
                            showDivider = index < uiState.songs.size - 1,
                            subtitle = formatDuration(song.duration),
                            onRemoveSong = { songId ->
                                viewModel.handleIntent(PlaylistDetailIntent.RemoveSong(songId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistHeader(
    playlist: com.cycling.domain.model.Playlist?,
    songCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = SonicColors.Teal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = playlist?.name ?: "未知播放列表",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$songCount 首歌曲",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SongItemWithMenu(
    song: Song,
    showDivider: Boolean,
    subtitle: String,
    onRemoveSong: (Long) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    SongListItem(
        song = song,
        showDivider = showDivider,
        subtitle = subtitle,
        showDuration = false,
        showMoreButton = true,
        moreMenuExpanded = menuExpanded,
        onMoreClick = { menuExpanded = true },
        onMoreDismiss = { menuExpanded = false },
        moreMenuItems = listOf(
            MenuItem(
                name = "从播放列表移除",
                isDestructive = true,
                onClick = { onRemoveSong(song.id) }
            )
        )
    )
}
