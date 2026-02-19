package com.cycling.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.ThemeMode
import com.cycling.presentation.components.IOSInsetGrouped
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.theme.SonicColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToExcludeFolders: () -> Unit,
    onNavigateToApiKeyConfig: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val hasApiKey by viewModel.hasApiKey.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "设置",
                onNavigateBack = onNavigateBack
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

            IOSInsetGrouped {
                IOSListItem(
                    title = "扫描本地音乐",
                    icon = Icons.Default.Refresh,
                    iconBackgroundColor = SonicColors.Blue,
                    onClick = onNavigateToScan,
                    showDivider = true
                )
                IOSListItem(
                    title = "排除文件夹",
                    icon = Icons.Default.Folder,
                    iconBackgroundColor = SonicColors.Orange,
                    onClick = onNavigateToExcludeFolders,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            IOSInsetGrouped {
                IOSListItem(
                    title = "DeepSeek API 配置",
                    subtitle = if (hasApiKey) "已配置" else "未配置",
                    icon = Icons.Default.Key,
                    iconBackgroundColor = SonicColors.Purple,
                    onClick = onNavigateToApiKeyConfig,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            IOSInsetGrouped {
                ThemeOptionItem(
                    text = "跟随系统",
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                    showDivider = true
                )
                ThemeOptionItem(
                    text = "浅色模式",
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                    showDivider = true
                )
                ThemeOptionItem(
                    text = "深色模式",
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    IOSListItem(
        title = text,
        onClick = onClick,
        showDivider = showDivider,
        trailing = {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SonicColors.Green,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}
