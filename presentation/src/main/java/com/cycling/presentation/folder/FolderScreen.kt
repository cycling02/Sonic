package com.cycling.presentation.folder

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.FolderItem
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSCenteredContent
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.components.IOSScreenWithTopBar
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.components.formatDuration
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

@Composable
fun FolderScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long, List<Song>) -> Unit,
    viewModel: FolderViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FolderEffect.NavigateToPlayer -> {
                    onNavigateToPlayer(effect.songId, effect.songs)
                }
                is FolderEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    IOSScreenWithTopBar(
        title = uiState.title,
        onNavigateBack = if (uiState.parentPath != null) {
            { viewModel.handleIntent(FolderIntent.NavigateBack) }
        } else {
            onNavigateBack
        }
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.folders.isEmpty() && uiState.songs.isEmpty()) {
            IOSCenteredContent(
                icon = Icons.Default.Folder,
                iconTint = SonicColors.Blue,
                title = "暂无内容",
                subtitle = "此文件夹中没有音乐文件",
                button = {}
            )
        } else {
            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                }

                items(uiState.folders, key = { it.path }) { folder ->
                    FolderItem(
                        folder = folder,
                        onClick = {
                            viewModel.handleIntent(FolderIntent.NavigateToFolder(folder.path))
                        }
                    )
                }

                items(uiState.songs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        showDivider = true,
                        subtitle = "${song.artist} · ${formatDuration(song.duration)}",
                        showDuration = false,
                        showMoreButton = false,
                        onClick = {
                            viewModel.handleIntent(FolderIntent.SongClick(song))
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}

@Composable
private fun FolderItem(
    folder: FolderItem,
    onClick: () -> Unit
) {
    IOSListItem(
        title = folder.name,
        icon = Icons.Default.Folder,
        iconBackgroundColor = SonicColors.Blue,
        subtitle = "${folder.songCount} 首歌曲",
        onClick = onClick,
        showDivider = true
    )
}
