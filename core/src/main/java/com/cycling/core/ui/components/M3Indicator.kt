package com.cycling.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cycling.core.ui.theme.M3Theme

@Composable
fun M3CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    if (progress != null) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap
        )
    }
}

@Composable
fun M3LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeCap: StrokeCap = StrokeCap.Round
) {
    if (progress != null) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeCap = strokeCap
        )
    } else {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = trackColor,
            strokeCap = strokeCap
        )
    }
}

@Composable
fun M3LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                M3CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun M3SmallCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier.size(32.dp),
        color = color,
        strokeWidth = 3.dp,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun M3LargeCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier.size(64.dp),
        color = color,
        strokeWidth = 6.dp,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun M3ExpressiveCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    size: Dp = 48.dp
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (progress != null && progress < 1f) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expressive_scale"
    )

    if (progress != null) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = modifier
                .size(size)
                .scale(animatedScale),
            color = color,
            trackColor = trackColor,
            strokeWidth = (size.value / 8).dp,
            strokeCap = StrokeCap.Round
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier
                .size(size)
                .scale(animatedScale),
            color = color,
            trackColor = trackColor,
            strokeWidth = (size.value / 8).dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun M3ExpressiveLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "expressive_progress"
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round
    )
}

@Composable
fun M3PulsingProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 48.dp
) {
    var pulse by remember { mutableStateOf(1f) }
    
    val animatedScale by animateFloatAsState(
        targetValue = pulse,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(animatedScale)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = (size.value / 10).dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun M3WaveformProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    waveformCount: Int = 5
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "waveform_progress"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(waveformCount) { index ->
            val barProgress = if (index < (animatedProgress * waveformCount).toInt()) {
                1f
            } else if (index == (animatedProgress * waveformCount).toInt()) {
                (animatedProgress * waveformCount) % 1f
            } else {
                0.3f
            }
            
            val barHeight by animateFloatAsState(
                targetValue = barProgress,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "bar_height_$index"
            )
            
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(32.dp * barHeight.coerceIn(0.3f, 1f))
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (barProgress > 0.3f) color else trackColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun M3IndicatorPreview() {
    M3Theme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            M3CircularProgressIndicator()
            M3CircularProgressIndicator(progress = 0.7f)
            M3LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            M3LinearProgressIndicator(
                progress = 0.5f,
                modifier = Modifier.fillMaxWidth()
            )
            M3SmallCircularProgressIndicator()
            M3LargeCircularProgressIndicator()
            M3ExpressiveCircularProgressIndicator(progress = 0.6f)
            M3ExpressiveLinearProgressIndicator(
                progress = 0.75f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            M3WaveformProgressIndicator(
                progress = 0.6f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
