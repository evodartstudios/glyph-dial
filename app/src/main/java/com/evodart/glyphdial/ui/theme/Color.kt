package com.evodart.glyphdial.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Glyph Dial Color Palette
 * Nothing-inspired design with signature red accent
 */
object NothingColors {
    // Signature Nothing Red
    val NothingRed = Color(0xFFD71921)
    val NothingRedLight = Color(0xFFFF4D4D)
    val NothingRedDark = Color(0xFF9E1218)
    val NothingRedAlpha50 = Color(0x80D71921)
    val NothingRedAlpha20 = Color(0x33D71921)
    
    // Monochrome Scale
    val PureBlack = Color(0xFF000000)
    val DeepBlack = Color(0xFF0A0A0A)
    val CharcoalBlack = Color(0xFF141414)
    val DarkGray = Color(0xFF1E1E1E)
    val MediumGray = Color(0xFF2D2D2D)
    val Gray = Color(0xFF3D3D3D)
    val LightGray = Color(0xFF6B6B6B)
    val SilverGray = Color(0xFF9E9E9E)
    val OffWhite = Color(0xFFE5E5E5)
    val PureWhite = Color(0xFFFFFFFF)
    
    // Surface Colors (Dark Theme)
    val SurfaceDark = Color(0xFF121212)
    val SurfaceCard = Color(0xFF1A1A1A)
    val SurfaceCardElevated = Color(0xFF242424)
    val SurfaceCardHover = Color(0xFF2A2A2A)
    val SurfaceOverlay = Color(0x99000000)
    
    // Surface Colors (Light Theme)
    val SurfaceLight = Color(0xFFF5F5F5)
    val SurfaceCardLight = Color(0xFFFFFFFF)
    val SurfaceCardElevatedLight = Color(0xFFF8F8F8)
    
    // Semantic Colors
    val Success = Color(0xFF4CAF50)
    val SuccessDark = Color(0xFF388E3C)
    val Warning = Color(0xFFFFC107)
    val WarningDark = Color(0xFFFFA000)
    val Error = Color(0xFFD71921)
    val ErrorDark = Color(0xFFB71C1C)
    val Info = Color(0xFF2196F3)
    val InfoDark = Color(0xFF1976D2)
    
    // Call State Colors
    val IncomingCall = Color(0xFF4CAF50)
    val OutgoingCall = Color(0xFF2196F3)
    val MissedCall = Color(0xFFD71921)
    val OngoingCall = Color(0xFF4CAF50)
    val EndedCall = Color(0xFF6B6B6B)
    val OnHold = Color(0xFFFFC107)
    
    // Call Action Colors
    val AcceptCall = Color(0xFF4CAF50)
    val DeclineCall = Color(0xFFD71921)
    val EndCall = Color(0xFFD71921)
    val CallGreen = Color(0xFF4CAF50)
    
    // Spam/Block Colors
    val SpamRed = Color(0xFFE53935)
    val SpamRedAlpha = Color(0x33E53935)
    val BlockedGray = Color(0xFF424242)
    
    // Dot Matrix Animation Colors
    val DotPrimary = Color(0xFFD71921)
    val DotSecondary = Color(0xFFFFFFFF)
    val DotTertiary = Color(0xFF6B6B6B)
    val DotGlow = Color(0xFFFF6B6B)
    
    // Gradient Colors (for progress arcs)
    val GradientRedStart = Color(0xFFD71921)
    val GradientRedEnd = Color(0xFFFF4D4D)
    val GradientGreenStart = Color(0xFF4CAF50)
    val GradientGreenEnd = Color(0xFF81C784)
}

/**
 * Color schemes for Material3
 */
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = NothingColors.NothingRed,
    onPrimary = NothingColors.PureWhite,
    primaryContainer = NothingColors.NothingRedDark,
    onPrimaryContainer = NothingColors.PureWhite,
    secondary = NothingColors.LightGray,
    onSecondary = NothingColors.PureWhite,
    secondaryContainer = NothingColors.Gray,
    onSecondaryContainer = NothingColors.OffWhite,
    tertiary = NothingColors.SilverGray,
    onTertiary = NothingColors.PureBlack,
    background = NothingColors.PureBlack,
    onBackground = NothingColors.PureWhite,
    surface = NothingColors.SurfaceDark,
    onSurface = NothingColors.PureWhite,
    surfaceVariant = NothingColors.SurfaceCard,
    onSurfaceVariant = NothingColors.OffWhite,
    surfaceTint = NothingColors.NothingRed,
    error = NothingColors.Error,
    onError = NothingColors.PureWhite,
    errorContainer = NothingColors.ErrorDark,
    onErrorContainer = NothingColors.PureWhite,
    outline = NothingColors.Gray,
    outlineVariant = NothingColors.MediumGray,
    scrim = NothingColors.SurfaceOverlay
)

val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = NothingColors.NothingRed,
    onPrimary = NothingColors.PureWhite,
    primaryContainer = NothingColors.NothingRedLight,
    onPrimaryContainer = NothingColors.PureWhite,
    secondary = NothingColors.Gray,
    onSecondary = NothingColors.PureWhite,
    secondaryContainer = NothingColors.LightGray,
    onSecondaryContainer = NothingColors.DeepBlack,
    tertiary = NothingColors.SilverGray,
    onTertiary = NothingColors.PureBlack,
    background = NothingColors.SurfaceLight,
    onBackground = NothingColors.DeepBlack,
    surface = NothingColors.SurfaceCardLight,
    onSurface = NothingColors.DeepBlack,
    surfaceVariant = NothingColors.SurfaceCardElevatedLight,
    onSurfaceVariant = NothingColors.DarkGray,
    surfaceTint = NothingColors.NothingRed,
    error = NothingColors.Error,
    onError = NothingColors.PureWhite,
    errorContainer = NothingColors.NothingRedLight,
    onErrorContainer = NothingColors.ErrorDark,
    outline = NothingColors.SilverGray,
    outlineVariant = NothingColors.OffWhite,
    scrim = NothingColors.SurfaceOverlay
)

// AMOLED Black theme for maximum battery saving
val AmoledColorScheme = androidx.compose.material3.darkColorScheme(
    primary = NothingColors.NothingRed,
    onPrimary = NothingColors.PureWhite,
    primaryContainer = NothingColors.NothingRedDark,
    onPrimaryContainer = NothingColors.PureWhite,
    secondary = NothingColors.LightGray,
    onSecondary = NothingColors.PureWhite,
    background = NothingColors.PureBlack,
    onBackground = NothingColors.PureWhite,
    surface = NothingColors.PureBlack,
    onSurface = NothingColors.PureWhite,
    surfaceVariant = NothingColors.CharcoalBlack,
    onSurfaceVariant = NothingColors.OffWhite,
    error = NothingColors.Error,
    onError = NothingColors.PureWhite
)