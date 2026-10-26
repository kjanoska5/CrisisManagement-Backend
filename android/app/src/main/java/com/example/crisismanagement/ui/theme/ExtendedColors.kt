package com.example.crisismanagement.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val textMuted: Color,
    val surfaceHigh: Color,
    val danger: Color,
    val warning: Color,
    val success: Color
)

val DarkExtendedColors = ExtendedColors(
    textMuted = TextMutedDark,
    surfaceHigh = SurfaceHighDark,
    danger = DangerDark,
    warning = WarningDark,
    success = SuccessDark
)

val LightExtendedColors = ExtendedColors(
    textMuted = TextMutedLight,
    surfaceHigh = SurfaceHighLight,
    danger = DangerLight,
    warning = WarningLight,
    success = SuccessLight
)

val LocalExtendedColors = staticCompositionLocalOf { DarkExtendedColors }
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current