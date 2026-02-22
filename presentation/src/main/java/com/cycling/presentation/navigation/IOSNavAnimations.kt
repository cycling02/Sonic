package com.cycling.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import com.cycling.presentation.theme.DesignTokens

object IOSNavAnimations {

    private inline fun <reified T> springSpec() = spring<T>(
        dampingRatio = DesignTokens.Animation.springDampingRatio,
        stiffness = DesignTokens.Animation.springStiffness
    )

    fun iosPushEnter(): EnterTransition = slideInHorizontally(
        animationSpec = springSpec(),
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn(
        animationSpec = springSpec(),
        initialAlpha = 0.3f
    )

    fun iosPushExit(): ExitTransition = slideOutHorizontally(
        animationSpec = springSpec(),
        targetOffsetX = { fullWidth -> -fullWidth / 3 }
    ) + fadeOut(
        animationSpec = springSpec(),
        targetAlpha = 0.8f
    )

    fun iosPopEnter(): EnterTransition = slideInHorizontally(
        animationSpec = springSpec(),
        initialOffsetX = { fullWidth -> -fullWidth / 3 }
    ) + fadeIn(
        animationSpec = springSpec(),
        initialAlpha = 0.8f
    )

    fun iosPopExit(): ExitTransition = slideOutHorizontally(
        animationSpec = springSpec(),
        targetOffsetX = { fullWidth -> fullWidth }
    ) + fadeOut(
        animationSpec = springSpec(),
        targetAlpha = 0.3f
    )

    fun iosModalEnter(): EnterTransition = slideInVertically(
        animationSpec = springSpec(),
        initialOffsetY = { fullHeight -> fullHeight }
    ) + fadeIn(
        animationSpec = springSpec(),
        initialAlpha = 0.5f
    )

    fun iosModalExit(): ExitTransition = slideOutVertically(
        animationSpec = springSpec(),
        targetOffsetY = { fullHeight -> fullHeight }
    ) + fadeOut(
        animationSpec = springSpec(),
        targetAlpha = 0.5f
    )

    fun iosSheetEnter(): EnterTransition = slideInVertically(
        animationSpec = springSpec(),
        initialOffsetY = { fullHeight -> fullHeight }
    ) + fadeIn(
        animationSpec = springSpec(),
        initialAlpha = 0.7f
    )

    fun iosSheetExit(): ExitTransition = slideOutVertically(
        animationSpec = springSpec(),
        targetOffsetY = { fullHeight -> fullHeight }
    ) + fadeOut(
        animationSpec = springSpec(),
        targetAlpha = 0.7f
    )

    fun iosFadeEnter(): EnterTransition = fadeIn(
        animationSpec = springSpec(),
        initialAlpha = 0.0f
    )

    fun iosFadeExit(): ExitTransition = fadeOut(
        animationSpec = springSpec(),
        targetAlpha = 0.0f
    )
}
