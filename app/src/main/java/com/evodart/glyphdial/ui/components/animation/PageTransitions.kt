package com.evodart.glyphdial.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingColors
import kotlin.math.abs

/**
 * Transition direction
 */
enum class TransitionDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}

/**
 * Swipe dot transition - grid-based dots that appear during swipe
 * This is the main animation used for page transitions
 */
@Composable
fun SwipeDotTransition(
    progress: Float,
    direction: TransitionDirection = TransitionDirection.LEFT_TO_RIGHT,
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed
) {
    if (progress <= 0.01f || progress >= 0.99f) return
    
    val columns = 25
    val rows = 40
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val cellWidth = size.width / columns
        val cellHeight = size.height / rows
        val dotRadius = 1.5f.dp.toPx()
        val isLeftToRight = direction == TransitionDirection.LEFT_TO_RIGHT
        
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val normalizedCol = if (isLeftToRight) col.toFloat() / columns else (columns - col).toFloat() / columns
                val dotThreshold = normalizedCol
                val dotProgress = ((progress - dotThreshold + 0.2f) / 0.4f).coerceIn(0f, 1f)
                
                if (dotProgress > 0f && dotProgress < 1f) {
                    val alpha = when {
                        dotProgress < 0.3f -> dotProgress / 0.3f
                        dotProgress > 0.7f -> (1f - dotProgress) / 0.3f
                        else -> 1f
                    } * 0.5f
                    
                    val x = col * cellWidth + cellWidth / 2
                    val y = row * cellHeight + cellHeight / 2
                    
                    drawCircle(
                        color = color.copy(alpha = alpha.coerceIn(0f, 0.6f)),
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

/**
 * Grid dot animation for page entry (minimal version)
 */
@Composable
fun GridDotAnimation(
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    columns: Int = 20,
    rows: Int = 35,
    durationMs: Int = 350
) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
        )
    }
    
    Canvas(modifier = modifier) {
        val cellWidth = size.width / columns
        val cellHeight = size.height / rows
        val dotRadius = 2.dp.toPx()
        
        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val delay = (row + col).toFloat() / (rows + columns) * 0.5f
                val dotProgress = ((progress.value - delay) / 0.5f).coerceIn(0f, 1f)
                
                if (dotProgress > 0f) {
                    val alpha = when {
                        dotProgress < 0.3f -> dotProgress / 0.3f
                        dotProgress > 0.6f -> (1f - dotProgress) / 0.4f
                        else -> 1f
                    } * 0.5f
                    
                    val scale = when {
                        dotProgress < 0.2f -> dotProgress / 0.2f
                        dotProgress > 0.7f -> (1f - dotProgress) / 0.3f
                        else -> 1f
                    }
                    
                    val x = col * cellWidth + cellWidth / 2
                    val y = row * cellHeight + cellHeight / 2
                    
                    drawCircle(
                        color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
                        radius = dotRadius * scale,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}
