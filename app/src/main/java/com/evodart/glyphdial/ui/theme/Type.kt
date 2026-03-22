package com.evodart.glyphdial.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.evodart.glyphdial.R

/**
 * Nothing Typography System
 * 
 * Uses official Nothing fonts:
 * - NDot57: Dot matrix display font for numbers and dial pad
 * - NType82: Headline and body text font
 */

// Nothing Dot Matrix font for numbers
val NDot57 = FontFamily(
    Font(R.font.ndot57_regular, FontWeight.Normal)
)

// Nothing Type font for text
val NType82 = FontFamily(
    Font(R.font.ntype82_regular, FontWeight.Normal),
    Font(R.font.ntype82_headline, FontWeight.Bold)
)

/**
 * Typography scale using Nothing fonts
 */
val NothingTypography = Typography(
    
    // Dot Matrix Hero - Large call timer, main numbers
    displayLarge = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-2).sp
    ),
    
    // Dot Matrix Large - Section headers, durations
    displayMedium = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-1).sp
    ),
    
    // Dot Matrix Medium - Card values, phone numbers
    displaySmall = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headlines - Screen titles (NType82 Bold)
    headlineLarge = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    
    headlineMedium = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    headlineSmall = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // Titles - Card titles, list headers
    titleLarge = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    
    titleMedium = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    
    titleSmall = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body text (NType82)
    bodyLarge = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    bodyMedium = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    
    bodySmall = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Labels - Buttons, chips, captions
    labelLarge = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    labelMedium = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    
    labelSmall = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Custom text styles for specific components
 */
object NothingTextStyles {
    // Phone number in dial pad (NDot57)
    val phoneNumberLarge = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 2.sp
    )
    
    // Phone number in lists
    val phoneNumberMedium = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
    
    // Call timer (NDot57)
    val timerLarge = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = 4.sp
    )
    
    // Call timer small
    val timerMedium = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 2.sp
    )
    
    // Dial pad button number (NDot57)
    val dialPadNumber = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    
    // Dial pad T9 letters (NType82)
    val dialPadLetters = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 1.sp
    )
    
    // Card value (NDot57)
    val cardValue = TextStyle(
        fontFamily = NDot57,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
    
    // Card label (NType82)
    val cardLabel = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    // Section headers
    val sectionHeader = TextStyle(
        fontFamily = NType82,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp
    )
}