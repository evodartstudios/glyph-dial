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

    // Standard ripple, themed to match the app
    val rippleIndication = androidx.compose.material3.ripple(color = rippleColor)

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .combinedClickable(
            interactionSource = actualInteractionSource,
            indication = rippleIndication,
            enabled = enabled,
            role = role,
            onLongClick = onLongClick,
            onClick = {
                onClick()
            }
        )
}
