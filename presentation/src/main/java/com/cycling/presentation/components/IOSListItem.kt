package com.cycling.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicColors
import com.cycling.presentation.theme.SonicTheme

@Composable
fun IOSListItem(
    title: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) else Color.Transparent,
        animationSpec = tween(durationMillis = DesignTokens.Animation.animationDurationShort),
        label = "backgroundColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(DesignTokens.ListItem.iconSize)
                    .clip(RoundedCornerShape(DesignTokens.ListItem.iconCornerSize))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            trailing?.invoke() ?: Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DesignTokens.ListItem.dividerIndent)
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
fun IOSListItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f) else Color.Transparent,
        animationSpec = tween(durationMillis = DesignTokens.Animation.animationDurationShort),
        label = "backgroundColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leading?.invoke()
            if (leading != null) Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            trailing?.invoke()
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = if (leading != null) DesignTokens.ListItem.dividerIndent else 16.dp)
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
fun IOSListSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title.toUpperCase(Locale.current),
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun IOSInsetGrouped(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSListItemWithIconPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IOSInsetGrouped {
                IOSListItem(
                    title = "歌曲",
                    icon = Icons.Default.MusicNote,
                    iconBackgroundColor = SonicColors.Red,
                    onClick = {},
                    showDivider = true
                )
                IOSListItem(
                    title = "专辑",
                    icon = Icons.Default.Album,
                    iconBackgroundColor = SonicColors.Teal,
                    onClick = {},
                    showDivider = true
                )
                IOSListItem(
                    title = "歌手",
                    icon = Icons.Default.Person,
                    iconBackgroundColor = SonicColors.Blue,
                    onClick = {},
                    showDivider = false
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSListItemWithSubtitlePreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IOSInsetGrouped {
                IOSListItem(
                    title = "最近播放",
                    icon = Icons.Default.MusicNote,
                    iconBackgroundColor = SonicColors.Purple,
                    subtitle = "128 首歌曲",
                    onClick = {},
                    showDivider = true
                )
                IOSListItem(
                    title = "收藏",
                    icon = Icons.Default.Album,
                    iconBackgroundColor = SonicColors.Orange,
                    subtitle = "32 首歌曲",
                    onClick = {},
                    showDivider = false
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSListItemWithSwitchPreview() {
    var checked by remember { mutableStateOf(true) }

    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IOSInsetGrouped {
                IOSListItem(
                    title = "深色模式",
                    onClick = { checked = !checked },
                    showDivider = true,
                    leading = {
                        Box(
                            modifier = Modifier
                                .size(DesignTokens.ListItem.iconSize)
                                .clip(RoundedCornerShape(DesignTokens.ListItem.iconCornerSize))
                                .background(SonicColors.Indigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    trailing = {
                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SonicColors.Green
                            )
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
private fun IOSListSectionHeaderPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IOSListSectionHeader(title = "媒体库")
            IOSInsetGrouped {
                IOSListItem(
                    title = "歌曲",
                    icon = Icons.Default.MusicNote,
                    iconBackgroundColor = SonicColors.Red,
                    onClick = {},
                    showDivider = true
                )
                IOSListItem(
                    title = "专辑",
                    icon = Icons.Default.Album,
                    iconBackgroundColor = SonicColors.Teal,
                    onClick = {},
                    showDivider = false
                )
            }
        }
    }
}
