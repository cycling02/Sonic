package com.cycling.presentation.theme

import androidx.compose.ui.graphics.Color

object IOSystemColors {
    object Light {
        val Blue = Color(0xFF007AFF)
        val Green = Color(0xFF34C759)
        val Red = Color(0xFFFF3B30)
        val Orange = Color(0xFFFF9500)
        val Yellow = Color(0xFFFFCC00)
        val Purple = Color(0xFFAF52DE)
        val Pink = Color(0xFFFF2D55)
        val Teal = Color(0xFF5AC8FA)
        val Indigo = Color(0xFF5856D6)
    }

    object Dark {
        val Blue = Color(0xFF0A84FF)
        val Green = Color(0xFF30D158)
        val Red = Color(0xFFFF453A)
        val Orange = Color(0xFFFF9F0A)
        val Yellow = Color(0xFFFFD60A)
        val Purple = Color(0xFFBF5AF2)
        val Pink = Color(0xFFFF375F)
        val Teal = Color(0xFF64D2FF)
        val Indigo = Color(0xFF5E5CE6)
    }
}

object SonicColors {
    val Blue = IOSystemColors.Light.Blue
    val Green = IOSystemColors.Light.Green
    val Red = IOSystemColors.Light.Red
    val Orange = IOSystemColors.Light.Orange
    val Yellow = IOSystemColors.Light.Yellow
    val Purple = IOSystemColors.Light.Purple
    val Pink = IOSystemColors.Light.Pink
    val Teal = IOSystemColors.Light.Teal
    val Indigo = IOSystemColors.Light.Indigo
}

object LightColors {
    val Background = Color(0xFFF2F2F7)
    val SecondaryBackground = Color(0xFFFFFFFF)
    val TertiaryBackground = Color(0xFFF2F2F7)
    val Separator = Color(0xFFC6C6C8)
    val Label = Color(0xFF000000)
    val SecondaryLabel = Color(0x993C3C43)
    val TertiaryLabel = Color(0x4D3C3C43)
    val QuaternaryLabel = Color(0x2E3C3C43)
    val Fill = Color(0x33787880)
    val SecondaryFill = Color(0x29787880)
    val TertiaryFill = Color(0x1F767680)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF2F2F7)
    val OnBackground = Color.Black
    val OnSurface = Color.Black
    val OnSurfaceVariant = Color(0xFF8E8E93)
    val Outline = Color(0xFFC6C6C8)
    val OutlineVariant = Color(0xFFE5E5EA)
    val Primary = IOSystemColors.Light.Blue
    val OnPrimary = Color.White
    val PrimaryContainer = Color(0xFFD1E8FF)
    val OnPrimaryContainer = Color(0xFF001D36)
    val Secondary = IOSystemColors.Light.Purple
    val OnSecondary = Color.White
    val Success = IOSystemColors.Light.Green
    val Warning = IOSystemColors.Light.Orange
    val Error = IOSystemColors.Light.Red
    val OnError = Color.White
    val Favorite = IOSystemColors.Light.Pink
}

object DarkColors {
    val Background = Color(0xFF000000)
    val SecondaryBackground = Color(0xFF1C1C1E)
    val TertiaryBackground = Color(0xFF2C2C2E)
    val Separator = Color(0xFF38383A)
    val Label = Color(0xFFFFFFFF)
    val SecondaryLabel = Color(0x99EBEBF5)
    val TertiaryLabel = Color(0x4DEBEBF5)
    val QuaternaryLabel = Color(0x2EEBEBF5)
    val Fill = Color(0x5C787880)
    val SecondaryFill = Color(0x52787880)
    val TertiaryFill = Color(0x3D767680)
    val Surface = Color(0xFF1C1C1E)
    val SurfaceVariant = Color(0xFF2C2C2E)
    val OnBackground = Color.White
    val OnSurface = Color.White
    val OnSurfaceVariant = Color(0xFF8E8E93)
    val Outline = Color(0xFF38383A)
    val OutlineVariant = Color(0xFF3A3A3C)
    val Primary = IOSystemColors.Dark.Blue
    val OnPrimary = Color.White
    val PrimaryContainer = Color(0xFF003E6C)
    val OnPrimaryContainer = Color(0xFFD1E8FF)
    val Secondary = IOSystemColors.Dark.Indigo
    val OnSecondary = Color.White
    val Success = IOSystemColors.Dark.Green
    val Warning = IOSystemColors.Dark.Orange
    val Error = IOSystemColors.Dark.Red
    val OnError = Color.White
    val Favorite = IOSystemColors.Dark.Pink
}
