package com.evodart.glyphdial.ui.screens.contacts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import com.evodart.glyphdial.ui.components.animation.nothingClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.data.model.PhoneNumber
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.CallType
import java.time.format.DateTimeFormatter
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.theme.NothingColors

/**
 * Contact detail screen with full info and actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    history: List<CallLogEntry> = emptyList(),
    onBackClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onSmsClick: (String) -> Unit,
    onFavoriteToggle: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NothingColors.PureBlack)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = NothingColors.SilverGray
                        )
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = NothingColors.SilverGray
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Profile header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile photo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(NothingColors.SurfaceCard),
                    contentAlignment = Alignment.Center
                ) {
                    if (contact.photoUri != null) {
                        AsyncImage(
                            model = contact.photoUri,
                            contentDescription = contact.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = contact.initials,
                            style = MaterialTheme.typography.displayMedium,
                            color = NothingColors.NothingRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = NothingColors.PureWhite,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            
            // Quick actions row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                contact.primaryNumber?.let { number ->
                    QuickActionButton(
                        icon = Icons.Filled.Call,
                        label = "Call",
                        onClick = { onCallClick(number) }
                    )
                    QuickActionButton(
                        icon = Icons.AutoMirrored.Filled.Message,
                        label = "Message",
                        onClick = { onSmsClick(number) }
                    )
                }
                QuickActionButton(
                    icon = Icons.Filled.VideoCall,
                    label = "Video",
                    onClick = { /* TODO */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Phone numbers section
            if (contact.phoneNumbers.isNotEmpty()) {
                SectionHeader("Phone Numbers")
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NothingColors.SurfaceCard)
                ) {
                    contact.phoneNumbers.forEachIndexed { index, phone ->
                        PhoneNumberRow(
                            phoneNumber = phone,
                            onCallClick = { onCallClick(phone.number) },
                            onSmsClick = { onSmsClick(phone.number) }
                        )
                        if (index < contact.phoneNumbers.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = NothingColors.DarkGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Contact info section
            SectionHeader("Contact Info")
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NothingColors.SurfaceCard)
            ) {
                InfoRow(
                    icon = Icons.Filled.Person,
                    label = "Name",
                    value = contact.displayName
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = NothingColors.DarkGray.copy(alpha = 0.3f)
                )
                InfoRow(
                    icon = Icons.Filled.Badge,
                    label = "Contact ID",
                    value = contact.id.toString()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Call history section
            if (history.isNotEmpty()) {
                SectionHeader("Call History")
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NothingColors.SurfaceCard)
                ) {
                    history.forEachIndexed { index, historyCall ->
                        val historyCallColor = when (historyCall.type) {
                            CallType.MISSED -> NothingColors.NothingRed
                            CallType.REJECTED -> NothingColors.Warning
                            else -> NothingColors.CallGreen
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (historyCall.type) {
                                    CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                                    CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                                    CallType.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                                    CallType.REJECTED -> Icons.Filled.PhoneDisabled
                                    CallType.BLOCKED -> Icons.Filled.Block
                                    CallType.VOICEMAIL -> Icons.Filled.Voicemail
                                },
                                contentDescription = null,
                                tint = historyCallColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = historyCall.type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NothingColors.PureWhite
                                )
                                Text(
                                    text = historyCall.timestamp.atZone(java.time.ZoneId.systemDefault())
                                        .format(DateTimeFormatter.ofPattern("MMM d, h:mm a")) + 
                                        if (historyCall.duration > 0) " · ${historyCall.formattedDuration}" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NothingColors.SilverGray
                                )
                            }
                        }
                        
                        if (index < history.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = NothingColors.DarkGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Delete button
            TextButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = NothingColors.NothingRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete Contact",
                    color = NothingColors.NothingRed
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // FAB for favorite
    FloatingActionButton(
        onClick = onFavoriteToggle,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(24.dp),
        containerColor = if (contact.starred) NothingColors.Warning else NothingColors.SurfaceCard,
        contentColor = if (contact.starred) NothingColors.PureBlack else NothingColors.PureWhite,
        shape = CircleShape
    ) {
        Icon(
            imageVector = if (contact.starred) Icons.Filled.Star else Icons.Filled.StarBorder,
            contentDescription = "Favorite"
        )
    }
}
}


@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.nothingClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(NothingColors.SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = NothingColors.PureWhite,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = NothingColors.SilverGray
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = NothingColors.SilverGray,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun PhoneNumberRow(
    phoneNumber: PhoneNumber,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Phone type icon
        Icon(
            imageVector = Icons.Filled.Phone,
            contentDescription = null,
            tint = NothingColors.SilverGray,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Number and type
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatPhoneNumber(phoneNumber.number),
                style = MaterialTheme.typography.bodyLarge,
                color = NothingColors.PureWhite
            )
            Text(
                text = phoneNumber.type.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = NothingColors.SilverGray
            )
        }
        
        // Actions
        IconButton(onClick = onSmsClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Message,
                contentDescription = "SMS",
                tint = NothingColors.SilverGray,
                modifier = Modifier.size(20.dp)
            )
        }
        IconButton(onClick = onCallClick) {
            Icon(
                imageVector = Icons.Filled.Call,
                contentDescription = "Call",
                tint = NothingColors.CallGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NothingColors.SilverGray,
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
