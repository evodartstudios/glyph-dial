package com.evodart.glyphdial.ui.screens.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.evodart.glyphdial.service.AudioRoute
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
    isConference: Boolean = false,
    canMerge: Boolean = false,
    hasBluetoothRoute: Boolean = false,
    onMuteToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onBluetoothClick: () -> Unit = {},
    onMergeClick: () -> Unit = {},
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
                label = if (isMuted) "unmute" else "mute",
                contentDescription = if (isMuted) "Unmute microphone" else "Mute microphone",
                isActive = isMuted,
                onClick = onMuteToggle
            )
            ActionButton(
                icon = Icons.Filled.Dialpad,
                label = "keypad",
                contentDescription = "Open DTMF keypad",
                isActive = false,
                onClick = onKeypadClick
            )
            ActionButton(
                icon = if (isSpeaker) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                label = if (isSpeaker) "speaker on" else "speaker",
                contentDescription = if (isSpeaker) "Switch to earpiece" else "Switch to speaker",
                isActive = isSpeaker,
                onClick = onSpeakerToggle
            )
        }

        // Row 2: Bluetooth (or Merge if possible), Hold, More — always 3 buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Show Bluetooth normally; if canMerge highlight it as Merge
            if (canMerge && !isConference) {
                ActionButton(
                    icon = Icons.Filled.CallMerge,
                    label = "merge",
                    contentDescription = "Merge into conference",
                    isActive = false,
                    onClick = onMergeClick
                )
            } else if (isConference) {
                ActionButton(
                    icon = Icons.Filled.CallMerge,
                    label = "conf.",
                    contentDescription = "Conference call active",
                    isActive = true,
                    onClick = onMergeClick
                )
            } else {
                ActionButton(
                    icon = Icons.Filled.Bluetooth,
                    label = "bluetooth",
                    contentDescription = "Switch to Bluetooth audio",
                    isActive = false,
                    enabled = hasBluetoothRoute,
                    onClick = onBluetoothClick
                )
            }
            ActionButton(
                icon = if (isOnHold) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                label = if (isOnHold) "resume" else "hold",
                contentDescription = if (isOnHold) "Resume call" else "Put call on hold",
                isActive = isOnHold,
                onClick = onHoldToggle
            )
            ActionButton(
                icon = Icons.Filled.MoreHoriz,
                label = "more",
                contentDescription = "More call options",
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
    contentDescription: String = label,
    isActive: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(72.dp)
                .alpha(if (enabled) 1f else 0.4f),
            enabled = enabled,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isActive) NothingColors.PureWhite
                                 else NothingColors.SurfaceCard.copy(alpha = 0.8f),
                contentColor = if (isActive) NothingColors.PureBlack
                               else NothingColors.PureWhite,
                disabledContainerColor = NothingColors.SurfaceCard.copy(alpha = 0.3f),
                disabledContentColor   = NothingColors.SilverGray
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
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
    isConference: Boolean = false,
    canMerge: Boolean = false,
    availableRoutes: List<AudioRoute> = emptyList(),
    isEnding: Boolean = false,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onBluetoothClick: () -> Unit = {},
    onMergeClick: () -> Unit = {},
    onHoldToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onMoreClick: () -> Unit = {},
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    var showRouteSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
            .then(if (isEnding) Modifier.alpha(0.5f) else Modifier)
    ) {
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

            Text(
                text = if (isEnding) "Ending…" else (callerName ?: callerNumber),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp
                ),
                color = if (isEnding) NothingColors.SilverGray else NothingColors.PureWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = formatDuration(callDuration),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp
                ),
                color = when {
                    isEnding  -> NothingColors.SilverGray
                    isOnHold  -> NothingColors.Warning
                    else      -> NothingColors.CallGreen
                }
            )

            if (isOnHold && !isEnding) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "On Hold",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NothingColors.Warning
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!isEnding) {
                ActionButtonsGrid(
                    isMuted          = isMuted,
                    isSpeaker        = isSpeaker,
                    isOnHold         = isOnHold,
                    isConference     = isConference,
                    canMerge         = canMerge,
                    hasBluetoothRoute= availableRoutes.any { it is AudioRoute.Bluetooth },
                    onMuteToggle     = onMuteToggle,
                    onKeypadClick    = onKeypadClick,
                    onSpeakerToggle  = onSpeakerToggle,
                    onHoldToggle     = onHoldToggle,
                    onBluetoothClick = { showRouteSheet = true },
                    onMergeClick     = onMergeClick,
                    onMoreClick      = onMoreClick
                )

                Spacer(modifier = Modifier.height(32.dp))
                EndCallButton(onClick = onEndCall)
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Audio route sheet
    if (showRouteSheet && availableRoutes.isNotEmpty()) {
        AudioRouteBottomSheet(
            routes = availableRoutes,
            onRouteSelected = { route ->
                showRouteSheet = false
                when (route) {
                    is AudioRoute.Earpiece  -> com.evodart.glyphdial.service.GlyphDialCallService.setSpeaker(false)
                    is AudioRoute.Speaker   -> com.evodart.glyphdial.service.GlyphDialCallService.setSpeaker(true)
                    is AudioRoute.Bluetooth -> onBluetoothClick()
                }
            },
            onDismiss = { showRouteSheet = false }
        )
    }
}

