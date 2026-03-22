package com.evodart.glyphdial.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Theme mode options
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    AMOLED,
    SYSTEM
}

/**
 * Local composition for accessing Nothing-specific design tokens
 */
data class NothingDesignTokens(
    val spacing: NothingSpacing = NothingSpacing,
    val sizes: NothingSizes = NothingSizes,
    val grid: NothingGrid = NothingGrid,
    val elevation: NothingElevation = NothingElevation,
    val motion: NothingMotion = NothingMotion,
    val textStyles: NothingTextStyles = NothingTextStyles,
    val componentShapes: NothingComponentShapes = NothingComponentShapes
)

val LocalNothingTokens = staticCompositionLocalOf { NothingDesignTokens() }

/**
 * Access Nothing design tokens from any composable
 */
object NothingTheme {
    val tokens: NothingDesignTokens
        @Composable
        get() = LocalNothingTokens.current
    
    val spacing: NothingSpacing
        @Composable
        get() = LocalNothingTokens.current.spacing
    
    val sizes: NothingSizes
        @Composable
        get() = LocalNothingTokens.current.sizes
    
    val grid: NothingGrid
        @Composable
        get() = LocalNothingTokens.current.grid
    
    val motion: NothingMotion
        @Composable
        get() = LocalNothingTokens.current.motion
    
    val textStyles: NothingTextStyles
        @Composable
        get() = LocalNothingTokens.current.textStyles
    
    val shapes: NothingComponentShapes
        @Composable
        get() = LocalNothingTokens.current.componentShapes
}

/**
 * Main theme composable for Glyph Dial app
 * 
 * @param themeMode The theme mode to use
 * @param dynamicColor Whether to use dynamic color (Material You) - disabled by default for Nothing style
 * @param content The composable content
 */
@Composable
fun GlyphDialTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    dynamicColor: Boolean = false, // Disabled by default for consistent Nothing branding
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.SYSTEM -> if (systemInDarkTheme) DarkColorScheme else LightColorScheme
    }
    
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.SYSTEM -> systemInDarkTheme
    }
    
    // Configure system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Make status bar transparent
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Set system bar icons color
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkTheme
                isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }
    
    // Provide Nothing design tokens
    CompositionLocalProvider(
        LocalNothingTokens provides NothingDesignTokens()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NothingTypography,
            shapes = NothingShapes,
            content = content
        )
    }
}

/**
 * Preview theme for compose previews
 */
@Composable
fun GlyphDialPreviewTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(
        LocalNothingTokens provides NothingDesignTokens()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NothingTypography,
            shapes = NothingShapes,
            content = content
        )
    }
}