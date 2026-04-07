package com.evodart.glyphdial.ui.components.dialpad

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.components.animation.nothingClickable
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingTextStyles

/**
 * T9 letter mapping for dial pad
 */
val T9_LETTERS = mapOf(
    '1' to "",
    '2' to "ABC",
    '3' to "DEF",
    '4' to "GHI",
    '5' to "JKL",
    '6' to "MNO",
    '7' to "PQRS",
    '8' to "TUV",
    '9' to "WXYZ",
    '*' to "",
    '0' to "+",
    '#' to ""
)

/**
 * Complete T9-style dial pad - compact and centered
 */
@Composable
fun NothingDialPad(
    modifier: Modifier = Modifier,
    onDigitPressed: (Char) -> Unit,
    onDigitLongPressed: ((Char) -> Unit)? = null,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    
    // Fixed width for proper centering
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: 1, 2, 3
        DialPadRow(
            digits = listOf('1', '2', '3'),
            onDigitPressed = { digit ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onDigitPressed(digit)
            },
            onDigitLongPressed = onDigitLongPressed,
            enabled = enabled
        )
        
        // Row 2: 4, 5, 6
        DialPadRow(
            digits = listOf('4', '5', '6'),
            onDigitPressed = { digit ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onDigitPressed(digit)
            },
            onDigitLongPressed = onDigitLongPressed,
            enabled = enabled
        )
        
        // Row 3: 7, 8, 9
        DialPadRow(
            digits = listOf('7', '8', '9'),
            onDigitPressed = { digit ->
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onDigitPressed(digit)
            },
            onDigitLongPressed = onDigitLongPressed,
            enabled = enabled
        )
        
        // Row 4: *, 0, #
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NothingDialButton(
                digit = '*',
                letters = "",
                onPress = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDigitPressed('*') 
                },
                onLongPress = onDigitLongPressed?.let { { it('*') } },
                enabled = enabled
            )
            NothingDialButton(
                digit = '0',
                letters = "+",
                onPress = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDigitPressed('0') 
                },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDigitLongPressed?.invoke('0') ?: onDigitPressed('+')
                },
                enabled = enabled
            )
            NothingDialButton(
                digit = '#',
                letters = "",
                onPress = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onDigitPressed('#') 
                },
                onLongPress = onDigitLongPressed?.let { { it('#') } },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun DialPadRow(
    digits: List<Char>,
    onDigitPressed: (Char) -> Unit,
    onDigitLongPressed: ((Char) -> Unit)?,
    enabled: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Reduced horizontal spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        digits.forEach { digit ->
            NothingDialButton(
                digit = digit,
                letters = T9_LETTERS[digit] ?: "",
                onPress = { onDigitPressed(digit) },
                onLongPress = onDigitLongPressed?.let { { it(digit) } },
                enabled = enabled
            )
        }
    }
}

@Composable
fun NothingDialButton(
    digit: Char,
    letters: String = "",
    size: Dp = 72.dp,
    onPress: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .nothingClickable(
                enabled = enabled,
                rippleColor = NothingColors.NothingRed
            ) {
                onPress()
            }
            .background(NothingColors.SurfaceCard),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = digit.toString(),
                style = NothingTextStyles.dialPadNumber,
                color = if (enabled) NothingColors.PureWhite else NothingColors.Gray
            )
            
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    style = NothingTextStyles.dialPadLetters,
                    color = if (enabled) NothingColors.SilverGray else NothingColors.DarkGray
                )
            }
        }
    }
}

/**
 * Expanding ring animation
 */
@Composable
private fun ExpandingRingAnimation(
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    duration: Int = 300
) {
    val progress = remember { Animatable(0f) }
    var isVisible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(duration, easing = FastOutSlowInEasing)
        )
        isVisible = false
    }
    
    if (isVisible) {
        Canvas(modifier = modifier) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.minDimension / 2
            
            val startRadius = maxRadius * 0.7f
            val currentRadius = startRadius + (maxRadius - startRadius) * progress.value
            val alpha = (1f - progress.value).coerceIn(0f, 0.9f)
            val strokeWidth = 5.dp.toPx() * (1f - progress.value * 0.3f)
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = currentRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
