package com.cycling.presentation.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSLargeTitleTopAppBar
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FavoritesEffect.ShowToast -> {}
                is FavoritesEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
            }
        }
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "喜欢的歌曲",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (uiState.songs.isNotEmpty()) {
                        IconButton(onClick = { viewModel.handleIntent(FavoritesIntent.PlayAll) }) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "全部播放",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { viewModel.handleIntent(FavoritesIntent.ShuffleAll) }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "随机播放",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.padding(bottom = 16.dp),
                        tint = SonicColors.Pink
                    )
                    Text(
                        text = "还没有喜欢的歌曲",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "在播放器中点击心形图标来收藏歌曲",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    Text(
                        text = "${uiState.songs.size} 首歌曲",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(uiState.songs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        showDivider = true,
                        modifier = Modifier.clickable { viewModel.handleIntent(FavoritesIntent.SongClick(song)) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.handleIntent(FavoritesIntent.ToggleFavorite(song)) }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "取消收藏",
                                    tint = SonicColors.Red
                                )
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
