package com.cycling.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cycling.presentation.theme.DesignTokens
import com.cycling.presentation.theme.SonicTheme

@Composable
fun IOSMediaCard(
    title: String,
    subtitle: String,
    artwork: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = DesignTokens.Card.mediaCardWidth,
    placeholderIcon: ImageVector = Icons.Outlined.MusicNote
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) DesignTokens.Animation.buttonPressScale else 1f,
        animationSpec = spring(
            dampingRatio = DesignTokens.Animation.springDampingRatio,
            stiffness = DesignTokens.Animation.springStiffness
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .width(width)
            .scale(scale)
            .clip(RoundedCornerShape(DesignTokens.CornerRadius.medium))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(DesignTokens.Card.mediaCardAspectRatio)
                .clip(RoundedCornerShape(DesignTokens.CornerRadius.medium))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (artwork != null) {
                AsyncImage(
                    model = artwork,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = placeholderIcon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun IOSArtistCard(
    name: String,
    artwork: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = DesignTokens.Card.artistCardSize
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) DesignTokens.Animation.buttonPressScale else 1f,
        animationSpec = spring(
            dampingRatio = DesignTokens.Animation.springDampingRatio,
            stiffness = DesignTokens.Animation.springStiffness
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .width(size)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFf093fb),
                            Color(0xFFf5576c)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (artwork != null) {
                AsyncImage(
                    model = artwork,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun IOSPlaylistCard(
    title: String,
    subtitle: String,
    artwork: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = DesignTokens.Card.mediaCardWidth
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) DesignTokens.Animation.buttonPressScale else 1f,
        animationSpec = spring(
            dampingRatio = DesignTokens.Animation.springDampingRatio,
            stiffness = DesignTokens.Animation.springStiffness
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .width(width)
            .scale(scale)
            .clip(RoundedCornerShape(DesignTokens.CornerRadius.medium))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(DesignTokens.Card.mediaCardAspectRatio)
                .clip(RoundedCornerShape(DesignTokens.CornerRadius.medium))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (artwork != null) {
                AsyncImage(
                    model = artwork,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSMediaCardPreview() {
    SonicTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IOSMediaCard(title = "歌曲标题", subtitle = "歌手名称", artwork = null, onClick = {})
            IOSMediaCard(title = "这是一首很长的歌曲名称", subtitle = "这是一位很长的歌手名称", artwork = null, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSArtistCardPreview() {
    SonicTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IOSArtistCard(name = "周杰伦", artwork = null, onClick = {})
            IOSArtistCard(name = "林俊杰", artwork = null, onClick = {})
            IOSArtistCard(name = "邓紫棋", artwork = null, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Light")
@Preview(showBackground = true, name = "Dark", uiMode = 32)
@Composable
private fun IOSPlaylistCardPreview() {
    SonicTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IOSPlaylistCard(title = "我喜欢的音乐", subtitle = "100首歌曲", artwork = null, onClick = {})
            IOSPlaylistCard(title = "这是一个很长的播放列表名称", subtitle = "这是一个很长的描述", artwork = null, onClick = {})
        }
    }
}

@Preview(showBackground = true, widthDp = 400, name = "Light")
@Preview(showBackground = true, widthDp = 400, name = "Dark", uiMode = 32)
@Composable
private fun IOSCardsCombinedPreview() {
    SonicTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "最近播放",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IOSMediaCard(title = "晴天", subtitle = "周杰伦", artwork = null, onClick = {})
                IOSMediaCard(title = "江南", subtitle = "林俊杰", artwork = null, onClick = {})
            }

            Text(
                text = "热门歌手",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IOSArtistCard(name = "周杰伦", artwork = null, onClick = {})
                IOSArtistCard(name = "林俊杰", artwork = null, onClick = {})
                IOSArtistCard(name = "邓紫棋", artwork = null, onClick = {})
            }

            Text(
                text = "我的歌单",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IOSPlaylistCard(title = "我喜欢的音乐", subtitle = "100首歌曲", artwork = null, onClick = {})
                IOSPlaylistCard(title = "运动歌单", subtitle = "50首歌曲", artwork = null, onClick = {})
            }
        }
    }
}
