package com.evodart.glyphdial.ui.screens.dialpad

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.evodart.glyphdial.ui.components.animation.nothingClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evodart.glyphdial.data.model.Contact
import com.evodart.glyphdial.ui.components.dialpad.CallActionBar
import com.evodart.glyphdial.ui.components.dialpad.NothingDialPad
import com.evodart.glyphdial.ui.components.dialpad.PhoneNumberDisplay
import com.evodart.glyphdial.ui.components.dialpad.SimPreference
import com.evodart.glyphdial.ui.components.dialpad.formatPhoneNumber
import com.evodart.glyphdial.ui.theme.NothingColors

/**
 * Dial pad screen with T9 search
 */
@Composable
fun DialPadScreen(
    modifier: Modifier = Modifier,
    dualSimEnabled: Boolean = false,
    simPreference: SimPreference = SimPreference.ALWAYS_ASK,
    sim1Name: String = "SIM 1",
    sim2Name: String = "SIM 2",
    t9Suggestions: List<Contact> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    
    var phoneNumber by remember { mutableStateOf("") }
    
    // Update search query when number changes
    LaunchedEffect(phoneNumber) {
        onSearchQueryChange(phoneNumber)
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top area: T9 suggestions + Phone number display
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // T9 suggestions list - show when typing 2+ digits
            if (phoneNumber.length >= 2) {
                if (t9Suggestions.isNotEmpty()) {
                    T9SuggestionsList(
                        suggestions = t9Suggestions,
                        onSuggestionClick = { contact ->
                            contact.primaryNumber?.let { number ->
                                phoneNumber = number.filter { it.isDigit() || it == '+' }
                            }
                        },
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                    )
                } else {
                    // No matches found
                    Text(
                        text = "No contacts found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NothingColors.SilverGray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
            
            // Phone number display
            PhoneNumberDisplay(
                number = phoneNumber,
                onNumberChange = { newNumber ->
                    phoneNumber = newNumber
                },
                placeholder = "Enter number",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Dial pad
        NothingDialPad(
            onDigitPressed = { digit ->
                phoneNumber += digit
            },
            onDigitLongPressed = { digit ->
                when (digit) {
                    '0' -> phoneNumber += '+'
                    else -> phoneNumber += digit
                }
            },
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Call action bar
        CallActionBar(
            number = phoneNumber,
            onCall = { simSlot ->
                makeCall(context, phoneNumber, simSlot)
            },
            onBackspace = {
                if (phoneNumber.isNotEmpty()) {
                    phoneNumber = phoneNumber.dropLast(1)
                }
            },
            onBackspaceLongPress = {
                phoneNumber = ""
            },
            dualSimEnabled = dualSimEnabled,
            sim1Name = sim1Name,
            sim2Name = sim2Name,
            simPreference = simPreference
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

/**
 * T9 search suggestions - minimal two-line format
 */
@Composable
private fun T9SuggestionsList(
    suggestions: List<Contact>,
    onSuggestionClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        reverseLayout = true
    ) {
        items(suggestions.take(5)) { contact ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nothingClickable { onSuggestionClick(contact) }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Line 1: Name
                        Text(
                            text = contact.displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = NothingColors.PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Line 2: Number | Type
                        contact.primaryNumber?.let { number ->
                            Spacer(modifier = Modifier.height(6.dp))
                            val phoneType = contact.phoneNumbers.firstOrNull()?.type?.name?.lowercase()
                                ?.replaceFirstChar { it.uppercase() } ?: "Mobile"
                            Text(
                                text = "${formatPhoneNumber(number)} · $phoneType",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                color = NothingColors.SilverGray,
                                maxLines = 1
                            )
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = "Call",
                        tint = NothingColors.PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = NothingColors.DarkGray.copy(alpha = 0.3f)
                )
            }
        }
    }
}

private fun makeCall(context: Context, phoneNumber: String, simSlot: Int?) {
    if (phoneNumber.isEmpty()) return
    
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
        simSlot?.let { slot ->
            putExtra("com.android.phone.extra.slot", slot)
            putExtra("simSlot", slot)
        }
    }
    
    try {
        context.startActivity(intent)
    } catch (e: SecurityException) {
        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(dialIntent)
    }
}
