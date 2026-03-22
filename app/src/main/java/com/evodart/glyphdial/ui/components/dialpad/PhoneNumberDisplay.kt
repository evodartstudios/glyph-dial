package com.evodart.glyphdial.ui.components.dialpad

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingTextStyles
import com.evodart.glyphdial.ui.theme.NType82

/**
 * Phone number display with copy/paste support via context menu
 * NO TextField - just displays the number with explicit copy/paste
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneNumberDisplay(
    number: String,
    onNumberChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    placeholder: String = "Enter number"
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    
    var showMenu by remember { mutableStateOf(false) }
    
    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = { /* Just focus visual */ },
                    onLongClick = { showMenu = true }
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (number.isEmpty()) {
                Text(
                    text = placeholder,
                    style = NothingTextStyles.phoneNumberLarge.copy(
                        fontFamily = NType82,
                        textAlign = TextAlign.Center
                    ),
                    color = NothingColors.DarkGray
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = number,
                        style = NothingTextStyles.phoneNumberLarge.copy(
                            textAlign = TextAlign.Center,
                            color = NothingColors.PureWhite
                        )
                    )
                    // Blinking cursor
                    Box(
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .width(2.dp)
                            .height(32.dp)
                            .background(
                                NothingColors.NothingRed.copy(alpha = cursorAlpha)
                            )
                    )
                }
            }
            
            // Context menu for copy/paste
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(NothingColors.CharcoalBlack)
            ) {
                if (number.isNotEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Copy", color = NothingColors.PureWhite) },
                        onClick = {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText("phone", number)
                            )
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear", color = NothingColors.NothingRed) },
                        onClick = {
                            onNumberChange("")
                            showMenu = false
                        }
                    )
                }
                
                // Only show paste if clipboard has text
                val clipData = clipboardManager.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    val pasteText = clipData.getItemAt(0).text?.toString()
                    if (!pasteText.isNullOrEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Paste", color = NothingColors.PureWhite) },
                            onClick = {
                                // Filter and append pasted content
                                val filtered = pasteText.filter { 
                                    it.isDigit() || it == '+' || it == '*' || it == '#' 
                                }
                                onNumberChange(number + filtered)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

fun formatPhoneNumber(number: String): String {
    val cleaned = number.filter { it.isDigit() || it == '+' }
    
    return when {
        cleaned.isEmpty() -> ""
        cleaned.startsWith("+") -> formatInternational(cleaned)
        cleaned.length <= 3 -> cleaned
        cleaned.length <= 6 -> "${cleaned.substring(0, 3)} ${cleaned.substring(3)}"
        cleaned.length <= 10 -> {
            "${cleaned.substring(0, 3)} ${cleaned.substring(3, 6)} ${cleaned.substring(6)}"
        }
        else -> {
            "${cleaned.substring(0, 5)} ${cleaned.substring(5, minOf(10, cleaned.length))}${if (cleaned.length > 10) " ${cleaned.substring(10)}" else ""}"
        }
    }
}

private fun formatInternational(number: String): String {
    if (number.length < 3) return number
    val countryCode = number.substring(0, 3)
    val rest = number.substring(3)
    
    return when {
        rest.isEmpty() -> countryCode
        rest.length <= 5 -> "$countryCode $rest"
        rest.length <= 10 -> "$countryCode ${rest.substring(0, 5)} ${rest.substring(5)}"
        else -> "$countryCode ${rest.substring(0, 5)} ${rest.substring(5, 10)} ${rest.substring(10)}"
    }
}
