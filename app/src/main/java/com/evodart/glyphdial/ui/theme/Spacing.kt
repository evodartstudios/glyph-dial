package com.evodart.glyphdial.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing system following 8-point grid
 * Consistent spacing throughout the app
 */
object NothingSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 20.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val huge: Dp = 48.dp
    val massive: Dp = 64.dp
}

/**
 * Elevation values for depth
 */
object NothingElevation {
    val none: Dp = 0.dp
    val low: Dp = 1.dp
    val medium: Dp = 4.dp
    val high: Dp = 8.dp
    val overlay: Dp = 16.dp
}

/**
 * Standard component sizes
 */
object NothingSizes {
    // Icon sizes
    val iconXs: Dp = 16.dp
    val iconSm: Dp = 20.dp
    val iconMd: Dp = 24.dp
    val iconLg: Dp = 32.dp
    val iconXl: Dp = 48.dp
    
    // Avatar sizes
    val avatarSm: Dp = 32.dp
    val avatarMd: Dp = 40.dp
    val avatarLg: Dp = 48.dp
    val avatarXl: Dp = 64.dp
    val avatarXxl: Dp = 96.dp
    val avatarHero: Dp = 120.dp
    
    // Button sizes
    val buttonHeightSm: Dp = 32.dp
    val buttonHeightMd: Dp = 40.dp
    val buttonHeightLg: Dp = 48.dp
    val buttonHeightXl: Dp = 56.dp
    
    // Dial pad
    val dialButtonSize: Dp = 72.dp
    val dialButtonSpacing: Dp = 16.dp
    val callButtonSize: Dp = 64.dp
    val callButtonLarge: Dp = 80.dp
    
    // Card sizes
    val cardMinHeight: Dp = 120.dp
    val cardSquareSize: Dp = 160.dp
    val cardCircleSize: Dp = 160.dp
    val cardBannerHeight: Dp = 80.dp
    
    // Bottom navigation
    val bottomNavHeight: Dp = 80.dp
    val bottomNavIconSize: Dp = 24.dp
    
    // Top bar
    val topBarHeight: Dp = 64.dp
    
    // FAB
    val fabSize: Dp = 56.dp
    val fabSizeLarge: Dp = 72.dp
    
    // Touch targets (minimum)
    val minTouchTarget: Dp = 48.dp
    
    // Dividers
    val dividerThickness: Dp = 1.dp
    
    // Progress indicators
    val progressStrokeWidth: Dp = 4.dp
    val progressStrokeWidthLarge: Dp = 8.dp
    
    // Dot sizes for animations
    val dotSm: Dp = 2.dp
    val dotMd: Dp = 4.dp
    val dotLg: Dp = 6.dp
    val dotXl: Dp = 8.dp
}

/**
 * Grid configuration for organic layouts
 */
object NothingGrid {
    val columns: Int = 2
    val gridSpacing: Dp = 12.dp
    val cardPadding: Dp = 16.dp
    val screenPadding: Dp = 16.dp
    
    // Aspect ratios (as floats for Modifier.aspectRatio)
    const val squareRatio: Float = 1f
    const val wideRatio: Float = 2.1f
    const val tallRatio: Float = 0.5f
}
