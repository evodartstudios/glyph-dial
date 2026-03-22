package com.evodart.glyphdial.ui.screens.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.evodart.glyphdial.service.CallState
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingTextStyles
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium Outgoing Call Screen with animations
 * - Grid-based snow dot animation while ringing
 * - Large profile picture with pulse ring
 * - Status text (Calling..., Ringing...)
 * - iOS-style action buttons at bottom
 * - Dot matrix transition when call connects
 */
@Composable
fun OutgoingCallScreen(
    callerName: String?,
    callerNumber: String,
    callState: CallState,
    photoUri: String? = null,
    onMuteToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onEndCall: () -> Unit,
    isMuted: Boolean = false,
    isSpeaker: Boolean = false,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    
    // Track if call was just connected for transition animation
    var showConnectAnimation by remember { mutableStateOf(false) }
    var previousState by remember { mutableStateOf(callState) }
    
    // Detect state transition to ACTIVE
    LaunchedEffect(callState) {
        if (callState == CallState.ACTIVE && previousState != CallState.ACTIVE) {
            showConnectAnimation = true
        }
        previousState = callState
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        // Background snow animation (only when not connected)
        if (callState != CallState.ACTIVE) {
            GridSnowAnimation(
                accentColor = accentColor,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Connect transition animation
        AnimatedVisibility(
            visible = showConnectAnimation,
            enter = fadeIn() + scaleIn()
        ) {
            DotMatrixConnectTransition(
                accentColor = accentColor,
                onComplete = { showConnectAnimation = false },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Profile section with pulse ring
            ProfileWithPulse(
                name = callerName,
                photoUri = photoUri,
                accentColor = accentColor,
                isPulsing = callState != CallState.ACTIVE
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Name
            Text(
                text = callerName ?: "Unknown",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp
                ),
                color = NothingColors.PureWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Number
            Text(
                text = callerNumber,
                style = MaterialTheme.typography.bodyLarge.copy(
                    letterSpacing = 1.sp
                ),
                color = NothingColors.SilverGray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status with animation
            CallStatusText(callState = callState, accentColor = accentColor)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Android-style action buttons grid
            ActionButtonsGrid(
                isMuted = isMuted,
                isSpeaker = isSpeaker,
                onMuteToggle = onMuteToggle,
                onKeypadClick = onKeypadClick,
                onSpeakerToggle = onSpeakerToggle
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // End call button
            EndCallButton(onClick = onEndCall)
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/**
 * Grid-based snow animation - dots appear/disappear on grid positions
 * Slow fade in/out (2 second cycle), larger dots
 */
@Composable
private fun GridSnowAnimation(
    accentColor: Color,
    modifier: Modifier = Modifier,
    isConnected: Boolean = false,
    gridCols: Int = 16,
    gridRows: Int = 28,
    dotCount: Int = 50
) {
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    
    // Generate grid positions with larger dots
    val gridDots = remember {
        (0 until dotCount).map {
            GridDot(
                col = Random.nextInt(gridCols),
                row = Random.nextInt(gridRows),
                phase = Random.nextFloat() * 2 * PI.toFloat(),
                speed = 0.8f + Random.nextFloat() * 0.4f, // Slower, more uniform speed
                size = 6f + Random.nextFloat() * 6f, // 6-12px dots (bigger)
                isAccent = Random.nextFloat() > 0.5f
            )
        }
    }
    
    // Slower time cycle - full loop every ~15 seconds for smooth 2 sec per dot
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat() * 8, // 8 full sine cycles
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing), // 2 sec per cycle
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier) {
        val cellWidth = size.width / gridCols
        val cellHeight = size.height / gridRows
        
        gridDots.forEach { dot ->
            // Slower sine wave: time * speed gives ~2 second fade in/out
            val alpha = ((sin(time * dot.speed + dot.phase) + 1) / 2f) * 0.7f
            
            if (alpha > 0.05f) {
                val x = (dot.col + 0.5f) * cellWidth
                val y = (dot.row + 0.5f) * cellHeight
                
                // When connected, use green/white, when calling use accent/white
                val dotColor = if (isConnected) {
                    if (dot.isAccent) NothingColors.CallGreen.copy(alpha = alpha)
                    else NothingColors.PureWhite.copy(alpha = alpha * 0.4f)
                } else {
                    if (dot.isAccent) accentColor.copy(alpha = alpha)
                    else NothingColors.PureWhite.copy(alpha = alpha * 0.4f)
                }
                
                drawCircle(
                    color = dotColor,
                    radius = dot.size,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private data class GridDot(
    val col: Int,
    val row: Int,
    val phase: Float,
    val speed: Float,
    val size: Float,
    val isAccent: Boolean
)

/**
 * Profile picture with animated pulse rings
 */
@Composable
private fun ProfileWithPulse(
    name: String?,
    photoUri: String?,
    accentColor: Color,
    isPulsing: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(666)
        ),
        label = "pulse2"
    )
    
    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1333)
        ),
        label = "pulse3"
    )
    
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulse rings
        if (isPulsing) {
            PulseRing(progress = pulse1, accentColor = accentColor, size = 200.dp)
            PulseRing(progress = pulse2, accentColor = accentColor, size = 200.dp)
            PulseRing(progress = pulse3, accentColor = accentColor, size = 200.dp)
        }
        
        // Profile picture or initial
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(NothingColors.SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = (name?.firstOrNull() ?: "?").toString().uppercase(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp
                    ),
                    color = NothingColors.PureWhite
                )
            }
        }
    }
}

@Composable
private fun PulseRing(
    progress: Float,
    accentColor: Color,
    size: Dp
) {
    val scale = 0.7f + progress * 0.3f
    val alpha = (1f - progress) * 0.4f
    
    Box(
        modifier = Modifier
            .size(size * scale)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = alpha))
    )
}

/**
 * Call status text with animated dots
 */
@Composable
private fun CallStatusText(
    callState: CallState,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    val dotCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotCount"
    )
    
    val statusText = when (callState) {
        CallState.DIALING -> "Calling"
        CallState.CONNECTING -> "Connecting"
        CallState.RINGING -> "Ringing"
        CallState.ACTIVE -> "Connected"
        CallState.HOLDING -> "On Hold"
        CallState.DISCONNECTING -> "Ending"
        else -> "Calling"
    }
    
    val dots = if (callState != CallState.ACTIVE) ".".repeat(dotCount) else ""
    
    Text(
        text = "$statusText$dots",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        ),
        color = if (callState == CallState.ACTIVE) NothingColors.CallGreen else accentColor
    )
}

