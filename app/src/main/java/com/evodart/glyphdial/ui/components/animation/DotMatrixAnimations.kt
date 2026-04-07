package com.evodart.glyphdial.ui.components.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingMotion
import kotlin.math.*
import kotlin.random.Random

/**
 * Represents a single animated dot
 */
data class AnimatedDot(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val delay: Long,
    val color: Color,
    val size: Float
)

/**
 * State holder for dot matrix animations
 */
class DotMatrixState(
    private val dotCount: Int,
    private val centerX: Float,
    private val centerY: Float,
    private val radius: Float
) {
    val dots: List<AnimatedDot> = generateDots()
    
    private fun generateDots(): List<AnimatedDot> {
        val random = Random(System.currentTimeMillis())
        return (0 until dotCount).map { index ->
            val angle = (index.toFloat() / dotCount) * 2 * PI
            val randomRadius = radius * (0.6f + random.nextFloat() * 0.8f)
            val endX = centerX + (cos(angle) * randomRadius).toFloat()
            val endY = centerY + (sin(angle) * randomRadius).toFloat()
            
            AnimatedDot(
                id = index,
                startX = centerX + (random.nextFloat() - 0.5f) * 20f,
                startY = centerY + (random.nextFloat() - 0.5f) * 20f,
                endX = endX,
                endY = endY,
                delay = (index * NothingMotion.DotMatrix.dotSpawnDelayMs),
                color = if (random.nextFloat() > 0.3f) NothingColors.NothingRed else NothingColors.PureWhite,
                size = 4f + random.nextFloat() * 4f
            )
        }
    }
}

/**
 * Dot Matrix Explosion animation
 * 
 * Creates an explosion effect from a center point, with dots scattering outward.
 * Used for: Accept call button -> Active call screen transition
 *
 * @param isExploding Whether the explosion animation should play
 * @param centerOffset Center point of the explosion (usually button center)
 * @param dotCount Number of dots in the explosion
 * @param color Primary color for dots
 * @param onComplete Callback when animation completes
 */
@Composable
fun DotMatrixExplosion(
    isExploding: Boolean,
    modifier: Modifier = Modifier,
    centerOffset: Offset = Offset.Zero,
    dotCount: Int = NothingMotion.DotMatrix.transitionDots,
    color: Color = NothingColors.NothingRed,
    explosionRadius: Dp = 300.dp,
    durationMs: Int = NothingMotion.CallAnimations.acceptExplosionDurationMs,
    onComplete: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(isExploding) {
        if (isExploding) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = durationMs,
                    easing = NothingMotion.Easing.emphasizedDecelerate
                )
            )
            onComplete()
        }
    }
    
    val dots = remember(centerOffset, dotCount) {
        generateExplosionDots(
            centerX = centerOffset.x,
            centerY = centerOffset.y,
            dotCount = dotCount,
            radius = explosionRadius.value,
            color = color
        )
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        dots.forEach { dot ->
            val dotProgress = calculateDotProgress(
                overallProgress = progress.value,
                dotDelay = dot.delay,
                totalDuration = durationMs.toLong()
            )
            
            if (dotProgress > 0f) {
                val x = lerp(dot.startX, dot.endX, dotProgress)
                val y = lerp(dot.startY, dot.endY, dotProgress)
                val alpha = if (dotProgress > 0.8f) 1f - ((dotProgress - 0.8f) * 5f) else 1f
                val scale = if (dotProgress < 0.2f) dotProgress * 5f else 1f
                
                drawCircle(
                    color = dot.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = dot.size * scale,
                    center = Offset(x, y)
                )
            }
        }
    }
}

/**
 * Dot Matrix Implosion animation
 * 
 * Creates an implosion effect where dots converge to a center point.
 * Used for: Decline call button, end call animation
 */
@Composable
fun DotMatrixImplosion(
    isImploding: Boolean,
    modifier: Modifier = Modifier,
    centerOffset: Offset = Offset.Zero,
    dotCount: Int = NothingMotion.DotMatrix.transitionDots,
    color: Color = NothingColors.NothingRed,
    implosionRadius: Dp = 300.dp,
    durationMs: Int = NothingMotion.CallAnimations.declineImplodeDurationMs,
    onComplete: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(isImploding) {
        if (isImploding) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = durationMs,
                    easing = NothingMotion.Easing.emphasizedAccelerate
                )
            )
            onComplete()
        }
    }
    
    val dots = remember(centerOffset, dotCount) {
        generateExplosionDots(
            centerX = centerOffset.x,
            centerY = centerOffset.y,
            dotCount = dotCount,
            radius = implosionRadius.value,
            color = color
        )
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        dots.forEach { dot ->
            val dotProgress = calculateDotProgress(
                overallProgress = progress.value,
                dotDelay = dot.delay,
                totalDuration = durationMs.toLong()
            )
            
            if (dotProgress > 0f) {
                // Reverse direction for implosion
                val x = lerp(dot.endX, dot.startX, dotProgress)
                val y = lerp(dot.endY, dot.startY, dotProgress)
                val alpha = if (dotProgress < 0.2f) dotProgress * 5f else 1f
                val scale = if (dotProgress > 0.8f) 1f - ((dotProgress - 0.8f) * 5f) else 1f
                
                drawCircle(
                    color = dot.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = dot.size * scale.coerceAtLeast(0.1f),
                    center = Offset(x, y)
                )
            }
        }
    }
}

