package com.evodart.glyphdial.ui.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.LocalAccentColor
import com.evodart.glyphdial.ui.theme.NothingColors
import kotlinx.coroutines.launch

/**
 * Nav items matching SwipeablePages order
 */
enum class NavRoute(val route: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    RECENTS("recents", Icons.Outlined.History, Icons.Filled.History),
    CONTACTS("contacts", Icons.Outlined.Contacts, Icons.Filled.Contacts),
    DIAL("dial", Icons.Filled.Dialpad, Icons.Filled.Dialpad),
    FAVORITES("favorites", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
    SETTINGS("settings", Icons.Outlined.Settings, Icons.Filled.Settings);
}

/**
 * Bottom nav that controls pager directly - uses LocalAccentColor
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NothingBottomNav(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val accentColor = LocalAccentColor.current
    
    // Derive selected index directly from pager
    val selectedIndex = pagerState.currentPage
    val selectedRoute = NavRoute.entries.getOrNull(selectedIndex)?.route ?: "dial"
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(12.dp, RoundedCornerShape(34.dp))
                .clip(RoundedCornerShape(34.dp))
                .background(NothingColors.CharcoalBlack)
        ) {
            // Selection indicator with accent color
            SelectionIndicator(
                selectedIndex = selectedIndex,
                hideForDial = selectedRoute == "dial",
                accentColor = accentColor
            )
            
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavRoute.entries.forEachIndexed { index, navItem ->
                    val isSelected = index == selectedIndex
                    
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (navItem == NavRoute.DIAL) {
                            DialButton(
                                isSelected = isSelected,
                                accentColor = accentColor,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        } else {
                            NavItemButton(
                                navItem = navItem,
                                isSelected = isSelected,
                                accentColor = accentColor,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionIndicator(
    selectedIndex: Int,
    hideForDial: Boolean,
    accentColor: Color
) {
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(150),
        label = "indicator"
    )
    
    if (!hideForDial) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val padding = 4.dp.toPx()
            val itemWidth = (size.width - padding * 2) / 5
            val centerX = padding + (animatedIndex * itemWidth) + (itemWidth / 2)
            
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(centerX - 12.dp.toPx(), size.height - 10.dp.toPx()),
                size = Size(24.dp.toPx(), 3.dp.toPx()),
                cornerRadius = CornerRadius(1.5.dp.toPx())
            )
        }
    }
}

@Composable
private fun DialButton(
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "dialScale"
    )
    
    Box(
        modifier = Modifier
            .size(52.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(accentColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Dialpad,
            contentDescription = "Dial",
            tint = NothingColors.PureWhite,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun NavItemButton(
    navItem: NavRoute,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else NothingColors.SilverGray,
        animationSpec = tween(200),
        label = "navColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "navScale"
    )
    
    Icon(
        imageVector = if (isSelected) navItem.selectedIcon else navItem.icon,
        contentDescription = navItem.route,
        tint = iconColor,
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}