/**
 * Android-style action buttons grid (3x2) 
 * Row 1: Mute, Keypad, Speaker
 * Row 2: Bluetooth, Hold, More
 */
@Composable
private fun ActionButtonsGrid(
    isMuted: Boolean,
    isSpeaker: Boolean,
    isOnHold: Boolean = false,
    onMuteToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onBluetoothClick: () -> Unit = {},
    onHoldToggle: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Row 1: Core controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                label = "mute",
                isActive = isMuted,
                onClick = onMuteToggle
            )
            ActionButton(
                icon = Icons.Filled.Dialpad,
                label = "keypad",
                isActive = false,
                onClick = onKeypadClick
            )
            ActionButton(
                icon = if (isSpeaker) Icons.Filled.VolumeUp else Icons.Filled.VolumeDown,
                label = "speaker",
                isActive = isSpeaker,
                onClick = onSpeakerToggle
            )
        }
        
        // Row 2: Additional controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Filled.Bluetooth,
                label = "bluetooth",
                isActive = false,
                onClick = onBluetoothClick
            )
            ActionButton(
                icon = if (isOnHold) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                label = if (isOnHold) "resume" else "hold",
                isActive = isOnHold,
                onClick = onHoldToggle
            )
            ActionButton(
                icon = Icons.Filled.MoreHoriz,
                label = "more",
                isActive = false,
                onClick = onMoreClick
            )
        }
    }
}

/**
 * Individual action button (iOS style)
 */
@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isActive) NothingColors.PureWhite 
                                else NothingColors.SurfaceCard.copy(alpha = 0.8f),
                contentColor = if (isActive) NothingColors.PureBlack 
                              else NothingColors.PureWhite
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = NothingColors.PureWhite
        )
    }
}

