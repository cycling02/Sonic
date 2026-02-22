package com.cycling.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.ThemeMode
import com.cycling.presentation.components.IOSInsetGrouped
import com.cycling.presentation.components.IOSListItem
import com.cycling.presentation.components.IOSListSectionHeader
import com.cycling.presentation.components.IOSScreenWithTopBar
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToExcludeFolders: () -> Unit,
    onNavigateToApiKeyConfig: () -> Unit = {},
    onNavigateToLibraryStats: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val hasApiKey by viewModel.hasApiKey.collectAsStateWithLifecycle()

    IOSScreenWithTopBar(
        title = "设置",
        onNavigateBack = onNavigateBack
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))

        IOSListSectionHeader(title = "音乐库")
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
                showDivider = true
            )
            IOSListItem(
                title = "音乐库统计",
                icon = Icons.Default.BarChart,
                iconBackgroundColor = SonicColors.Green,
                onClick = onNavigateToLibraryStats,
                showDivider = false
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sectionSpacing))

        IOSListSectionHeader(title = "播放")
        IOSInsetGrouped {
            IOSListItem(
                title = "DeepSeek API 配置",
                subtitle = if (hasApiKey) "已配置" else "未配置",
                icon = Icons.Default.Key,
                iconBackgroundColor = SonicColors.Purple,
                onClick = onNavigateToApiKeyConfig,
                showDivider = true
            )
            IOSListItem(
                title = "主题",
                subtitle = getThemeModeText(themeMode),
                icon = Icons.Default.Palette,
                iconBackgroundColor = SonicColors.Indigo,
                onClick = {},
                showDivider = false
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sectionSpacing))

        IOSListSectionHeader(title = "外观")
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

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sectionSpacing))

        IOSListSectionHeader(title = "关于")
        IOSInsetGrouped {
            IOSListItem(
                title = "版本",
                subtitle = "1.0.0",
                icon = Icons.Default.Info,
                iconBackgroundColor = SonicColors.Teal,
                onClick = {},
                showDivider = false,
                trailing = {
                    Text(
                        text = "1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sectionSpacing))
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

@Composable
private fun getThemeModeText(themeMode: ThemeMode): String {
    return when (themeMode) {
        ThemeMode.SYSTEM -> "跟随系统"
        ThemeMode.LIGHT -> "浅色模式"
        ThemeMode.DARK -> "深色模式"
    }
}
