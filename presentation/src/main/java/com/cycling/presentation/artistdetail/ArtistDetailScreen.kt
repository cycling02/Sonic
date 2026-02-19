package com.cycling.presentation.artistdetail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cycling.presentation.ai.AiInfoCard
import com.cycling.presentation.ai.AiInfoIntent
import com.cycling.presentation.ai.AiInfoViewModel
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.components.PlayActionButtons
import com.cycling.presentation.components.SongListItem
import com.cycling.presentation.theme.SonicColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToApiKeyConfig: () -> Unit = {},
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    aiInfoViewModel: AiInfoViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val aiUiState by aiInfoViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAiInfo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ArtistDetailEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
            }
        }
    }

    if (showAiInfo) {
        AiInfoCard(
            uiState = aiUiState,
            onIntent = aiInfoViewModel::handleIntent,
            onNavigateToApiKeyConfig = {
                scope.launch { sheetState.hide() }
                showAiInfo = false
                onNavigateToApiKeyConfig()
            },
            onDismiss = {
                scope.launch { sheetState.hide() }
                showAiInfo = false
            },
            sheetState = sheetState
        )
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "歌手详情",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            uiState.artist?.let { artist ->
                                aiInfoViewModel.handleIntent(
                                    AiInfoIntent.LoadArtistInfo(artist.name)
                                )
                            }
                            showAiInfo = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI 介绍",
                            tint = SonicColors.Purple
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
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    ArtistHeader(
                        artist = uiState.artist,
                        songCount = uiState.songs.size
                    )
                }

                item {
                    PlayActionButtons(
                        onPlayAll = {
                            uiState.songs.firstOrNull()?.let { song ->
                                viewModel.handleIntent(ArtistDetailIntent.SongClick(song))
                            }
                        },
                        onShuffle = {
                            uiState.songs.randomOrNull()?.let { song ->
                                viewModel.handleIntent(ArtistDetailIntent.SongClick(song))
                            }
                        }
                    )
                }

                itemsIndexed(uiState.songs) { index, song ->
                    SongListItem(
                        song = song,
                        showDivider = index < uiState.songs.size - 1,
                        subtitle = song.album
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistHeader(
    artist: com.cycling.domain.model.Artist?,
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
                .size(160.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (artist?.artistArt != null) {
                AsyncImage(
                    model = artist.artistArt,
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
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SonicColors.Blue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = artist?.name ?: "未知歌手",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        val albumCount = artist?.numberOfAlbums ?: 0
        Text(
            text = "$albumCount 张专辑 · $songCount 首歌曲",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
