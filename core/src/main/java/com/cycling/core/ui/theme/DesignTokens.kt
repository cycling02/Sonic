package com.cycling.core.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

object M3Spacing {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val extraExtraLarge = 48.dp
    val extraExtraExtraLarge = 64.dp
}

object M3Shapes {
    val cornerSmall = RoundedCornerShape(8.dp)
    val cornerMedium = RoundedCornerShape(12.dp)
    val cornerLarge = RoundedCornerShape(16.dp)
    val cornerExtraLarge = RoundedCornerShape(28.dp)
    val cornerFull = RoundedCornerShape(50)
}

object M3Elevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
    val level4 = 8.dp
    val level5 = 12.dp
}

object M3ComponentSize {
    val buttonMinHeight = 40.dp
    val buttonIconSize = 18.dp
    val fabStandardSize = 56.dp
    val fabSmallSize = 48.dp
    val fabLargeSize = 96.dp
    val fabIconSize = 24.dp
    
    val listItemOneLineHeight = 56.dp
    val listItemTwoLineHeight = 72.dp
    val listItemThreeLineHeight = 88.dp
    val listItemIconSize = 24.dp
    val listItemAvatarSize = 40.dp
    
    val topAppBarHeight = 64.dp
    val largeTopAppBarHeight = 152.dp
    val navigationBarHeight = 80.dp
    val navigationBarItemIconSize = 24.dp
    
    val miniPlayerHeight = 64.dp
    val miniPlayerArtworkSize = 48.dp
    val miniPlayerButtonSize = 40.dp
    
    val mediaCardWidth = 160.dp
    val mediaCardAspectRatio = 1f
    val artistCardSize = 100.dp
}

object M3Motion {
    val defaultSpringSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val expressiveSpringSpec: AnimationSpec<Float> = spring(
        dampingRatio = 0.8f,
        stiffness = 400f
    )
    
    val bouncySpringSpec: AnimationSpec<Float> = spring(
        dampingRatio = 0.6f,
        stiffness = 200f
    )
    
    val buttonPressScale = 0.95f
    val cardHoverScale = 1.02f
    
    const val durationShort = 150
    const val durationMedium = 300
    const val durationLong = 500
}

object M3Alpha {
    const val disabled = 0.38f
    const val mediumEmphasis = 0.6f
    const val highEmphasis = 0.87f
    const val pressed = 0.12f
    const val focus = 0.12f
    const val hover = 0.08f
    const val drag = 0.16f
}

object M3StateLayers {
    const val hover = 0.08f
    const val focus = 0.12f
    const val pressed = 0.12f
    const val drag = 0.16f
}

object M3Easing {
    const val standard = "cubic-bezier(0.2, 0.0, 0.0, 1.0)"
    const val emphasized = "cubic-bezier(0.2, 0.0, 0.0, 1.0)"
    const val emphasizedDecelerate = "cubic-bezier(0.05, 0.7, 0.1, 1.0)"
    const val emphasizedAccelerate = "cubic-bezier(0.3, 0.0, 0.8, 0.15)"
    const val standardDecelerate = "cubic-bezier(0.0, 0.0, 0.0, 1.0)"
    const val standardAccelerate = "cubic-bezier(0.3, 0.0, 1.0, 1.0)"
}

object M3Duration {
    const val short = 150
    const val medium = 300
    const val long = 500
    const val extraLong = 700
}

object M3ExpressiveShapes {
    val cornerExtraSmall = RoundedCornerShape(4.dp)
    val cornerSmall = RoundedCornerShape(8.dp)
    val cornerMedium = RoundedCornerShape(12.dp)
    val cornerLarge = RoundedCornerShape(16.dp)
    val cornerExtraLarge = RoundedCornerShape(28.dp)
    val cornerFull = RoundedCornerShape(50)

    val circle = CircleShape
    val square = RectangleShape
    val pill = RoundedCornerShape(50)

    val topRounded = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val bottomRounded = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    val leftRounded = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
    val rightRounded = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)

    val asymmetricTopLeft = RoundedCornerShape(topStart = 28.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 28.dp)
    val asymmetricTopRight = RoundedCornerShape(topStart = 8.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 8.dp)

    val softSquare = RoundedCornerShape(4.dp)
    val verySoftSquare = RoundedCornerShape(6.dp)

    val cornerXLarge = RoundedCornerShape(24.dp)
    val cornerXXLarge = RoundedCornerShape(32.dp)
    val cornerXXXLarge = RoundedCornerShape(48.dp)

    val cutCornerSmall = CutCornerShape(8.dp)
    val cutCornerMedium = CutCornerShape(12.dp)
    val cutCornerLarge = CutCornerShape(16.dp)

    val mixedTopLeftBottomRight = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp)
    val mixedTopRightBottomLeft = RoundedCornerShape(topEnd = 24.dp, bottomStart = 24.dp)

    val buttonShape = RoundedCornerShape(20.dp)
    val fabShape = RoundedCornerShape(16.dp)
    val chipShape = RoundedCornerShape(8.dp)

    val cardSmall = RoundedCornerShape(12.dp)
    val cardMedium = RoundedCornerShape(16.dp)
    val cardLarge = RoundedCornerShape(24.dp)

    val dialogShape = RoundedCornerShape(28.dp)

    val bottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
}

object M3SpringTokens {
    fun standardSpatial() = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 200f
    )

    fun expressiveSpatial() = spring<Float>(
        dampingRatio = 0.6f,
        stiffness = 200f
    )

    fun standardMorph() = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 400f
    )

    fun expressiveMorph() = spring<Float>(
        dampingRatio = 0.6f,
        stiffness = 300f
    )

    fun fast() = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 600f
    )

    fun default() = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 400f
    )

    fun slow() = spring<Float>(
        dampingRatio = 0.9f,
        stiffness = 150f
    )

    fun buttonPress() = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 400f
    )

    fun cardHover() = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 300f
    )

    fun fabExpand() = spring<Float>(
        dampingRatio = 0.6f,
        stiffness = 250f
    )

    fun listItem() = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 350f
    )
}

object M3ExpressiveComponentSize {
    val buttonSmallHeight = 32.dp
    val buttonMediumHeight = 40.dp
    val buttonLargeHeight = 48.dp
    val buttonExtraLargeHeight = 56.dp

    val fabSmallSize = 40.dp
    val fabStandardSize = 56.dp
    val fabLargeSize = 96.dp
    val fabExtendedHeight = 48.dp

    val cardSmallWidth = 120.dp
    val cardMediumWidth = 160.dp
    val cardLargeWidth = 200.dp

    val avatarSmallSize = 32.dp
    val avatarMediumSize = 48.dp
    val avatarLargeSize = 64.dp
    val avatarExtraLargeSize = 100.dp

    val iconSmallSize = 16.dp
    val iconMediumSize = 24.dp
    val iconLargeSize = 32.dp
    val iconExtraLargeSize = 48.dp

    val toolbarHeight = 56.dp

    val carouselItemWidth = 280.dp
    val carouselItemSpacing = 16.dp
}
