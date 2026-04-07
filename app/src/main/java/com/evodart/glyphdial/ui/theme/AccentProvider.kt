package com.evodart.glyphdial.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.evodart.glyphdial.data.settings.AccentColor

/**
 * CompositionLocal for dynamic accent color
 */
val LocalAccentColor = compositionLocalOf { Color(AccentColor.RED.hex) }

/**
 * Get Color from AccentColor enum
 */
fun AccentColor.toColor(): Color = Color(this.hex)
