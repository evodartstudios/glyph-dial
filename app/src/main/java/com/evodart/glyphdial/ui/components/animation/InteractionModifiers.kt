package com.evodart.glyphdial.ui.components.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingMotion
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.nothingClickable(
    interactionSource: MutableInteractionSource? = null,
    enabled: Boolean = true,
    role: Role? = null,
    rippleColor: Color = NothingColors.NothingRed,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by actualInteractionSource.interactions.collectAsState(initial = null)
    val pressed = isPressed is PressInteraction.Press

    val scale = remember { Animatable(1f) }

    LaunchedEffect(pressed) {
        if (pressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) // Light tick
            scale.animateTo(0.95f, tween(NothingMotion.CallAnimations.acceptExplosionDurationMs / 2, easing = NothingMotion.Easing.emphasizedDecelerate))
        } else {
            scale.animateTo(1f, tween(NothingMotion.CallAnimations.declineImplodeDurationMs, easing = NothingMotion.Easing.emphasizedAccelerate))
        }
    }

    // A fast, precalculated dot explosion state.
    // Instead of instantiating hundreds of objects, we manage simple float arrays.
    class DotRippleState {
        var progress by mutableFloatStateOf(0f)
        var centerX by mutableFloatStateOf(0f)
        var centerY by mutableFloatStateOf(0f)
        var isRunning by mutableStateOf(false)
        val endRadius = 150f
    }
    
    val rippleState = remember { DotRippleState() }

    LaunchedEffect(isPressed) {
        if (isPressed is PressInteraction.Release) {
            val press = (isPressed as PressInteraction.Release).press
            rippleState.centerX = press.pressPosition.x
            rippleState.centerY = press.pressPosition.y
            rippleState.progress = 0f
            rippleState.isRunning = true
            
            launch {
                Animatable(0f).animateTo(
                    1f,
                    animationSpec = tween(400, easing = NothingMotion.Easing.easeOut)
                ) {
                    rippleState.progress = value
                }
                rippleState.isRunning = false
            }
        }
    }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .drawWithContent {
            drawContent()
            if (rippleState.isRunning) {
                val prg = rippleState.progress
                val radius = prg * rippleState.endRadius
                val alpha = (1f - prg).coerceIn(0f, 1f)
                val dotScale = 1f - prg * 0.5f
                
                // Extremely optimized static 8-dot blast
                for (i in 0 until 8) {
                    val angle = (i / 8f) * 2 * PI
                    val x = rippleState.centerX + (cos(angle) * radius).toFloat()
                    val y = rippleState.centerY + (sin(angle) * radius).toFloat()
                    
                    drawCircle(
                        color = rippleColor.copy(alpha = alpha),
                        radius = 6f * dotScale,
                        center = Offset(x, y)
                    )
                }
                
                // Inner ring
                for (i in 0 until 4) {
                    val angle = (i / 4f) * 2 * PI + (PI/4)
                    val x = rippleState.centerX + (cos(angle) * radius * 0.5f).toFloat()
                    val y = rippleState.centerY + (sin(angle) * radius * 0.5f).toFloat()
                    
                    drawCircle(
                        color = rippleColor.copy(alpha = alpha * 0.7f),
                        radius = 4f * dotScale,
                        center = Offset(x, y)
                    )
                }
            }
        }
        .combinedClickable(
            interactionSource = actualInteractionSource,
            indication = null, // Disable default standard android ripple entirely
            enabled = enabled,
            role = role,
            onLongClick = onLongClick,
            onClick = {
                onClick()
            }
        )
}
