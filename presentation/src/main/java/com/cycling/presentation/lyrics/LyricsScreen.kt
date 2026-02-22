package com.cycling.presentation.lyrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.lyrics.components.KaraokeLyricsView
import com.cycling.presentation.theme.DesignTokens
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
    onNavigateBack: () -> Unit,
    viewModel: LyricsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LyricsEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    val animatedPositionState = remember { mutableLongStateOf(0L) }
    val currentPositionProvider = remember {
        { animatedPositionState.longValue.toInt() }
    }

    val latestPlaybackState by rememberUpdatedState(
        Triple(uiState.isPlaying, uiState.playbackPosition, uiState.lastUpdateTime)
    )

    LaunchedEffect(latestPlaybackState.first) {
        val (isPlaying, position, lastUpdateTime) = latestPlaybackState
        if (isPlaying) {
            while (true) {
                val elapsed = System.currentTimeMillis() - lastUpdateTime
                val newPosition = (position + elapsed).coerceAtMost(uiState.duration)

                val currentAnimPos = animatedPositionState.longValue

                if (currentAnimPos <= newPosition || abs(newPosition - currentAnimPos) >= 100) {
                    animatedPositionState.longValue = newPosition
                }
                awaitFrame()
            }
        } else {
            animatedPositionState.longValue = position
        }
    }

    val listState = rememberLazyListState()

    val songTitle = uiState.currentSong?.title ?: "未知歌曲"

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = songTitle,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                !uiState.hasLyrics -> {
                    NoLyricsContent(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    uiState.lyrics?.let { lyrics ->
                        KaraokeLyricsView(
                            listState = listState,
                            lyrics = lyrics,
                            currentPosition = currentPositionProvider,
                            onLineClicked = { line -> viewModel.onLineClicked(line) },
                            onLinePressed = { },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = DesignTokens.Spacing.lg),
                            textColor = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoLyricsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = DesignTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(DesignTokens.Spacing.xxl),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
        Text(
            text = "暂无歌词",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
