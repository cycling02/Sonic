package com.cycling.presentation.albumdetail

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
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cycling.presentation.components.IOSScreenWithTopBar
import com.cycling.presentation.components.PlayActionButtons
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.components.formatDuration
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

@Composable
fun AlbumDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToApiKeyConfig: () -> Unit = {},
    onNavigateToAiInfo: (String, String, String) -> Unit = { _, _, _ -> },
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AlbumDetailEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
            }
        }
    }

    IOSScreenWithTopBar(
        title = "专辑详情",
        onNavigateBack = onNavigateBack,
        actions = {
            IconButton(
                onClick = {
                    uiState.album?.let { album ->
                        onNavigateToAiInfo("album", album.name, album.artist)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI 介绍",
                    tint = SonicColors.Purple
                )
            }
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
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                AlbumHeader(
                    album = uiState.album,
                    songCount = uiState.songs.size
                )
            }

            item {
                PlayActionButtons(
                    onPlayAll = {
                        uiState.songs.firstOrNull()?.let { song ->
                            viewModel.handleIntent(AlbumDetailIntent.SongClick(song))
                        }
                    },
                    onShuffle = {
                        uiState.songs.randomOrNull()?.let { song ->
                            viewModel.handleIntent(AlbumDetailIntent.SongClick(song))
                        }
                    }
                )
            }

            itemsIndexed(uiState.songs) { index, song ->
                SongListItem(
                    song = song,
                    showDivider = index < uiState.songs.size - 1,
                    subtitle = formatDuration(song.duration),
                    showDuration = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(bottomPadding))
            }
        }
    }
}

@Composable
private fun AlbumHeader(
    album: com.cycling.domain.model.Album?,
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
                .size(200.dp)
                .clip(RoundedCornerShape(DesignTokens.CornerRadius.large)),
            contentAlignment = Alignment.Center
        ) {
            if (album?.albumArt != null) {
                AsyncImage(
                    model = album.albumArt,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = SonicColors.Teal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))

        Text(
            text = album?.name ?: "未知专辑",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))

        Text(
            text = album?.artist ?: "未知歌手",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        val year = album?.firstYear ?: 0
        val yearText = if (year > 0) "$year · " else ""
        Text(
            text = "$yearText$songCount 首歌曲",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
    }
}
