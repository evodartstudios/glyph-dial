package com.evodart.glyphdial.ui.components.cards

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.components.animation.ProgressArc
import com.evodart.glyphdial.ui.theme.*

/**
 * Base Nothing Card component
 * 
 * Foundation for all card variants with dot-matrix inspired press animation
 */
@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    shape: Shape = NothingComponentShapes.card,
    backgroundColor: Color = NothingColors.SurfaceCard,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundColor, shape)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, borderColor, shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = NothingColors.NothingRed.copy(alpha = 0.3f)),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }
                    )
                } else {
                    Modifier
                }
            ),
        content = content
    )
}

/**
 * Circular card with progress ring
 * 
 * Nothing-style circular gauge card for displaying progress/stats
 * Used for: Call stats, spam blocked percentage, etc.
 */
@Composable
fun NothingCircleCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    progressColor: Color = NothingColors.NothingRed,
    trackColor: Color = NothingColors.Gray.copy(alpha = 0.3f),
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    labelColor: Color = NothingColors.LightGray,
    backgroundColor: Color = NothingColors.SurfaceCard,
    size: Dp = NothingSizes.cardCircleSize,
    showProgress: Boolean = true,
    animated: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val appearProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        if (animated) {
            appearProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = NothingMotion.Duration.normal,
                    easing = NothingMotion.Easing.emphasizedDecelerate
                )
            )
        } else {
            appearProgress.snapTo(1f)
        }
    }
    
    NothingCard(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = appearProgress.value
                scaleY = appearProgress.value
                alpha = appearProgress.value
            },
        shape = CircleShape,
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Progress ring
            if (showProgress) {
                ProgressArc(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    color = progressColor,
                    trackColor = trackColor,
                    strokeWidth = 6.dp,
                    animated = animated
                )
            }
            
            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(NothingSpacing.lg)
            ) {
                Text(
                    text = value,
                    style = NothingTextStyles.cardValue,
                    color = valueColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(NothingSpacing.xs))
                
                Text(
                    text = label,
                    style = NothingTextStyles.cardLabel,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Square card for information display
 * 
 * Standard Nothing-style square card with rounded corners
 */
@Composable
fun NothingSquareCard(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    subtitle: String? = null,
    icon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = NothingColors.SurfaceCard,
    titleColor: Color = NothingColors.LightGray,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = NothingColors.SilverGray,
    cornerRadius: Dp = NothingRadius.xl,
    aspectRatio: Float = 1f,
    staggerDelay: Int = 0,
    onClick: (() -> Unit)? = null
) {
    val appearProgress = remember { Animatable(0f) }
    
    LaunchedEffect(staggerDelay) {
        kotlinx.coroutines.delay(staggerDelay.toLong())
        appearProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = NothingMotion.Duration.normal,
                easing = NothingMotion.Easing.emphasizedDecelerate
            )
        )
    }
    
    NothingCard(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                scaleX = appearProgress.value
                scaleY = appearProgress.value
                alpha = appearProgress.value
            },
        shape = RoundedCornerShape(cornerRadius),
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(NothingSpacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: title and optional icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = NothingTextStyles.cardLabel,
                    color = titleColor,
                    modifier = Modifier.weight(1f)
                )
                
                icon?.invoke()
            }
            
            // Bottom section: value and optional subtitle
            Column {
                value?.let {
                    Text(
                        text = it,
                        style = NothingTextStyles.cardValue,
                        color = valueColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Wide banner card spanning full width
 * 
 * Used for alerts, summaries, featured content
 */
@Composable
fun NothingBannerCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = NothingColors.SurfaceCard,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: Color = NothingColors.LightGray,
    height: Dp = NothingSizes.cardBannerHeight,
    onClick: (() -> Unit)? = null
) {
    val appearProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        appearProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = NothingMotion.Duration.normal,
                easing = NothingMotion.Easing.emphasizedDecelerate
            )
        )
    }
    
    NothingCard(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                translationY = (1f - appearProgress.value) * 50f
                alpha = appearProgress.value
            },
        shape = NothingComponentShapes.card,
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = NothingSpacing.lg, vertical = NothingSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Leading content (icon, avatar, etc.)
            leadingContent?.let {
                Box(modifier = Modifier.padding(end = NothingSpacing.md)) {
                    it()
                }
            }
            
            // Title and subtitle
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Trailing content (button, indicator, etc.)
            trailingContent?.let {
                Box(modifier = Modifier.padding(start = NothingSpacing.md)) {
                    it()
                }
            }
        }
    }
}

/**
 * Accent card with highlight color
 * 
 * For important/highlighted items
 */
@Composable
fun NothingAccentCard(
    title: String,
    modifier: Modifier = Modifier,
    value: String? = null,
    accentColor: Color = NothingColors.NothingRed,
    icon: @Composable (() -> Unit)? = null,
    aspectRatio: Float = 1f,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.aspectRatio(aspectRatio),
        shape = NothingComponentShapes.card,
        backgroundColor = accentColor.copy(alpha = 0.15f),
        borderColor = accentColor.copy(alpha = 0.3f),
        borderWidth = 1.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(NothingSpacing.lg),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = NothingTextStyles.cardLabel,
                    color = accentColor,
                    modifier = Modifier.weight(1f)
                )
                
                icon?.invoke()
            }
            
            value?.let {
                Text(
                    text = it,
                    style = NothingTextStyles.cardValue,
                    color = accentColor
                )
            }
        }
    }
}
