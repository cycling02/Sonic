package com.cycling.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cycling.presentation.theme.SonicColors
import com.cycling.presentation.theme.SonicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSTopAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            actions?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSLargeTitleTopAppBar(
    title: String,
    isLarge: Boolean,
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = if (isLarge) MaterialTheme.typography.headlineLarge
                else MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        actions = {
            actions?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isLarge) MaterialTheme.colorScheme.background
            else MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

@Composable
fun IOSSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = "查看全部",
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        onActionClick?.let {
            Text(
                text = action ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = it)
            )
        }
    }
}

@Composable
fun IOSCenteredContent(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colorScheme.onBackground,
    button: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = iconTint
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        button()
    }
}

@Composable
fun IOSResultRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
fun IOSCardContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        content()
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSTopAppBarPreview() {
    SonicTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            IOSTopAppBar(title = "设置", onNavigateBack = {})
            IOSTopAppBar(
                title = "扫描本地音乐",
                onNavigateBack = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Light - Large")
@Preview(showBackground = true, name = "Dark - Large", uiMode = 32)
@Composable
private fun IOSLargeTitleTopAppBarPreview() {
    SonicTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            IOSLargeTitleTopAppBar(
                title = "音乐",
                isLarge = true,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
            IOSLargeTitleTopAppBar(
                title = "音乐",
                isLarge = false,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSSectionHeaderPreview() {
    SonicTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IOSSectionHeader(title = "最近播放", onActionClick = {})
            IOSSectionHeader(title = "热门专辑", action = "更多", onActionClick = {})
            IOSSectionHeader(title = "无操作标题", onActionClick = null)
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Light")
@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Dark", uiMode = 32)
@Composable
private fun IOSCenteredContentPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                IOSCenteredContent(
                    icon = Icons.Default.Refresh,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "扫描本地音乐",
                    subtitle = "扫描设备上的音乐文件并添加到音乐库",
                    button = { IOSFilledButton(text = "开始扫描", onClick = {}) }
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                IOSCenteredContent(
                    icon = Icons.Default.Error,
                    iconTint = MaterialTheme.colorScheme.error,
                    title = "扫描失败",
                    subtitle = "无法访问存储，请检查权限设置",
                    titleColor = MaterialTheme.colorScheme.error,
                    button = {
                        IOSFilledButton(
                            text = "重试",
                            onClick = {},
                            backgroundColor = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSResultRowPreview() {
    SonicTheme {
        IOSCardContainer(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            IOSResultRow(label = "歌曲", value = "128")
            IOSResultRow(label = "专辑", value = "32")
            IOSResultRow(label = "歌手", value = "16")
            IOSResultRow(label = "耗时", value = "1234ms", isLast = true)
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSCardContainerPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IOSCardContainer {
                Text(
                    text = "卡片标题",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "这是卡片内容描述文字",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IOSCardContainer(modifier = Modifier.fillMaxWidth(0.85f)) {
                IOSResultRow(label = "歌曲", value = "128")
                IOSResultRow(label = "专辑", value = "32", isLast = true)
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Light")
@Preview(showBackground = true, device = "spec:width=360dp,height=640dp", name = "Dark", uiMode = 32)
@Composable
private fun CompletedContentPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
            IOSCardContainer(modifier = Modifier.fillMaxWidth(0.85f)) {
                IOSResultRow(label = "歌曲", value = "128")
                IOSResultRow(label = "专辑", value = "32")
                IOSResultRow(label = "歌手", value = "16")
                IOSResultRow(label = "耗时", value = "1234ms", isLast = true)
            }
            Spacer(modifier = Modifier.height(32.dp))
            IOSTextButton(text = "重新扫描", onClick = {})
        }
    }
}
