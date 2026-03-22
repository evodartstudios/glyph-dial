package com.evodart.glyphdial.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Nothing-inspired shape system
 * Emphasis on rounded corners and circular elements
 */

// Corner radius values
object NothingRadius {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val pill = 50.dp     // For pill-shaped buttons
    val circle = 100.dp   // Forces circle on square
}

// Material3 Shapes
val NothingShapes = Shapes(
    extraSmall = RoundedCornerShape(NothingRadius.xs),
    small = RoundedCornerShape(NothingRadius.sm),
    medium = RoundedCornerShape(NothingRadius.lg),
    large = RoundedCornerShape(NothingRadius.xl),
    extraLarge = RoundedCornerShape(NothingRadius.xxl)
)

/**
 * Custom shape definitions for specific components
 */
object NothingComponentShapes {
    // Cards
    val card = RoundedCornerShape(NothingRadius.xl)
    val cardSmall = RoundedCornerShape(NothingRadius.lg)
    val cardLarge = RoundedCornerShape(NothingRadius.xxl)
    
    // Circular elements
    val circle = CircleShape
    val circleCard = CircleShape
    
    // Buttons
    val button = RoundedCornerShape(NothingRadius.lg)
    val buttonPill = RoundedCornerShape(NothingRadius.pill)
    val fabShape = CircleShape
    
    // Dial pad
    val dialButton = CircleShape
    val callButton = CircleShape
    
    // Input fields
    val textField = RoundedCornerShape(NothingRadius.md)
    val searchBar = RoundedCornerShape(NothingRadius.pill)
    
    // Chips
    val chip = RoundedCornerShape(NothingRadius.sm)
    val chipSelected = RoundedCornerShape(NothingRadius.sm)
    
    // Sheets
    val bottomSheet = RoundedCornerShape(
        topStart = NothingRadius.xxl,
        topEnd = NothingRadius.xxl
    )
    
    // Dialogs
    val dialog = RoundedCornerShape(NothingRadius.xxl)
    
    // Avatar
    val avatar = CircleShape
    
    // Progress indicators
    val progressTrack = RoundedCornerShape(NothingRadius.pill)
}
