package com.cycling.presentation.songdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cycling.domain.model.AudioMetadata
import com.cycling.domain.model.Song
import com.cycling.presentation.components.IOSFilledButton
import com.cycling.presentation.components.formatDuration
import com.cycling.presentation.theme.SonicColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToApiKeyConfig: () -> Unit = {},
    viewModel: SongDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SongDetailEffect.NavigateToPlayer -> onNavigateToPlayer(effect.songId)
                is SongDetailEffect.ShowAddToPlaylistDialog -> {}
                is SongDetailEffect.ShowCopiedMessage -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Song Path", effect.message)
                    clipboard.setPrimaryClip(clip)
                }
                is SongDetailEffect.ShowError -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "歌曲详情",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            uiState.song?.let { song ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    SongHeader(song = song)

                    Spacer(modifier = Modifier.height(24.dp))

                    ActionButtons(
                        isFavorite = uiState.isFavorite,
                        onPlay = { viewModel.handleIntent(SongDetailIntent.PlaySong) },
                        onFavorite = { viewModel.handleIntent(SongDetailIntent.ToggleFavorite) },
                        onAddToPlaylist = { viewModel.handleIntent(SongDetailIntent.AddToPlaylist) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SongInfoSection(song = song, audioMetadata = uiState.audioMetadata)
                }
            }
        }
    }
}

@Composable
private fun SongHeader(song: Song) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (song.albumArt != null) {
                AsyncImage(
                    model = song.albumArt,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = SonicColors.Red
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = song.artist,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = song.album,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionButtons(
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IOSFilledButton(
            text = "播放",
            onClick = onPlay,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onFavorite,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isFavorite) SonicColors.Red.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                tint = if (isFavorite) SonicColors.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onAddToPlaylist,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = "添加到播放列表",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SongInfoSection(song: Song, audioMetadata: AudioMetadata?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "基本信息",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoItem(label = "时长", value = formatDuration(song.duration))
        InfoItem(label = "格式", value = getFormatFromMimeType(song.mimeType))
        InfoItem(label = "文件大小", value = formatFileSize(song.size))

        audioMetadata?.let { metadata ->
            if (metadata.year != null) {
                InfoItem(label = "年份", value = metadata.year!!)
            }
            if (metadata.genre != null) {
                InfoItem(label = "流派", value = metadata.genre!!)
            }
            if (metadata.composer != null) {
                InfoItem(label = "作曲", value = metadata.composer!!)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "音频信息",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val bitrate = audioMetadata?.bitrate ?: song.bitrate
        InfoItem(label = "比特率", value = if (bitrate > 0) "$bitrate kbps" else "未知")
        
        audioMetadata?.let { metadata ->
            if (metadata.sampleRate > 0) {
                InfoItem(label = "采样率", value = "${metadata.sampleRate} Hz")
            }
            if (metadata.channels > 0) {
                InfoItem(label = "声道", value = if (metadata.channels == 2) "立体声" else "单声道")
            }
            if (metadata.encodingType.isNotEmpty()) {
                InfoItem(label = "编码", value = metadata.encodingType)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "文件信息",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoItem(label = "添加日期", value = formatDate(song.dateAdded))
        InfoItem(label = "修改日期", value = formatDate(song.dateModified))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "文件路径",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        PathItem(path = song.path)
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}

@Composable
private fun PathItem(path: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = path,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        val context = LocalContext.current
        TextButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Song Path", path)
                clipboard.setPrimaryClip(clip)
            }
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "复制",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("复制")
        }
    }
}

private fun getFormatFromMimeType(mimeType: String): String {
    return when {
        mimeType.contains("mp3", ignoreCase = true) -> "MP3"
        mimeType.contains("mpeg", ignoreCase = true) -> "MPEG"
        mimeType.contains("mp4", ignoreCase = true) -> "MP4"
        mimeType.contains("flac", ignoreCase = true) -> "FLAC"
        mimeType.contains("wav", ignoreCase = true) -> "WAV"
        mimeType.contains("aac", ignoreCase = true) -> "AAC"
        mimeType.contains("ogg", ignoreCase = true) -> "OGG"
        mimeType.contains("wma", ignoreCase = true) -> "WMA"
        mimeType.contains("m4a", ignoreCase = true) -> "M4A"
        mimeType.contains("3gpp", ignoreCase = true) -> "3GPP"
        else -> mimeType.ifEmpty { "未知" }
    }
}

private fun formatFileSize(size: Long): String {
    return when {
        size >= 1024 * 1024 * 1024 -> String.format(Locale.getDefault(), "%.2f GB", size.toDouble() / (1024 * 1024 * 1024))
        size >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.2f MB", size.toDouble() / (1024 * 1024))
        size >= 1024 -> String.format(Locale.getDefault(), "%.2f KB", size.toDouble() / 1024)
        else -> "$size B"
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return "未知"
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
