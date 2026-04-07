package com.evodart.glyphdial.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingMotion
import com.evodart.glyphdial.ui.theme.NothingTextStyles
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Text reveal style options
 */
enum class TextRevealStyle {
    TYPEWRITER,        // Left to right, one character at a time
    RAIN,              // Characters fall from above
    SCATTER,           // Characters appear from random directions
    SCALE_POP,         // Characters scale in with pop effect
    FADE_IN,           // Simple fade in per character
    MATRIX             // Matrix-style rain effect
}

/**
 * Dot Matrix Text Animation
 * 
 * Reveals text with various dot-matrix inspired animations.
 * Each character appears individually with customizable effects.
 *
 * @param text The text to display
 * @param style Text style for the characters
 * @param revealStyle Animation style for text reveal
 * @param revealDurationPerChar Duration for each character reveal
 * @param color Text color
 * @param modifier Modifier for the component
 */
@Composable
fun DotMatrixText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = NothingTextStyles.timerLarge,
    revealStyle: TextRevealStyle = TextRevealStyle.TYPEWRITER,
    revealDurationPerChar: Int = 50,
    color: Color = MaterialTheme.colorScheme.onBackground,
    startDelay: Long = 0L,
    onRevealComplete: () -> Unit = {}
) {
    var visibleCharCount by remember { mutableIntStateOf(0) }
    val charStates = remember(text) {
        text.indices.map { mutableStateOf(CharacterState.HIDDEN) }
    }
    
    LaunchedEffect(text) {
        delay(startDelay)
        text.indices.forEach { index ->
            charStates[index].value = CharacterState.ANIMATING
            delay(revealDurationPerChar.toLong())
            charStates[index].value = CharacterState.VISIBLE
            visibleCharCount = index + 1
        }
        onRevealComplete()
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEachIndexed { index, char ->
            AnimatedCharacter(
                character = char,
                state = charStates.getOrNull(index)?.value ?: CharacterState.VISIBLE,
                style = style,
                color = color,
                revealStyle = revealStyle,
                index = index
            )
        }
    }
}

/**
 * Individual animated character
 */
@Composable
private fun AnimatedCharacter(
    character: Char,
    state: CharacterState,
    style: TextStyle,
    color: Color,
    revealStyle: TextRevealStyle,
    index: Int
) {
    val animatedScale by animateFloatAsState(
        targetValue = when (state) {
            CharacterState.HIDDEN -> 0f
            CharacterState.ANIMATING -> when (revealStyle) {
                TextRevealStyle.SCALE_POP -> 1.3f
                else -> 1f
            }
            CharacterState.VISIBLE -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "scale"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = when (state) {
            CharacterState.HIDDEN -> 0f
            CharacterState.ANIMATING -> 0.8f
            CharacterState.VISIBLE -> 1f
        },
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.fast,
            easing = NothingMotion.Easing.easeOut
        ),
        label = "alpha"
    )
    
    val animatedOffsetY by animateFloatAsState(
        targetValue = when (state) {
            CharacterState.HIDDEN -> when (revealStyle) {
                TextRevealStyle.RAIN -> -30f
                TextRevealStyle.SCATTER -> if (index % 2 == 0) -20f else 20f
                else -> 0f
            }
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "offsetY"
    )
    
    Text(
        text = character.toString(),
        style = style,
        color = color,
        modifier = Modifier
            .scale(animatedScale)
            .alpha(animatedAlpha)
            .offset(y = animatedOffsetY.dp)
    )
}

private enum class CharacterState {
    HIDDEN,
    ANIMATING,
    VISIBLE
}

/**
 * Animated counter that animates number changes
 * 
 * Used for call timers, statistics, etc.
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = NothingTextStyles.timerLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    prefix: String = "",
    suffix: String = ""
) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.normal,
            easing = NothingMotion.Easing.easeOut
        ),
        label = "counter"
    )
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix.isNotEmpty()) {
            Text(text = prefix, style = style, color = color)
        }
        
        animatedCount.toString().forEach { digit ->
            AnimatedDigit(
                digit = digit,
                style = style,
                color = color
            )
        }
        
        if (suffix.isNotEmpty()) {
            Text(text = suffix, style = style, color = color)
        }
    }
}

@Composable
private fun AnimatedDigit(
    digit: Char,
    style: TextStyle,
    color: Color
) {
    var previousDigit by remember { mutableStateOf(digit) }
    var currentDigit by remember { mutableStateOf(digit) }
    
    LaunchedEffect(digit) {
        if (digit != currentDigit) {
            previousDigit = currentDigit
            currentDigit = digit
        }
    }
    
    val transition = updateTransition(targetState = currentDigit, label = "digit")
    
    val offsetY by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = NothingMotion.Duration.fast,
                easing = NothingMotion.Easing.easeOut
            )
        },
        label = "offsetY"
    ) { _ -> 0f }
    
    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            )
        },
        label = "scale"
    ) { if (it == currentDigit) 1f else 0.8f }
    
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = currentDigit.toString(),
            style = style,
            color = color,
            modifier = Modifier
                .offset(y = offsetY.dp)
                .scale(scale)
        )
    }
}

/**
 * Call timer with dot-matrix styling
 * 
 * Displays call duration in HH:MM:SS format with animated transitions
 */
@Composable
fun DotMatrixTimer(
    durationSeconds: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = NothingTextStyles.timerLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    showHours: Boolean = durationSeconds >= 3600,
    animated: Boolean = true
) {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60
    
    val timeString = if (showHours) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    
    if (animated) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            timeString.forEach { char ->
                if (char == ':') {
                    ColonSeparator(style = style, color = color)
                } else {
                    AnimatedTimerDigit(
                        digit = char,
                        style = style,
                        color = color
                    )
                }
            }
        }
    } else {
        Text(
            text = timeString,
            style = style,
            color = color,
            modifier = modifier
        )
    }
}

@Composable
private fun ColonSeparator(
    style: TextStyle,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "colon")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colonAlpha"
    )
    
    Text(
        text = ":",
        style = style,
        color = color.copy(alpha = alpha)
    )
}

@Composable
private fun AnimatedTimerDigit(
    digit: Char,
    style: TextStyle,
    color: Color
) {
    var previousDigit by remember { mutableStateOf(digit) }
    val isChanging = previousDigit != digit
    
    LaunchedEffect(digit) {
        previousDigit = digit
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isChanging) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "digitScale"
    )
    
    Text(
        text = digit.toString(),
        style = style,
        color = color,
        modifier = Modifier.scale(scale)
    )
}

/**
 * Scrolling marquee text with dot-matrix effect
 * 
 * For long caller names or messages
 */
@Composable
fun DotMatrixMarquee(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    scrollDurationMs: Int = 5000,
    enabled: Boolean = true
) {
    if (!enabled || text.length < 15) {
        // No marquee needed for short text
        Text(
            text = text,
            style = style,
            color = color,
            modifier = modifier
        )
        return
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(scrollDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueOffset"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        // Duplicate text for seamless looping
        Row {
            val displayText = "$text     "
            Text(
                text = displayText,
                style = style,
                color = color,
                modifier = Modifier.offset(x = (offset * displayText.length * 12).dp)
            )
            Text(
                text = displayText,
                style = style,
                color = color,
                modifier = Modifier.offset(x = (offset * displayText.length * 12).dp)
            )
        }
    }
}
