package com.cycling.presentation.theme

import androidx.compose.animation.core.Spring
import androidx.compose.ui.unit.dp

object DesignTokens {
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
        val sectionSpacing = 35.dp
    }

    object CornerRadius {
        val small = 8.dp
        val medium = 12.dp
        val large = 16.dp
        val xlarge = 20.dp
        val searchBar = 10.dp
    }

    object Animation {
        val springDampingRatio = 0.85f
        val springStiffness = Spring.StiffnessMediumLow
        val buttonPressScale = 0.96f
        val listItemPressScale = 0.98f
        const val animationDurationShort = 150
        const val animationDurationMedium = 300
        const val animationDurationLong = 500
    }

    object ListItem {
        val height = 44.dp
        val heightDouble = 64.dp
        val iconSize = 32.dp
        val iconCornerSize = 8.dp
        val dividerIndent = 62.dp
    }

    object Card {
        val mediaCardWidth = 160.dp
        val mediaCardAspectRatio = 1f
        val artistCardSize = 110.dp
    }

    object Player {
        val miniPlayerHeight = 64.dp
        val miniPlayerProgressHeight = 2.dp
        val miniPlayerArtworkSize = 48.dp
        val playerArtworkWidthPercent = 0.85f
        val playerPlayButtonSize = 72.dp
    }

    object TabBar {
        val height = 49.dp
        val iconSize = 24.dp
    }

    object NavBar {
        val height = 44.dp
        val largeHeight = 96.dp
    }
}
