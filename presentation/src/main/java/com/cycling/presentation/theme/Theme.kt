package com.cycling.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.cycling.domain.model.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = LightColors.Primary,
    onPrimary = LightColors.OnPrimary,
    primaryContainer = LightColors.PrimaryContainer,
    onPrimaryContainer = LightColors.OnPrimaryContainer,
    secondary = LightColors.Secondary,
    onSecondary = LightColors.OnSecondary,
    error = LightColors.Error,
    onError = LightColors.OnError,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    surfaceVariant = LightColors.SurfaceVariant,
    onSurfaceVariant = LightColors.OnSurfaceVariant,
    outline = LightColors.Outline,
    outlineVariant = LightColors.OutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.Primary,
    onPrimary = DarkColors.OnPrimary,
    primaryContainer = DarkColors.PrimaryContainer,
    onPrimaryContainer = DarkColors.OnPrimaryContainer,
    secondary = DarkColors.Secondary,
    onSecondary = DarkColors.OnSecondary,
    error = DarkColors.Error,
    onError = DarkColors.OnError,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    surfaceVariant = DarkColors.SurfaceVariant,
    onSurfaceVariant = DarkColors.OnSurfaceVariant,
    outline = DarkColors.Outline,
    outlineVariant = DarkColors.OutlineVariant
)

private val AnimationSpec: TweenSpec<Color> = tween(
    durationMillis = 400,
    easing = FastOutSlowInEasing
)

@Composable
private fun animateColor(target: Color): Color {
    return animateColorAsState(
        targetValue = target,
        animationSpec = AnimationSpec,
        label = "color_animation"
    ).value
}

@Composable
fun SonicTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDarkTheme
    }

    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val animatedColorScheme = baseColorScheme.copy(
        primary = animateColor(baseColorScheme.primary),
        onPrimary = animateColor(baseColorScheme.onPrimary),
        primaryContainer = animateColor(baseColorScheme.primaryContainer),
        onPrimaryContainer = animateColor(baseColorScheme.onPrimaryContainer),
        secondary = animateColor(baseColorScheme.secondary),
        onSecondary = animateColor(baseColorScheme.onSecondary),
        error = animateColor(baseColorScheme.error),
        onError = animateColor(baseColorScheme.onError),
        background = animateColor(baseColorScheme.background),
        onBackground = animateColor(baseColorScheme.onBackground),
        surface = animateColor(baseColorScheme.surface),
        onSurface = animateColor(baseColorScheme.onSurface),
        surfaceVariant = animateColor(baseColorScheme.surfaceVariant),
        onSurfaceVariant = animateColor(baseColorScheme.onSurfaceVariant),
        outline = animateColor(baseColorScheme.outline),
        outlineVariant = animateColor(baseColorScheme.outlineVariant)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = animatedColorScheme,
        typography = SonicTypography,
        content = content
    )
}

object SonicColorScheme {
    val success: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Success else LightColors.Success
        )

    val warning: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Warning else LightColors.Warning
        )

    val favorite: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Favorite else LightColors.Favorite
        )

    val secondaryBackground: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.SecondaryBackground else LightColors.SecondaryBackground
        )

    val tertiaryBackground: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.TertiaryBackground else LightColors.TertiaryBackground
        )

    val separator: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Separator else LightColors.Separator
        )

    val label: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Label else LightColors.Label
        )

    val secondaryLabel: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.SecondaryLabel else LightColors.SecondaryLabel
        )

    val tertiaryLabel: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.TertiaryLabel else LightColors.TertiaryLabel
        )

    val quaternaryLabel: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.QuaternaryLabel else LightColors.QuaternaryLabel
        )

    val fill: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.Fill else LightColors.Fill
        )

    val secondaryFill: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.SecondaryFill else LightColors.SecondaryFill
        )

    val tertiaryFill: Color
        @Composable get() = animateColor(
            if (isSystemInDarkTheme()) DarkColors.TertiaryFill else LightColors.TertiaryFill
        )
}
