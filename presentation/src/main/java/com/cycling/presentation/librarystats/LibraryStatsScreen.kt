package com.cycling.presentation.librarystats

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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cycling.domain.model.AudioQuality
import com.cycling.domain.model.LibraryStats
import com.cycling.presentation.components.IOSTopAppBar
import com.cycling.presentation.components.PieChart
import com.cycling.presentation.components.PieChartData

private val HRColor = Color(0xFFFFD700)
private val SQColor = Color(0xFF9C27B0)
private val HQColor = Color(0xFF2196F3)
private val OthersColor = Color(0xFF9E9E9E)

private data class QualityStatItemUi(
    val quality: AudioQuality,
    val count: Int,
    val percentage: Float,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryStatsScreen(
    viewModel: LibraryStatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is LibraryStatsEffect.ShowToast -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            IOSTopAppBar(
                title = "音乐库统计",
                onNavigateBack = onNavigateBack
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
        } else if (uiState.stats != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.stats!!.totalSongs > 0) {
                    ChartSection(stats = uiState.stats!!)
                    Spacer(modifier = Modifier.height(24.dp))
                    StatsDetailSection(stats = uiState.stats!!)
                } else {
                    EmptyState()
                }
            }
        } else {
            EmptyState()
        }
    }
}

@Composable
private fun ChartSection(stats: LibraryStats) {
    var selectedIndex by remember { mutableStateOf(-1) }
    
    val pieData = listOf(
        PieChartData(stats.hrCount.toFloat(), HRColor, "HR"),
        PieChartData(stats.sqCount.toFloat(), SQColor, "SQ"),
        PieChartData(stats.hqCount.toFloat(), HQColor, "HQ"),
        PieChartData(stats.othersCount.toFloat(), OthersColor, "Others")
    ).filter { it.value > 0 }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(24.dp)
    ) {
        Text(
            text = "音频质量分布",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        PieChart(
            data = pieData,
            selectedIndex = selectedIndex,
            onSliceClick = { selectedIndex = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "共 ${stats.totalSongs} 首歌曲",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (selectedIndex >= 0 && selectedIndex < pieData.size) {
            Spacer(modifier = Modifier.height(8.dp))
            val selected = pieData[selectedIndex]
            val percentage = (selected.value / stats.totalSongs.toFloat() * 100).toInt()
            Text(
                text = "${selected.label}: ${percentage}%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = selected.color
            )
        }
    }
}

@Composable
private fun StatsDetailSection(stats: LibraryStats) {
    val items = listOf(
        QualityStatItemUi(AudioQuality.HR, stats.hrCount, stats.hrPercentage, HRColor),
        QualityStatItemUi(AudioQuality.SQ, stats.sqCount, stats.sqPercentage, SQColor),
        QualityStatItemUi(AudioQuality.HQ, stats.hqCount, stats.hqPercentage, HQColor),
        QualityStatItemUi(AudioQuality.OTHERS, stats.othersCount, stats.othersPercentage, OthersColor)
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "统计明细",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        items.forEach { item ->
            StatItemRow(item = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatItemRow(item: QualityStatItemUi) {
    val (icon, title, description) = when (item.quality) {
        AudioQuality.HR -> Triple(
            Icons.Default.GraphicEq,
            "HR (Hi-Res)",
            "高解析度音频 > 1411 kbps"
        )
        AudioQuality.SQ -> Triple(
            Icons.Default.HighQuality,
            "SQ (Studio Quality)",
            "CD质量/无损 = 1411 kbps"
        )
        AudioQuality.HQ -> Triple(
            Icons.Default.BarChart,
            "HQ (High Quality)",
            "高质量 320-1410 kbps"
        )
        AudioQuality.OTHERS -> Triple(
            Icons.Default.LibraryMusic,
            "Others",
            "其他 < 320 kbps 或未知"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${item.count} 首",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = item.color
            )
            Text(
                text = "%.1f%%".format(item.percentage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "暂无音乐",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "请先扫描本地音乐",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
