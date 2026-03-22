package com.evodart.glyphdial.ui.screens.recents

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.CallType
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.theme.NothingColors
import java.time.format.DateTimeFormatter

/**
 * Call log detail screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallDetailScreen(
    call: CallLogEntry,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit,
    onAddToContactsClick: () -> Unit,
    onBlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val callColor = when (call.type) {
        CallType.MISSED -> NothingColors.NothingRed
        CallType.REJECTED -> NothingColors.Warning
        else -> NothingColors.CallGreen
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        // Top bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NothingColors.PureWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NothingColors.PureBlack
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Call info header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(NothingColors.SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (call.displayName.firstOrNull() ?: call.number.firstOrNull() ?: "?").toString().uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        color = callColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name/Number
                Text(
                    text = call.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formatPhoneNumber(call.number),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NothingColors.SilverGray
                )
            }
            
            // Quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAction(
                    icon = Icons.Filled.Call,
                    label = "Call",
                    tint = NothingColors.CallGreen,
                    onClick = onCallClick
                )
                QuickAction(
                    icon = Icons.AutoMirrored.Filled.Message,
                    label = "Message",
                    onClick = onSmsClick
                )
                QuickAction(
                    icon = Icons.Filled.PersonAdd,
                    label = "Add",
                    onClick = onAddToContactsClick
                )
                QuickAction(
                    icon = Icons.Filled.Block,
                    label = "Block",
                    tint = NothingColors.NothingRed,
                    onClick = onBlockClick
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Call details
            Text(
                text = "CALL DETAILS",
                style = MaterialTheme.typography.labelMedium,
                color = NothingColors.SilverGray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NothingColors.SurfaceCard)
                    .padding(16.dp)
            ) {
                // Call type
                DetailRow(
                    icon = getCallTypeIcon(call.type),
                    iconTint = callColor,
                    label = "Type",
                    value = call.type.name.lowercase().replaceFirstChar { it.uppercase() }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date & Time
                DetailRow(
                    icon = Icons.Filled.Schedule,
                    label = "Date & Time",
                    value = call.timestamp.atZone(java.time.ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Duration
                DetailRow(
                    icon = Icons.Filled.Timer,
                    label = "Duration",
                    value = if (call.duration > 0) call.formattedDuration else "Not answered"
                )
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = NothingColors.PureWhite,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(NothingColors.SurfaceCard)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NothingColors.SilverGray
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: androidx.compose.ui.graphics.Color = NothingColors.SilverGray
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = NothingColors.SilverGray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = NothingColors.PureWhite
            )
        }
    }
}

private fun getCallTypeIcon(type: CallType): ImageVector = when (type) {
    CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
    CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
    CallType.MISSED -> Icons.AutoMirrored.Filled.CallMissed
    CallType.REJECTED -> Icons.Filled.PhoneDisabled
    CallType.BLOCKED -> Icons.Filled.Block
    CallType.VOICEMAIL -> Icons.Filled.Voicemail
}
