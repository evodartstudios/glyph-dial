package com.evodart.glyphdial.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingMotion

/**
 * Animated progress arc with dot-matrix style ends
 * 
 * Used for circular card progress indicators, call stats, etc.
 */
@Composable
fun ProgressArc(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    trackColor: Color = NothingColors.Gray,
    strokeWidth: Dp = 8.dp,
    startAngle: Float = 135f,
    sweepAngle: Float = 270f,
    animated: Boolean = true,
    animationDuration: Int = NothingMotion.Duration.slow,
    showDotEnds: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) {
            tween(
                durationMillis = animationDuration,
                easing = NothingMotion.Easing.easeOut
            )
        } else {
            snap()
        },
        label = "progress"
    )
    
    Canvas(modifier = modifier) {
        val strokePx = strokeWidth.toPx()
        val radius = (size.minDimension - strokePx) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Draw track
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
        
        // Draw progress
        val progressSweep = sweepAngle * animatedProgress
        if (progressSweep > 0) {
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            
            // Draw dot at end of progress
            if (showDotEnds && animatedProgress > 0.05f) {
                val endAngle = Math.toRadians((startAngle + progressSweep).toDouble())
                val dotX = center.x + (radius * kotlin.math.cos(endAngle)).toFloat()
                val dotY = center.y + (radius * kotlin.math.sin(endAngle)).toFloat()
                
                drawCircle(
                    color = color,
                    radius = strokePx / 2 + 2.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }
    }
}

/**
 * Gradient progress arc
 */
@Composable
fun GradientProgressArc(
    progress: Float,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(
        NothingColors.GradientRedStart,
        NothingColors.GradientRedEnd
    ),
    trackColor: Color = NothingColors.Gray,
    strokeWidth: Dp = 8.dp,
    startAngle: Float = 135f,
    sweepAngle: Float = 270f,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.slow,
            easing = NothingMotion.Easing.easeOut
        ),
        label = "gradientProgress"
    )
    
    Canvas(modifier = modifier) {
        val strokePx = strokeWidth.toPx()
        val radius = (size.minDimension - strokePx) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Draw track
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
        
        // Draw gradient progress
        val progressSweep = sweepAngle * animatedProgress
        if (progressSweep > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = gradientColors,
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
    }
}

/**
 * Pulsing glow effect
 * 
 * Creates a breathing glow around elements (call buttons, etc.)
 */
@Composable
fun PulsingGlow(
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    minAlpha: Float = 0.2f,
    maxAlpha: Float = 0.6f,
    minRadius: Dp = 60.dp,
    maxRadius: Dp = 80.dp,
    pulseDuration: Int = 1500,
    enabled: Boolean = true
) {
    if (!enabled) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = NothingMotion.Easing.easeInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Canvas(modifier = modifier) {
        val alpha = minAlpha + (maxAlpha - minAlpha) * pulseProgress
        val radius = minRadius.toPx() + (maxRadius.toPx() - minRadius.toPx()) * pulseProgress
        
        // Outer glow
        drawCircle(
            color = color.copy(alpha = alpha * 0.3f),
            radius = radius * 1.3f,
            center = Offset(size.width / 2, size.height / 2)
        )
        
        // Middle glow
        drawCircle(
            color = color.copy(alpha = alpha * 0.5f),
            radius = radius,
            center = Offset(size.width / 2, size.height / 2)
        )
        
        // Inner glow
        drawCircle(
            color = color.copy(alpha = alpha * 0.8f),
            radius = radius * 0.7f,
            center = Offset(size.width / 2, size.height / 2)
        )
    }
}

/**
 * Ripple effect for button presses
 */
@Composable
fun DotRipple(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    onComplete: () -> Unit = {}
) {
    val animatable = remember { Animatable(0f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = NothingMotion.Duration.normal,
                    easing = NothingMotion.Easing.easeOut
                )
            )
            onComplete()
        }
    }
    
    if (animatable.value > 0f) {
        Canvas(modifier = modifier) {
            val progress = animatable.value
            val radius = size.minDimension * progress
            val alpha = (1f - progress) * 0.5f
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                center = Offset(size.width / 2, size.height / 2)
            )
        }
    }
}

/**
 * Loading spinner with dots
 */
@Composable
fun DotLoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    dotCount: Int = 8,
    dotSize: Dp = 6.dp,
    radius: Dp = 24.dp,
    duration: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radiusPx = radius.toPx()
        val dotSizePx = dotSize.toPx()
        
        for (i in 0 until dotCount) {
            val angle = Math.toRadians((i * 360.0 / dotCount) + rotation)
            val x = center.x + (radiusPx * kotlin.math.cos(angle)).toFloat()
            val y = center.y + (radiusPx * kotlin.math.sin(angle)).toFloat()
            
            // Fade based on position
            val alpha = (i.toFloat() / dotCount) * 0.8f + 0.2f
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = dotSizePx / 2,
                center = Offset(x, y)
            )
        }
    }
}