/**
 * Large red end call button
 */
@Composable
private fun EndCallButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = NothingColors.NothingRed,
            contentColor = NothingColors.PureWhite
        )
    ) {
        Icon(
            imageVector = Icons.Filled.CallEnd,
            contentDescription = "End Call",
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * Dot matrix connect transition - dots expand from center covering screen
 */
@Composable
private fun DotMatrixConnectTransition(
    accentColor: Color,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        onComplete()
    }
    
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = maxOf(size.width, size.height)
        
        val gridCols = 25
        val gridRows = 45
        val cellWidth = size.width / gridCols
        val cellHeight = size.height / gridRows
        
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val x = (col + 0.5f) * cellWidth
                val y = (row + 0.5f) * cellHeight
                
                // Distance from center
                val dx = x - centerX
                val dy = y - centerY
                val distance = kotlin.math.sqrt(dx * dx + dy * dy)
                val maxDist = maxRadius * 0.7f
                
                // Calculate when this dot should appear based on distance
                val dotThreshold = distance / maxDist
                val dotProgress = ((progress.value - dotThreshold * 0.3f) / 0.7f).coerceIn(0f, 1f)
                
                if (dotProgress > 0f) {
                    val alpha = dotProgress * (1f - progress.value * 0.5f)
                    val dotSize = 4f * dotProgress
                    
                    drawCircle(
                        color = if ((row + col) % 3 == 0) accentColor.copy(alpha = alpha)
                               else NothingColors.PureWhite.copy(alpha = alpha * 0.6f),
                        radius = dotSize,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

// ============ ACTIVE CALL SCREEN (Updated) ============

/**
 * Active call screen with controls and timer
 */
@Composable
fun ActiveCallScreen(
    callerName: String?,
    callerNumber: String,
    callDuration: Long,
    isMuted: Boolean,
    isSpeaker: Boolean,
    isOnHold: Boolean,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onHoldToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        // Keep dot animation when connected with green color
        GridSnowAnimation(
            accentColor = accentColor,
            isConnected = true,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            // Name
            Text(
                text = callerName ?: callerNumber,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp
                ),
                color = NothingColors.PureWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timer
            Text(
                text = formatDuration(callDuration),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp
                ),
                color = if (isOnHold) NothingColors.Warning else NothingColors.CallGreen
            )
            
            if (isOnHold) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "On Hold",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NothingColors.Warning
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Control buttons
            ActionButtonsGrid(
                isMuted = isMuted,
                isSpeaker = isSpeaker,
                isOnHold = isOnHold,
                onMuteToggle = onMuteToggle,
                onKeypadClick = onKeypadClick,
                onSpeakerToggle = onSpeakerToggle,
                onHoldToggle = onHoldToggle
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            EndCallButton(onClick = onEndCall)
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/**
 * Incoming call screen - answer or decline
 */
@Composable
fun IncomingCallScreen(
    callerName: String?,
    callerNumber: String,
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        // Background animation
        GridSnowAnimation(
            accentColor = accentColor,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Profile with pulse
            ProfileWithPulse(
                name = callerName,
                photoUri = null,
                accentColor = accentColor,
                isPulsing = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = callerName ?: "Unknown",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp
                ),
                color = NothingColors.PureWhite,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = callerNumber,
                style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 1.sp),
                color = NothingColors.SilverGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Incoming call status
            PulsingStatusText("Incoming Call", accentColor)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Answer/Decline buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Decline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = onDecline,
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NothingColors.NothingRed,
                            contentColor = NothingColors.PureWhite
                        )
                    ) {
                        Icon(Icons.Filled.CallEnd, "Decline", Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Decline", style = MaterialTheme.typography.labelMedium, 
                        color = NothingColors.SilverGray)
                }
                
                // Answer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = onAnswer,
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NothingColors.CallGreen,
                            contentColor = NothingColors.PureWhite
                        )
                    ) {
                        Icon(Icons.Filled.Call, "Answer", Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Accept", style = MaterialTheme.typography.labelMedium, 
                        color = NothingColors.SilverGray)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun PulsingStatusText(text: String, accentColor: Color) {
    val alpha by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        ),
        color = accentColor.copy(alpha = alpha)
    )
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}
