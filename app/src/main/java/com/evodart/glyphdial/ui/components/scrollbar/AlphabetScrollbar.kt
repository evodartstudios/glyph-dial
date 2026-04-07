package com.evodart.glyphdial.ui.components.scrollbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.evodart.glyphdial.data.settings.ScrollbarPosition
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NDot57
import com.evodart.glyphdial.ui.theme.NothingColors

/**
 * Animated alphabet scrollbar with NDot font indicator
 */
@Composable
fun AnimatedAlphabetScrollbar(
    letters: List<Char>,
    currentLetter: Char?,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
    position: ScrollbarPosition = ScrollbarPosition.RIGHT,
    accentColor: Color = LocalAccentColor.current
) {
    if (letters.isEmpty()) return
    
    var isDragging by remember { mutableStateOf(false) }
    var dragLetter by remember { mutableStateOf<Char?>(null) }
    var barHeight by remember { mutableFloatStateOf(0f) }
    
    // Simple width animation
    val animatedWidth by animateDpAsState(
        targetValue = if (isDragging) 36.dp else 20.dp,
        animationSpec = tween(100),
        label = "scrollbarWidth"
    )
    
    // Display letter: drag letter or current scroll letter
    val displayLetter = dragLetter ?: currentLetter
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .zIndex(100f),
        contentAlignment = if (position == ScrollbarPosition.LEFT) 
            Alignment.CenterStart else Alignment.CenterEnd
    ) {
        // Letter indicator popup - simple circle with NDot font
        AnimatedVisibility(
            visible = isDragging && dragLetter != null,
            enter = scaleIn(tween(80)) + fadeIn(tween(80)),
            exit = scaleOut(tween(80)) + fadeOut(tween(80)),
            modifier = Modifier
                .zIndex(200f)
                .offset(
                    x = if (position == ScrollbarPosition.LEFT) 50.dp else (-50).dp
                )
        ) {
            // Simple red circle with NDot letter
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (dragLetter ?: ' ').toString(),
                    fontFamily = NDot57,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    color = NothingColors.PureWhite,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Scrollbar track
        Column(
            modifier = Modifier
                .width(animatedWidth)
                .fillMaxHeight()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isDragging) NothingColors.SurfaceCard.copy(alpha = 0.7f)
                    else Color.Transparent
                )
                .onSizeChanged { barHeight = it.height.toFloat() }
                .pointerInput(letters) {
                    detectVerticalDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            val index = ((offset.y / barHeight) * letters.size)
                                .toInt().coerceIn(0, letters.size - 1)
                            dragLetter = letters[index]
                            onLetterSelected(letters[index])
                        },
                        onVerticalDrag = { change, _ ->
                            val y = change.position.y.coerceIn(0f, barHeight)
                            val index = ((y / barHeight) * letters.size)
                                .toInt().coerceIn(0, letters.size - 1)
                            if (dragLetter != letters[index]) {
                                dragLetter = letters[index]
                                onLetterSelected(letters[index])
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                            dragLetter = null
                        },
                        onDragCancel = {
                            isDragging = false
                            dragLetter = null
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            letters.forEach { letter ->
                val isActive = letter == displayLetter
                
                Text(
                    text = letter.toString(),
                    fontSize = if (isDragging) 10.sp else 9.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isActive -> accentColor
                        isDragging -> NothingColors.PureWhite.copy(alpha = 0.8f)
                        else -> NothingColors.SilverGray.copy(alpha = 0.5f)
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 0.5.dp)
                )
            }
        }
    }
}
