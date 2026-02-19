package com.cycling.presentation.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ScanEffect.ShowToast -> onShowToast(effect.message)
                is ScanEffect.NavigateBack -> onNavigateBack()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("扫描本地音乐") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(ScanIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            when {
                uiState.isScanning -> ScanningContent(uiState)
                uiState.scanResult != null -> CompletedContent(
                    result = uiState.scanResult!!,
                    onScanAgain = { viewModel.handleIntent(ScanIntent.ResetScan) }
                )
                uiState.error != null -> ErrorContent(
                    error = uiState.error!!,
                    onRetry = { viewModel.handleIntent(ScanIntent.StartScan) }
                )
                else -> IdleContent(
                    onStartScan = { viewModel.handleIntent(ScanIntent.StartScan) }
                )
            }
        }
    }
}

@Composable
private fun IdleContent(
    onStartScan: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "扫描本地音乐",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "扫描设备上的音乐文件并添加到音乐库",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStartScan,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("开始扫描")
        }
    }
}

@Composable
private fun ScanningContent(
    state: ScanUiState
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = when (state.scanStep) {
                ScanStep.QueryingMediaStore -> "正在查询媒体库..."
                ScanStep.SavingToDatabase -> "正在保存到数据库..."
                else -> "扫描中..."
            },
            style = MaterialTheme.typography.titleMedium
        )
        
        if (state.totalSongs > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${state.songsProcessed} / ${state.totalSongs} 首歌曲",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompletedContent(
    result: com.cycling.domain.model.ScanResult,
    onScanAgain: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "扫描完成",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResultRow("歌曲", result.songsFound)
            ResultRow("专辑", result.albumsFound)
            ResultRow("歌手", result.artistsFound)
            ResultRow("耗时", "${result.duration}ms")
        }
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = onScanAgain) {
            Text("重新扫描")
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: Any
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.5f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "扫描失败",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}


