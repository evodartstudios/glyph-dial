package com.evodart.glyphdial.ui.components.dialpad

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NType82

/**
 * SIM preference for calling
 */
enum class SimPreference {
    SIM_1,
    SIM_2,
    ALWAYS_ASK
}

/**
 * Call action bar - aligned with dial pad width
 */
@Composable
fun CallActionBar(
    number: String,
    modifier: Modifier = Modifier,
    onCall: (simSlot: Int?) -> Unit,
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    dualSimEnabled: Boolean = false,
    sim1Name: String = "SIM 1",
    sim2Name: String = "SIM 2",
    simPreference: SimPreference = SimPreference.ALWAYS_ASK
) {
    val haptic = LocalHapticFeedback.current
    var showSimPicker by remember { mutableStateOf(false) }
    
    // Match dial pad width: 3 buttons × 88dp + 2 × 8dp spacing = 280dp
    Row(
        modifier = modifier
            .width(280.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left spacer for alignment (same size as button: 88dp)
        Box(
            modifier = Modifier.size(88.dp),
            contentAlignment = Alignment.Center
        ) {
            if (dualSimEnabled && simPreference == SimPreference.ALWAYS_ASK) {
                SimButton(
                    label = "1",
                    simName = sim1Name,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCall(0)
                    },
                    enabled = number.isNotEmpty()
                )
            }
        }
        
        // Center: White call button with "Call" text
        CallButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                when {
                    number.isEmpty() -> { /* No action */ }
                    !dualSimEnabled -> onCall(null)
                    simPreference == SimPreference.SIM_1 -> onCall(0)
                    simPreference == SimPreference.SIM_2 -> onCall(1)
                    else -> showSimPicker = true
                }
            },
            enabled = number.isNotEmpty()
        )
        
        // Right: backspace - aligned with dial pad button size
        Box(
            modifier = Modifier.size(88.dp),
            contentAlignment = Alignment.Center
        ) {
            if (number.isNotEmpty()) {
                BackspaceButton(
                    onBackspace = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBackspace()
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBackspaceLongPress()
                    }
                )
            } else if (dualSimEnabled && simPreference == SimPreference.ALWAYS_ASK) {
                SimButton(
                    label = "2",
                    simName = sim2Name,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCall(1)
                    },
                    enabled = number.isNotEmpty()
                )
            }
        }
    }
    
    // SIM picker dialog
    if (showSimPicker) {
        SimPickerDialog(
            sim1Name = sim1Name,
            sim2Name = sim2Name,
            onSimSelected = { slot ->
                showSimPicker = false
                onCall(slot)
            },
            onDismiss = { showSimPicker = false }
        )
    }
}

/**
 * Accent-colored circular call button with icon only
 */
@Composable
private fun CallButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val accentColor = LocalAccentColor.current
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = tween(150),
        label = "scale"
    )
    
    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(accentColor)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Call,
            contentDescription = "Call",
            tint = NothingColors.PureWhite,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Backspace button
 */
@Composable
private fun BackspaceButton(
    onBackspace: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onBackspace() },
                    onLongPress = { onLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace",
            tint = NothingColors.SilverGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * SIM selection button
 */
@Composable
private fun SimButton(
    label: String,
    simName: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) NothingColors.SurfaceCard else NothingColors.CharcoalBlack
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SimCard,
                contentDescription = simName,
                tint = if (enabled) NothingColors.PureWhite else NothingColors.DarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) NothingColors.SilverGray else NothingColors.DarkGray
        )
    }
}

/**
 * SIM picker dialog
 */
@Composable
private fun SimPickerDialog(
    sim1Name: String,
    sim2Name: String,
    onSimSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select SIM",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NothingColors.SurfaceCard)
                        .clickable { onSimSelected(0) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.SimCard,
                        contentDescription = null,
                        tint = NothingColors.NothingRed
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = sim1Name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NothingColors.PureWhite
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NothingColors.SurfaceCard)
                        .clickable { onSimSelected(1) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.SimCard,
                        contentDescription = null,
                        tint = NothingColors.CallGreen
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = sim2Name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NothingColors.PureWhite
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NothingColors.SilverGray)
            }
        },
        containerColor = NothingColors.CharcoalBlack
    )
}