/**
 * Pulsing dot ring animation
 * 
 * Creates expanding rings of dots, like ripples in water.
 * Used for: Incoming call, ringing state, button press feedback
 */
@Composable
fun DotMatrixPulse(
    isPulsing: Boolean,
    modifier: Modifier = Modifier,
    centerOffset: Offset = Offset.Zero,
    color: Color = NothingColors.NothingRed,
    minRadius: Dp = 40.dp,
    maxRadius: Dp = 120.dp,
    dotCount: Int = 12,
    pulseDurationMs: Int = NothingMotion.CallAnimations.ringingPulseDurationMs
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs, easing = NothingMotion.Easing.easeOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs, easing = NothingMotion.Easing.easeOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(pulseDurationMs / 3)
        ),
        label = "pulse2"
    )
    
    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs, easing = NothingMotion.Easing.easeOut),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(pulseDurationMs * 2 / 3)
        ),
        label = "pulse3"
    )
    
    if (isPulsing) {
        Canvas(modifier = modifier) {
            val centerX = if (centerOffset == Offset.Zero) size.width / 2 else centerOffset.x
            val centerY = if (centerOffset == Offset.Zero) size.height / 2 else centerOffset.y
            
            drawPulseRing(centerX, centerY, pulse1, minRadius.toPx(), maxRadius.toPx(), dotCount, color)
            drawPulseRing(centerX, centerY, pulse2, minRadius.toPx(), maxRadius.toPx(), dotCount, color)
            drawPulseRing(centerX, centerY, pulse3, minRadius.toPx(), maxRadius.toPx(), dotCount, color)
        }
    }
}

/**
 * Floating dots background animation
 * 
 * Creates subtle floating dots in the background, like digital snow.
 * Used for: Incoming call screen background, loading states
 */
@Composable
fun DotMatrixFloating(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    dotCount: Int = 30,
    color: Color = NothingColors.NothingRed.copy(alpha = 0.3f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val floatingDots = remember {
        (0 until dotCount).map { index ->
            FloatingDot(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = 2f + Random.nextFloat() * 4f,
                speed = 0.5f + Random.nextFloat() * 1f,
                phase = Random.nextFloat() * 2 * PI.toFloat()
            )
        }
    }
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            floatingDots.forEach { dot ->
                val x = (dot.x + sin(time * dot.speed * 0.01f + dot.phase) * 0.02f) * size.width
                val y = ((dot.y + time * dot.speed * 0.0001f) % 1f) * size.height
                val alpha = 0.3f + sin(time * 0.05f + dot.phase) * 0.2f
                
                drawCircle(
                    color = color.copy(alpha = alpha.coerceIn(0.1f, 0.5f)),
                    radius = dot.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// ============================================
// HELPER FUNCTIONS AND CLASSES
// ============================================

private data class FloatingDot(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)

private fun generateExplosionDots(
    centerX: Float,
    centerY: Float,
    dotCount: Int,
    radius: Float,
    color: Color
): List<AnimatedDot> {
    val random = Random(System.currentTimeMillis())
    return (0 until dotCount).map { index ->
        val angle = (index.toFloat() / dotCount) * 2 * PI
        val randomRadius = radius * (0.5f + random.nextFloat() * 1f)
        val endX = centerX + (cos(angle) * randomRadius).toFloat()
        val endY = centerY + (sin(angle) * randomRadius).toFloat()
        
        AnimatedDot(
            id = index,
            startX = centerX + (random.nextFloat() - 0.5f) * 30f,
            startY = centerY + (random.nextFloat() - 0.5f) * 30f,
            endX = endX,
            endY = endY,
            delay = (index * NothingMotion.DotMatrix.dotSpawnDelayMs / 2),
            color = if (random.nextFloat() > 0.4f) color else NothingColors.PureWhite.copy(alpha = 0.8f),
            size = 3f + random.nextFloat() * 5f
        )
    }
}

private fun calculateDotProgress(
    overallProgress: Float,
    dotDelay: Long,
    totalDuration: Long
): Float {
    val delayRatio = dotDelay.toFloat() / totalDuration
    val adjustedProgress = (overallProgress - delayRatio) / (1f - delayRatio)
    return adjustedProgress.coerceIn(0f, 1f)
}

private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

private fun DrawScope.drawPulseRing(
    centerX: Float,
    centerY: Float,
    progress: Float,
    minRadius: Float,
    maxRadius: Float,
    dotCount: Int,
    color: Color
) {
    val radius = minRadius + (maxRadius - minRadius) * progress
    val alpha = (1f - progress) * 0.8f
    
    for (i in 0 until dotCount) {
        val angle = (i.toFloat() / dotCount) * 2 * PI
        val x = centerX + (cos(angle) * radius).toFloat()
        val y = centerY + (sin(angle) * radius).toFloat()
        
        drawCircle(
            color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
            radius = 4f * (1f - progress * 0.5f),
            center = Offset(x, y)
        )
    }
}
