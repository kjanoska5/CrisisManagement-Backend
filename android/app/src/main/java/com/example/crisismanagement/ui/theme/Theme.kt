package com.example.crisismanagement.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = AcidMint,
    onPrimary = ForestGraphite,
    primaryContainer = SurfaceHighDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AcidMint,
    onSecondary = ForestGraphite,
    background = ForestGraphite,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceHighDark,
    onSurfaceVariant = TextMutedDark,
    surfaceContainerLowest = ForestGraphite,
    surfaceContainerLow = SurfaceDark,
    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceHighDark,
    surfaceContainerHighest = SurfaceHighDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
    error = DangerDark,
    onError = ForestGraphite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = SurfaceLight,
    primaryContainer = SurfaceHighLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = PrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceHighLight,
    onSurfaceVariant = TextMutedLight,
    surfaceContainerLowest = BackgroundLight,
    surfaceContainerLow = SurfaceLight,
    surfaceContainer = SurfaceLight,
    surfaceContainerHigh = SurfaceHighLight,
    surfaceContainerHighest = SurfaceHighLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = DangerLight,
    onError = SurfaceLight
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CrisisManagementTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AppShapes,
            content = content
        )
    }
}