/**
 * Incoming call screen - answer or decline, with reject-to-SMS
 */
@Composable
fun IncomingCallScreen(
    callerName: String?,
    callerNumber: String,
    photoUri: String? = null,
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
    onRejectWithSms: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    var showSmsSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        GridSnowAnimation(accentColor = accentColor, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            ProfileWithPulse(
                name = callerName,
                photoUri = photoUri,
                accentColor = accentColor,
                isPulsing = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = callerName ?: "Unknown",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 32.sp
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

            PulsingStatusText("Incoming Call", accentColor)

            Spacer(modifier = Modifier.weight(1f))

            // Respond with SMS
            TextButton(
                onClick = { showSmsSheet = true },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Message,
                    contentDescription = "Reply with SMS",
                    tint = NothingColors.SilverGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Reply with message",
                    style = MaterialTheme.typography.labelMedium,
                    color = NothingColors.SilverGray
                )
            }

            // Decline / Answer — with generous spacing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = onDecline,
                        modifier = Modifier.size(80.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NothingColors.NothingRed,
                            contentColor   = NothingColors.PureWhite
                        )
                    ) { Icon(Icons.Filled.CallEnd, "Decline call", Modifier.size(36.dp)) }
                    Spacer(Modifier.height(12.dp))
                    Text("Decline", style = MaterialTheme.typography.labelMedium, color = NothingColors.SilverGray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(
                        onClick = onAnswer,
                        modifier = Modifier.size(80.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = NothingColors.CallGreen,
                            contentColor   = NothingColors.PureWhite
                        )
                    ) { Icon(Icons.Filled.Call, "Answer call", Modifier.size(36.dp)) }
                    Spacer(Modifier.height(12.dp))
                    Text("Accept", style = MaterialTheme.typography.labelMedium, color = NothingColors.SilverGray)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Reject with SMS sheet
    if (showSmsSheet) {
        RejectWithSmsSheet(
            onSend = { message ->
                onRejectWithSms(message)
                showSmsSheet = false
            },
            onDismiss = { showSmsSheet = false }
        )
    }
}

/**
 * Bottom sheet for rejecting a call with a pre-set or custom SMS
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RejectWithSmsSheet(onSend: (String) -> Unit, onDismiss: () -> Unit) {
    val presets = listOf("Can't talk right now", "Call you back shortly", "I'm in a meeting")
    var custom by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = NothingColors.CharcoalBlack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Reply with message",
                style = MaterialTheme.typography.titleMedium,
                color = NothingColors.PureWhite,
                fontWeight = FontWeight.Bold
            )
            presets.forEach { preset ->
                OutlinedButton(
                    onClick = { onSend(preset) },
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NothingColors.DarkGray)
                ) {
                    Text(preset, color = NothingColors.PureWhite, textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth())
                }
            }
            OutlinedTextField(
                value = custom,
                onValueChange = { custom = it },
                placeholder = { Text("Custom message…", color = NothingColors.SilverGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NothingColors.PureWhite,
                    unfocusedTextColor = NothingColors.PureWhite,
                    focusedBorderColor = NothingColors.NothingRed,
                    unfocusedBorderColor = NothingColors.DarkGray
                ),
                trailingIcon = {
                    if (custom.isNotBlank()) {
                        IconButton(onClick = { onSend(custom) }) {
                            Icon(Icons.Filled.Send, "Send message", tint = NothingColors.NothingRed)
                        }
                    }
                },
                maxLines = 3
            )
        }
    }
}

/**
 * Call Waiting Banner — appears at top when a second call is incoming
 */
@Composable
fun CallWaitingBanner(
    callerDisplay: String,
    onReject: () -> Unit,
    onHoldAndAnswer: () -> Unit,
    onEndAndAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 12.dp, end = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = NothingColors.SurfaceCard,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CallReceived,
                    contentDescription = "Incoming second call",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Incoming call · $callerDisplay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Reject
                TextButton(
                    onClick = onReject,
                    colors = ButtonDefaults.textButtonColors(contentColor = NothingColors.NothingRed)
                ) { Text("Decline") }
                // Hold & Answer
                Button(
                    onClick = onHoldAndAnswer,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) { Text("Hold & Answer", color = NothingColors.PureBlack) }
                // End & Answer
                OutlinedButton(
                    onClick = onEndAndAnswer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NothingColors.DarkGray)
                ) { Text("End & Answer", color = NothingColors.PureWhite) }
            }
        }
    }
}

