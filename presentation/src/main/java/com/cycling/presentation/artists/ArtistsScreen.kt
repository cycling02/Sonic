package com.cycling.presentation.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Person
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
import com.cycling.presentation.components.IOSArtistCard
import com.cycling.presentation.components.IOSCenteredContent
import com.cycling.presentation.components.IOSScreenWithLargeTitle
import com.cycling.presentation.theme.DesignTokens

@Composable
fun ArtistsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToArtistDetail: (Long) -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ArtistsEffect.NavigateToArtistDetail -> onNavigateToArtistDetail(effect.artistId)
            }
        }
    }

    IOSScreenWithLargeTitle(
        title = "歌手",
        scrollState = gridState,
        onNavigateBack = onNavigateBack,
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "排序",
                    tint = MaterialTheme.colorScheme.primary
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
        if (uiState.artists.isEmpty()) {
            IOSCenteredContent(
                icon = Icons.Default.Person,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                title = "暂无歌手",
                subtitle = "您的音乐库中没有歌手信息",
                button = { }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = DesignTokens.Card.artistCardSize + DesignTokens.Spacing.lg),
                modifier = Modifier.fillMaxSize(),
                state = gridState,
                contentPadding = PaddingValues(
                    start = DesignTokens.Spacing.md,
                    end = DesignTokens.Spacing.md,
                    top = DesignTokens.Spacing.sm,
                    bottom = DesignTokens.Spacing.md + bottomPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                items(uiState.artists, key = { it.id }) { artist ->
                    IOSArtistCard(
                        name = artist.name,
                        artwork = artist.artistArt,
                        onClick = { viewModel.handleIntent(ArtistsIntent.ArtistClick(artist)) }
                    )
                }
            }
        }
    }
}
