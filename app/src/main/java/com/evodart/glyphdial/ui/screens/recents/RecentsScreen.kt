package com.evodart.glyphdial.ui.screens.recents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.evodart.glyphdial.data.model.CallLogEntry
import com.evodart.glyphdial.data.model.CallType
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingTextStyles
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Stacked call group - consecutive calls from same number
 */
data class StackedCallGroup(
    val number: String,
    val displayName: String,
    val photoUri: String?,
    val calls: List<CallLogEntry>,
    val latestCall: CallLogEntry
) {
    val count: Int get() = calls.size
    val isStacked: Boolean get() = calls.size > 1
    val hasMissed: Boolean get() = calls.any { it.type == CallType.MISSED }
    val hasRejected: Boolean get() = calls.any { it.type == CallType.REJECTED }
}

/**
 * Recent calls screen with stacked calls
 */
@Composable
fun RecentsScreen(
    calls: List<CallLogEntry>,
    onCallClick: (CallLogEntry) -> Unit,
    onRedialClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedGroupId by remember { mutableStateOf<String?>(null) }
    
    // Filter calls based on search
    val filteredCalls = remember(calls, searchQuery) {
        if (searchQuery.isBlank()) calls
        else calls.filter { call ->
            call.displayName.contains(searchQuery, ignoreCase = true) ||
            call.number.contains(searchQuery)
        }
    }
    
    // Stack consecutive calls from same number
    val stackedGroups = remember(filteredCalls) {
        stackConsecutiveCalls(filteredCalls)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search recents",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = LocalAccentColor.current
                )
            } else if (stackedGroups.isEmpty()) {
                Text(
                    text = if (searchQuery.isNotBlank()) "No results for \"$searchQuery\"" 
                           else "No recent calls",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NothingColors.SilverGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Group by date
                val groupedByDate = stackedGroups.groupBy { group ->
                    group.latestCall.timestamp.atZone(ZoneId.systemDefault()).toLocalDate()
                }.toSortedMap(compareByDescending { it })
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    groupedByDate.forEach { (date, groups) ->
                        item(key = "header_$date") {
                            DateHeader(date = date)
                        }
                        
                        items(
                            items = groups,
                            key = { "${it.number}_${it.latestCall.id}" }
                        ) { group ->
                            val groupId = "${group.number}_${group.latestCall.id}"
                            val isExpanded = expandedGroupId == groupId
                            
                            StackedCallItem(
                                group = group,
                                isExpanded = isExpanded,
                                onHeaderClick = {
                                    if (group.isStacked) {
                                        expandedGroupId = if (isExpanded) null else groupId
                                    } else {
                                        onCallClick(group.latestCall)
                                    }
                                },
                                onCallClick = onCallClick,
                                onRedialClick = { onRedialClick(group.number) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Stack consecutive calls from the same number
 */
private fun stackConsecutiveCalls(calls: List<CallLogEntry>): List<StackedCallGroup> {
    if (calls.isEmpty()) return emptyList()
    
    val groups = mutableListOf<StackedCallGroup>()
    var currentGroup = mutableListOf<CallLogEntry>()
    var currentNumber: String? = null
    
    for (call in calls.sortedByDescending { it.timestamp }) {
        if (currentNumber == null || call.number == currentNumber) {
            currentGroup.add(call)
            currentNumber = call.number
        } else {
            // Different number - save current group and start new
            if (currentGroup.isNotEmpty()) {
                groups.add(createGroup(currentGroup))
            }
            currentGroup = mutableListOf(call)
            currentNumber = call.number
        }
    }
    
    // Don't forget last group
    if (currentGroup.isNotEmpty()) {
        groups.add(createGroup(currentGroup))
    }
    
    return groups
}

private fun createGroup(calls: List<CallLogEntry>): StackedCallGroup {
    val latest = calls.first()
    return StackedCallGroup(
        number = latest.number,
        displayName = latest.displayName,
        photoUri = latest.photoUri,
        calls = calls,
        latestCall = latest
    )
}

/**
 * Stacked call item with expand/collapse
 */
@Composable
private fun StackedCallItem(
    group: StackedCallGroup,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onCallClick: (CallLogEntry) -> Unit,
    onRedialClick: () -> Unit
) {
    val accentColor = LocalAccentColor.current
    val isMissed = group.hasMissed
    val isRejected = group.hasRejected
    
    val rowColor = when {
        isMissed -> NothingColors.NothingRed
        isRejected -> NothingColors.Warning
        else -> NothingColors.PureWhite
    }
    
    Column {
        // Main row (header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onHeaderClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile photo or icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NothingColors.SurfaceCard),
                contentAlignment = Alignment.Center
            ) {
                if (group.photoUri != null) {
                    AsyncImage(
                        model = group.photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = group.displayName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        color = rowColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                        color = rowColor,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    // Stack count badge
                    if (group.isStacked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${group.count}",
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getCallTypeIcon(group.latestCall.type),
                        contentDescription = null,
                        tint = rowColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${formatPhoneNumber(group.number)} · ${formatTime(group.latestCall.timestamp)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = if (isMissed || isRejected) rowColor.copy(alpha = 0.7f) 
                               else NothingColors.SilverGray,
                        maxLines = 1
                    )
                }
            }
            
            // Expand indicator for stacked
            if (group.isStacked) {
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = NothingColors.SilverGray,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (isExpanded) 180f else 0f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Call button
            IconButton(
                onClick = onRedialClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call",
                    tint = rowColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        
        // Expanded content - individual calls
        AnimatedVisibility(
            visible = isExpanded && group.isStacked,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NothingColors.SurfaceCard.copy(alpha = 0.3f))
                    .padding(start = 72.dp)
            ) {
                group.calls.forEach { call ->
                    ExpandedCallItem(
                        call = call,
                        onClick = { onCallClick(call) }
                    )
                }
            }
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(start = 76.dp),
            thickness = 0.5.dp,
            color = NothingColors.DarkGray.copy(alpha = 0.3f)
        )
    }
}

/**
 * Individual call in expanded stack
 */
@Composable
private fun ExpandedCallItem(
    call: CallLogEntry,
    onClick: () -> Unit
) {
    val isMissed = call.type == CallType.MISSED
    val isRejected = call.type == CallType.REJECTED
    
    val rowColor = when {
        isMissed -> NothingColors.NothingRed
        isRejected -> NothingColors.Warning
        else -> NothingColors.PureWhite
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getCallTypeIcon(call.type),
            contentDescription = null,
            tint = rowColor.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = formatTime(call.timestamp),
            style = MaterialTheme.typography.bodyMedium,
            color = rowColor,
            modifier = Modifier.weight(1f)
        )
        
        if (call.duration > 0) {
            Text(
                text = call.formattedDuration,
                style = MaterialTheme.typography.bodySmall,
                color = NothingColors.SilverGray
            )
        }
    }
    
    HorizontalDivider(
        thickness = 0.5.dp,
        color = NothingColors.DarkGray.copy(alpha = 0.2f)
    )
}

// ============ SHARED COMPONENTS ============

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(NothingColors.SurfaceCard)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = NothingColors.SilverGray,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = NothingColors.PureWhite
            ),
            cursorBrush = SolidColor(LocalAccentColor.current),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NothingColors.SilverGray
                    )
                }
                innerTextField()
            }
        )
        
        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear",
                    tint = NothingColors.SilverGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    
    val displayText = when (date) {
        today -> "TODAY"
        yesterday -> "YESTERDAY"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")).uppercase()
    }
    
    Text(
        text = displayText,
        style = NothingTextStyles.sectionHeader,
        color = NothingColors.SilverGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

private fun getCallTypeIcon(type: CallType): ImageVector = when (type) {
    CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
    CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
    CallType.MISSED -> Icons.AutoMirrored.Filled.CallMissed
    CallType.REJECTED -> Icons.Filled.PhoneDisabled
    CallType.BLOCKED -> Icons.Filled.Block
    CallType.VOICEMAIL -> Icons.Filled.Voicemail
}

private fun formatTime(instant: Instant): String {
    val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
    return localTime.format(DateTimeFormatter.ofPattern("h:mm a"))
}
