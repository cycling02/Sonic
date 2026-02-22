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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSScreenWithTopBar
import com.cycling.presentation.components.MenuItem
import com.cycling.presentation.components.PlayActionButtons
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.components.formatDuration
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

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

    IOSScreenWithTopBar(
        title = "播放列表详情",
        onNavigateBack = onNavigateBack,
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                            .padding(DesignTokens.Spacing.xl),
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

            item {
                Spacer(modifier = Modifier.height(bottomPadding))
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
            .padding(DesignTokens.Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(DesignTokens.Card.mediaCardWidth)
                .clip(RoundedCornerShape(DesignTokens.CornerRadius.large)),
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

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Text(
            text = playlist?.name ?: "未知播放列表",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        Text(
            text = "$songCount 首歌曲",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
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
