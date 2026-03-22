package com.evodart.glyphdial.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring

/**
 * Motion system for animations and transitions
 * Following Nothing's smooth, elegant motion language
 */
object NothingMotion {
    
    // ============================================
    // DURATION
    // ============================================
    
    object Duration {
        const val instant = 50
        const val fast = 150
        const val normal = 300
        const val slow = 450
        const val dramatic = 600
        const val pageTransition = 500
        const val dotAnimation = 400
    }
    
    // ============================================
    // EASING CURVES
    // ============================================
    
    object Easing {
        // Standard easing
        val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val easeIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        val easeInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        
        // Emphasized easing (more dramatic)
        val emphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
        val emphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
        
        // Bounce effect
        val bounce = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
        val gentleBounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
        
        // Dot-specific (quick start, slow end)
        val dotTravel = CubicBezierEasing(0.0f, 0.0f, 0.1f, 1.0f)
        val dotSpawn = CubicBezierEasing(0.0f, 0.4f, 0.2f, 1.0f)
    }
    
    // ============================================
    // SPRING CONFIGURATIONS
    // ============================================
    
    object Springs {
        // Snappy for buttons
        val snappy = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
        
        // Gentle for large movements
        val gentle = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
        
        // Bouncy for playful elements
        val bouncy = spring<Float>(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        )
        
        // Smooth for transitions
        val smooth = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }
    
    // ============================================
    // DOT MATRIX ANIMATION CONFIG
    // ============================================
    
    object DotMatrix {
        // Number of dots in various animations
        const val buttonPressDots = 8
        const val transitionDots = 80
        const val explosionDots = 120
        const val loadingDots = 12
        
        // Timing
        const val dotSpawnDelayMs = 15L        // Between each dot spawn
        const val dotTravelDurationMs = 250    // Single dot travel time
        const val dotFadeInMs = 80             // Fade in duration
        const val dotFadeOutMs = 120           // Fade out duration
        
        // Physics
        const val gravity = 0.5f               // For falling animations
        const val friction = 0.95f             // Movement slowdown
        const val bounceCoefficient = 0.3f     // Bounce on collision
        const val maxVelocity = 2000f          // Max speed cap
        
        // Scatter parameters  
        const val minScatterRadius = 50f       // Minimum scatter distance
        const val maxScatterRadius = 400f      // Maximum scatter distance
        const val scatterVariance = 0.3f       // Randomness in scatter
    }
    
    // ============================================
    // TRANSITION SPECS
    // ============================================
    
    object Transitions {
        // Card appear animation
        fun cardAppear(delayMs: Int = 0) = tween<Float>(
            durationMillis = Duration.normal,
            delayMillis = delayMs,
            easing = Easing.emphasizedDecelerate
        )
        
        // Button press scale
        val buttonPress = tween<Float>(
            durationMillis = Duration.fast,
            easing = Easing.easeOut
        )
        
        // Dial button ripple
        val dialButtonRipple = tween<Float>(
            durationMillis = Duration.fast,
            easing = Easing.easeOut
        )
        
        // Page transition
        val pageEnter = tween<Float>(
            durationMillis = Duration.pageTransition,
            easing = Easing.emphasizedDecelerate
        )
        
        val pageExit = tween<Float>(
            durationMillis = Duration.normal,
            easing = Easing.emphasizedAccelerate
        )
        
        // Dot spawn
        val dotSpawn = tween<Float>(
            durationMillis = Duration.fast,
            easing = Easing.dotSpawn
        )
        
        // Dot travel
        val dotTravel = tween<Float>(
            durationMillis = DotMatrix.dotTravelDurationMs,
            easing = Easing.dotTravel
        )
    }
    
    // ============================================
    // CALL SCREEN ANIMATIONS
    // ============================================
    
    object CallAnimations {
        // Incoming call pulsing
        const val ringingPulseDurationMs = 1200
        const val ringingPulseScale = 1.15f
        
        // Accept button press -> screen transition
        const val acceptExplosionDurationMs = 500
        const val acceptDotCount = 100
        
        // Decline button implode
        const val declineImplodeDurationMs = 400
        const val declineDotCount = 60
        
        // Call timer tick
        const val timerDigitChangeDurationMs = 200
        
        // End call collapse
        const val endCallCollapseDurationMs = 450
    }
}

/**
 * Stagger calculation for grid items
 */
fun calculateStaggerDelay(
    index: Int,
    baseDelay: Int = 50,
    maxDelay: Int = 400
): Int {
    return minOf(index * baseDelay, maxDelay)
}

/**
 * Calculate dot positions for explosion effect
 */
fun calculateExplosionPositions(
    centerX: Float,
    centerY: Float,
    dotCount: Int,
    radius: Float
): List<Pair<Float, Float>> {
    return (0 until dotCount).map { index ->
        val angle = (index.toFloat() / dotCount) * 2 * Math.PI
        val randomRadius = radius * (0.7f + Math.random().toFloat() * 0.6f)
        val x = centerX + (Math.cos(angle) * randomRadius).toFloat()
        val y = centerY + (Math.sin(angle) * randomRadius).toFloat()
        Pair(x, y)
    }
}
