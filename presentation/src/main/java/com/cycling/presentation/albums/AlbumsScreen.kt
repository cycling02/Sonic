package com.cycling.presentation.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.Album
import com.cycling.domain.model.SortOrder
import com.cycling.presentation.components.IOSCenteredContent
import com.cycling.presentation.components.IOSScreenWithLargeTitle
import com.cycling.presentation.components.IOSMediaCard
import com.cycling.presentation.components.IOSSortMenu
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

@Composable
fun AlbumsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAlbumDetail: (Long) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    var showSortMenu by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(SortOrder.TITLE) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AlbumsEffect.NavigateToAlbumDetail -> onNavigateToAlbumDetail(effect.albumId)
            }
        }
    }

    val sortedAlbums = remember(uiState.albums, sortOrder) {
        sortAlbums(uiState.albums, sortOrder)
    }

    IOSScreenWithLargeTitle(
        title = "专辑",
        scrollState = gridState,
        onNavigateBack = onNavigateBack,
        actions = {
            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "排序",
                        tint = SonicColors.Blue
                    )
                }
                IOSSortMenu(
                    expanded = showSortMenu,
                    onDismiss = { showSortMenu = false },
                    items = listOf(SortOrder.TITLE, SortOrder.ARTIST, SortOrder.ALBUM),
                    selectedItem = sortOrder,
                    itemName = { getSortOrderName(it) },
                    onSelect = { sortOrder = it }
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
        if (sortedAlbums.isEmpty()) {
            IOSCenteredContent(
                icon = Icons.Default.Album,
                iconTint = SonicColors.Teal,
                title = "暂无专辑",
                subtitle = "扫描本地音乐以添加专辑",
                button = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                contentPadding = PaddingValues(
                    start = DesignTokens.Spacing.md,
                    end = DesignTokens.Spacing.md,
                    top = DesignTokens.Spacing.sm,
                    bottom = DesignTokens.Spacing.xxl + bottomPadding
                ),
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
            ) {
                items(sortedAlbums, key = { it.id }) { album ->
                    IOSMediaCard(
                        title = album.name,
                        subtitle = album.artist,
                        artwork = album.albumArt,
                        onClick = { viewModel.handleIntent(AlbumsIntent.AlbumClick(album)) },
                        placeholderIcon = Icons.Default.Album
                    )
                }
            }
        }
    }
}

private fun sortAlbums(albums: List<Album>, sortOrder: SortOrder): List<Album> {
    return when (sortOrder) {
        SortOrder.TITLE -> albums.sortedBy { it.name.lowercase() }
        SortOrder.ARTIST -> albums.sortedBy { it.artist.lowercase() }
        SortOrder.ALBUM -> albums.sortedBy { it.name.lowercase() }
        else -> albums.sortedBy { it.name.lowercase() }
    }
}

private fun getSortOrderName(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.TITLE -> "按专辑名"
        SortOrder.ARTIST -> "按艺术家"
        SortOrder.ALBUM -> "按专辑名"
        else -> "按专辑名"
    }
}
