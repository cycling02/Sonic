package com.cycling.presentation.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.presentation.components.IOSCardContainer
import com.cycling.presentation.components.IOSCenteredContent
import com.cycling.presentation.components.IOSFilledButton
import com.cycling.presentation.components.IOSResultRow
import com.cycling.presentation.components.IOSTextButton
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        viewModel.handleIntent(ScanIntent.PermissionResult(allGranted))
    }

    val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(Unit) {
        val hasPermission = storagePermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        viewModel.checkAndRequestPermission(hasPermission)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ScanEffect.ShowToast -> onShowToast(effect.message)
                is ScanEffect.NavigateBack -> onNavigateBack()
                is ScanEffect.RequestStoragePermission -> {
                    permissionLauncher.launch(storagePermissions)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "扫描本地音乐",
                onNavigateBack = { viewModel.handleIntent(ScanIntent.NavigateBack) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isScanning -> ScanningContent(uiState)
                uiState.scanResult != null -> CompletedContent(
                    result = uiState.scanResult!!,
                    onScanAgain = { viewModel.handleIntent(ScanIntent.ResetScan) }
                )
                uiState.shouldRequestPermission && !uiState.hasStoragePermission -> PermissionRequiredContent(
                    onRequestPermission = { viewModel.handleIntent(ScanIntent.RequestPermission) }
                )
                uiState.error != null -> ErrorContent(
                    error = uiState.error!!,
                    onRetry = {
                        if (!uiState.hasStoragePermission) {
                            viewModel.handleIntent(ScanIntent.RequestPermission)
                        } else {
                            viewModel.handleIntent(ScanIntent.StartScan)
                        }
                    }
                )
                else -> IdleContent(
                    onStartScan = { viewModel.handleIntent(ScanIntent.StartScan) }
                )
            }
        }
    }
}

@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit
) {
    IOSCenteredContent(
        icon = Icons.Default.Folder,
        iconTint = MaterialTheme.colorScheme.primary,
        title = "需要存储权限",
        subtitle = "为了扫描您设备上的音乐文件，请授予存储访问权限"
    ) {
        IOSFilledButton(
            text = "授予权限",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

@Composable
private fun IdleContent(
    onStartScan: () -> Unit
) {
    IOSCenteredContent(
        icon = Icons.Default.Refresh,
        iconTint = MaterialTheme.colorScheme.primary,
        title = "扫描本地音乐",
        subtitle = "扫描设备上的音乐文件并添加到音乐库"
    ) {
        IOSFilledButton(
            text = "开始扫描",
            onClick = onStartScan,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
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
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = when (state.scanStep) {
                ScanStep.QueryingMediaStore -> "正在查询媒体库..."
                ScanStep.SavingToDatabase -> "正在保存到数据库..."
                else -> "扫描中..."
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (state.totalSongs > 0) {
            Spacer(modifier = Modifier.height(24.dp))
            IOSCardContainer(
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${state.songsProcessed} / ${state.totalSongs} 首歌曲",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(SonicColors.Green),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "扫描完成",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        IOSCardContainer(
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            IOSResultRow(label = "歌曲", value = result.songsFound.toString())
            IOSResultRow(label = "专辑", value = result.albumsFound.toString())
            IOSResultRow(label = "歌手", value = result.artistsFound.toString())
            IOSResultRow(label = "耗时", value = "${result.duration}ms", isLast = true)
        }
        Spacer(modifier = Modifier.height(32.dp))
        IOSTextButton(
            text = "重新扫描",
            onClick = onScanAgain
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    IOSCenteredContent(
        icon = Icons.Default.Error,
        iconTint = MaterialTheme.colorScheme.error,
        title = "扫描失败",
        subtitle = error,
        titleColor = MaterialTheme.colorScheme.error
    ) {
        IOSFilledButton(
            text = "重试",
            onClick = onRetry,
            backgroundColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}