/**
 * Audio route selection bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRouteBottomSheet(
    routes: List<AudioRoute>,
    onRouteSelected: (AudioRoute) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = NothingColors.CharcoalBlack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Audio output",
                style = MaterialTheme.typography.titleMedium,
                color = NothingColors.PureWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            routes.forEach { route ->
                val (icon, label) = when (route) {
                    is AudioRoute.Earpiece  -> Icons.Filled.PhoneInTalk to "Earpiece"
                    is AudioRoute.Speaker   -> Icons.AutoMirrored.Filled.VolumeUp to "Speaker"
                    is AudioRoute.Bluetooth -> Icons.Filled.Bluetooth to route.name
                }
                TextButton(
                    onClick = { onRouteSelected(route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(icon, contentDescription = label, tint = NothingColors.PureWhite,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(label, color = NothingColors.PureWhite,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                }
            }
        }
    }
}

/**
 * More call options bottom sheet — shown when the "more" button is tapped during an active call.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsSheet(
    canMerge: Boolean,
    isConference: Boolean,
    callerName: String,
    onMerge: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = NothingColors.CharcoalBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "More options",
                style = MaterialTheme.typography.titleMedium,
                color = NothingColors.PureWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (canMerge && !isConference) {
                TextButton(
                    onClick = onMerge,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.CallMerge,
                        contentDescription = "Merge calls",
                        tint = NothingColors.PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Merge calls into conference",
                        color = NothingColors.PureWhite,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }
            }

            if (isConference) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CallMerge,
                        contentDescription = null,
                        tint = NothingColors.CallGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Conference Call Active",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NothingColors.PureWhite,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "With $callerName and others",
                            style = MaterialTheme.typography.bodySmall,
                            color = NothingColors.SilverGray
                        )
                    }
                }
            }

            // Add call — future feature placeholder
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add call",
                    tint = NothingColors.SilverGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Add call (coming soon)",
                    color = NothingColors.SilverGray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }
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

/**
 * In-Call Dialpad overlay for DTMF tones
 */
@androidx.compose.foundation.ExperimentalFoundationApi
@Composable
fun InCallDialpad(
    onDigitDown: (Char) -> Unit,
    onDigitUp: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dialpadKeys = listOf(
        listOf('1' to "", '2' to "ABC", '3' to "DEF"),
        listOf('4' to "GHI", '5' to "JKL", '6' to "MNO"),
        listOf('7' to "PQRS", '8' to "TUV", '9' to "WXYZ"),
        listOf('*' to "", '0' to "+", '#' to "")
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(NothingColors.SurfaceCard.copy(alpha = 0.95f))
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(NothingColors.DarkGray)
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        dialpadKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { (digit, letters) ->
                    InCallDialpadButton(
                        digit = digit,
                        letters = letters,
                        onPointerDown = { onDigitDown(digit) },
                        onPointerUp = onDigitUp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hide button
        FilledIconButton(
            onClick = onClose,
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = NothingColors.DarkGray,
                contentColor = NothingColors.PureWhite
            )
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardHide,
                contentDescription = "Hide Keypad",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun InCallDialpadButton(
    digit: Char,
    letters: String,
    onPointerDown: () -> Unit,
    onPointerUp: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(NothingColors.DarkGray.copy(alpha = 0.3f))
            .pointerInput(digit) {
                detectTapGestures(
                    onPress = {
                        onPointerDown()
                        tryAwaitRelease()
                        onPointerUp()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = digit.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 32.sp
                ),
                color = NothingColors.PureWhite
            )
            if (letters.isNotEmpty() && digit != '*') {
                Text(
                    text = letters,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    ),
                    color = NothingColors.SilverGray
                )
            }
        }
    }
}
