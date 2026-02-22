package com.cycling.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors
import com.cycling.presentation.theme.SonicTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior? = null
) {
    LargeTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = navigationIcon ?: {},
        actions = { actions?.invoke() },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IOSLargeTitleTopAppBar(
    title: String,
    scrollState: LazyGridState,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior? = null
) {
    LargeTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 34.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = navigationIcon ?: {},
        actions = { actions?.invoke() },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
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
            .padding(horizontal = DesignTokens.Spacing.md, vertical = DesignTokens.Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
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
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = DesignTokens.Spacing.xl)
        )
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
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
                .padding(vertical = DesignTokens.Spacing.sm + DesignTokens.Spacing.xs),
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
            .clip(RoundedCornerShape(DesignTokens.CornerRadius.medium))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        content()
    }
}

@Composable
fun IOSScrollbar(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    thumbColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    thumbWidth: androidx.compose.ui.unit.Dp = 4.dp
) {
    val totalItemsCount = lazyListState.layoutInfo.totalItemsCount
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    var isDragging by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    
    val isListDragged by lazyListState.interactionSource.collectIsDraggedAsState()
    
    val showScrollbar by remember {
        derivedStateOf {
            isDragging || isListDragged || isVisible
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (showScrollbar && totalItemsCount > 0) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "scrollbar_alpha"
    )
    
    LaunchedEffect(isListDragged, isDragging) {
        if (isListDragged || isDragging) {
            isVisible = true
        }
        if (!isListDragged && !isDragging) {
            delay(1000)
            isVisible = false
        }
    }
    
    val firstVisibleItemIndex by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex }
    }
    
    val scrollProgress by remember(totalItemsCount, firstVisibleItemIndex) {
        derivedStateOf {
            if (totalItemsCount == 0) 0f
            else firstVisibleItemIndex.toFloat() / totalItemsCount.coerceAtLeast(1)
        }
    }
    
    val viewportHeight = lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
    
    val thumbHeightPx = remember(viewportHeight) {
        (viewportHeight.toFloat() * 0.15f).coerceIn(48f, 80f)
    }
    
    val thumbHeightDp = with(density) { thumbHeightPx.toDp() }
    
    val maxThumbOffset = (viewportHeight - thumbHeightPx).coerceAtLeast(0f)
    val thumbOffsetPx = (scrollProgress * maxThumbOffset).coerceIn(0f, maxThumbOffset)
    val thumbOffsetDp = with(density) { thumbOffsetPx.toDp() }
    
    val draggableState = rememberDraggableState { delta ->
        if (totalItemsCount > 0 && viewportHeight > 0) {
            val scrollRatio = totalItemsCount.toFloat() / viewportHeight
            val scrollAmount = (delta * scrollRatio).toInt()
            coroutineScope.launch {
                lazyListState.scrollToItem(
                    index = (firstVisibleItemIndex + scrollAmount).coerceIn(0, totalItemsCount - 1)
                )
            }
        }
    }
    
    if (alpha > 0 && viewportHeight > 0) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(thumbWidth + DesignTokens.Spacing.sm)
                .padding(horizontal = DesignTokens.Spacing.xs),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .alpha(alpha)
                    .width(thumbWidth)
                    .height(thumbHeightDp)
                    .offset(y = thumbOffsetDp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(thumbColor)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = draggableState,
                        onDragStarted = { isDragging = true },
                        onDragStopped = { isDragging = false }
                    )
            )
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Light - Large")
@Preview(showBackground = true, name = "Dark - Large", uiMode = 32)
@Composable
private fun IOSLargeTitleTopAppBarPreview() {
    SonicTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            IOSLargeTitleTopAppBar(
                title = "音乐",
                scrollState = rememberLazyListState(),
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

@Composable
private fun rememberLazyListState(): LazyListState {
    return remember { LazyListState() }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSSectionHeaderPreview() {
    SonicTheme {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
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
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xl)
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
                .padding(DesignTokens.Spacing.md)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
        ) {
            IOSCardContainer {
                Text(
                    text = "卡片标题",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
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
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
            Text(
                text = "扫描完成",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.lg))
            IOSCardContainer(modifier = Modifier.fillMaxWidth(0.85f)) {
                IOSResultRow(label = "歌曲", value = "128")
                IOSResultRow(label = "专辑", value = "32")
                IOSResultRow(label = "歌手", value = "16")
                IOSResultRow(label = "耗时", value = "1234ms", isLast = true)
            }
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xl))
            IOSTextButton(text = "重新扫描", onClick = {})
        }
    }
}
