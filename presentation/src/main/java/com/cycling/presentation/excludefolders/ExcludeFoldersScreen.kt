package com.cycling.presentation.excludefolders

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.ExcludedFolder
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcludeFoldersScreen(
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit,
    viewModel: ExcludeFoldersViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val path = getFolderPathFromUri(uri)
            if (path != null) {
                viewModel.handleIntent(ExcludeFoldersIntent.AddFolder(path))
            } else {
                onShowToast("无法获取文件夹路径")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ExcludeFoldersEffect.ShowToast -> onShowToast(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "排除文件夹",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { folderPickerLauncher.launch(null) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加文件夹",
                            tint = SonicColors.Blue
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                EmptyContentState(
                    icon = Icons.Default.Folder,
                    title = "加载中..."
                )
            } else if (uiState.folders.isEmpty()) {
                EmptyContentState(
                    icon = Icons.Default.Folder,
                    title = "暂无排除的文件夹",
                    subtitle = "点击右上角 + 添加要排除的文件夹"
                )
            } else {
                LazyColumn {
                    items(uiState.folders, key = { it.path }) { folder ->
                        ExcludedFolderItem(
                            folder = folder,
                            onDelete = {
                                viewModel.handleIntent(ExcludeFoldersIntent.RemoveFolder(folder.path))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContentState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExcludedFolderItem(
    folder: ExcludedFolder,
    onDelete: () -> Unit
) {
    IOSListItem(
        title = getFolderDisplayName(folder.path),
        icon = Icons.Default.Folder,
        iconBackgroundColor = SonicColors.Blue,
        subtitle = folder.path,
        onClick = {},
        trailing = {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = SonicColors.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        showDivider = true
    )
}

private fun getFolderPathFromUri(uri: Uri): String? {
    val path = uri.path ?: return null
    val treeDocumentId = path.substringAfterLast(":")
    return "/storage/emulated/0/$treeDocumentId"
}

private fun getFolderDisplayName(path: String): String {
    return path.substringAfterLast("/")
}
