package com.evodart.glyphdial.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.evodart.glyphdial.ui.components.animation.TransitionDirection
import com.evodart.glyphdial.ui.theme.NothingColors
import com.evodart.glyphdial.ui.theme.NothingMotion

/**
 * Main navigation host for the app with dot matrix transitions
 */
@Composable
fun GlyphDialNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    startDestination: String = Routes.DIAL
) {
    // Track navigation direction for dot transition
    var previousRoute by remember { mutableStateOf(startDestination) }
    var transitionDirection by remember { mutableStateOf(TransitionDirection.LEFT_TO_RIGHT) }
    var showDotTransition by remember { mutableStateOf(false) }
    
    // Listen to navigation changes
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val newRoute = destination.route ?: return@addOnDestinationChangedListener
            val routes = listOf(Routes.DIAL, Routes.RECENTS, Routes.CONTACTS, Routes.FAVORITES)
            val oldIndex = routes.indexOf(previousRoute)
            val newIndex = routes.indexOf(newRoute)
            
            if (oldIndex != -1 && newIndex != -1 && oldIndex != newIndex) {
                transitionDirection = if (newIndex > oldIndex) {
                    TransitionDirection.LEFT_TO_RIGHT
                } else {
                    TransitionDirection.RIGHT_TO_LEFT
                }
                showDotTransition = true
            }
            previousRoute = newRoute
        }
    }
    
    // Reset transition flag
    LaunchedEffect(showDotTransition) {
        if (showDotTransition) {
            kotlinx.coroutines.delay(400)
            showDotTransition = false
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { dotMatrixEnterTransition() },
            exitTransition = { dotMatrixExitTransition() },
            popEnterTransition = { dotMatrixPopEnterTransition() },
            popExitTransition = { dotMatrixPopExitTransition() }
        ) {
            // ============================================
            // MAIN SCREENS (Bottom Navigation)
            // ============================================
            
            composable(
                route = Routes.DIAL,
                enterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { -it / 3 } },
                exitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { -it / 3 } },
                popEnterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { it / 3 } },
                popExitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { it / 3 } }
            ) {
                PlaceholderScreen("Dial Pad", NothingColors.NothingRed)
            }
            
            composable(
                route = Routes.RECENTS,
                enterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { it / 3 } },
                exitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { it / 3 } },
                popEnterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { -it / 3 } },
                popExitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { -it / 3 } }
            ) {
                PlaceholderScreen("Recents", NothingColors.NothingRed)
            }
            
            composable(
                route = Routes.CONTACTS,
                enterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { it / 3 } },
                exitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { it / 3 } },
                popEnterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { -it / 3 } },
                popExitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { -it / 3 } }
            ) {
                PlaceholderScreen("Contacts", NothingColors.NothingRed)
            }
            
            composable(
                route = Routes.FAVORITES,
                enterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { it / 3 } },
                exitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { it / 3 } },
                popEnterTransition = { fadeIn(tween(NothingMotion.Duration.normal)) + slideInHorizontally { -it / 3 } },
                popExitTransition = { fadeOut(tween(NothingMotion.Duration.fast)) + slideOutHorizontally { -it / 3 } }
            ) {
                PlaceholderScreen("Favorites", NothingColors.NothingRed)
            }
            
            // ============================================
            // SETTINGS
            // ============================================
            
            composable(
                route = Routes.SETTINGS,
                enterTransition = { slideInVertically { it } + fadeIn() },
                exitTransition = { slideOutVertically { it } + fadeOut() }
            ) {
                PlaceholderScreen("Settings", NothingColors.LightGray)
            }
            
            composable(Routes.SETTINGS_BLOCKED) {
                PlaceholderScreen("Blocked Numbers", NothingColors.SpamRed)
            }
            
            composable(Routes.SETTINGS_SPEED_DIAL) {
                PlaceholderScreen("Speed Dial", NothingColors.NothingRed)
            }
            
            // ============================================
            // CONTACT SCREENS
            // ============================================
            
            composable(
                route = Routes.CONTACT_DETAIL,
                arguments = listOf(
                    navArgument(NavArgs.CONTACT_ID) { type = NavType.StringType }
                ),
                enterTransition = { slideInHorizontally { it } + fadeIn() },
                exitTransition = { slideOutHorizontally { it } + fadeOut() }
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getString(NavArgs.CONTACT_ID) ?: ""
                PlaceholderScreen("Contact: $contactId", NothingColors.NothingRed)
            }
            
            composable(
                route = Routes.CONTACT_EDIT,
                arguments = listOf(
                    navArgument(NavArgs.CONTACT_ID) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getString(NavArgs.CONTACT_ID) ?: ""
                PlaceholderScreen("Edit Contact: $contactId", NothingColors.NothingRed)
            }
            
            composable(Routes.CONTACT_NEW) {
                PlaceholderScreen("New Contact", NothingColors.NothingRed)
            }
            
            // ============================================
            // CALL SCREENS - Full screen overlays with dot transitions
            // ============================================
            
            composable(
                route = Routes.CALL_INCOMING,
                enterTransition = { 
                    scaleIn(initialScale = 0.8f, animationSpec = tween(400)) + fadeIn(tween(400))
                },
                exitTransition = { 
                    scaleOut(targetScale = 1.2f, animationSpec = tween(300)) + fadeOut(tween(300))
                }
            ) {
                PlaceholderScreen("Incoming Call", NothingColors.AcceptCall)
            }
            
            composable(
                route = Routes.CALL_OUTGOING,
                enterTransition = { 
                    scaleIn(initialScale = 0.5f, animationSpec = tween(500)) + fadeIn(tween(400))
                },
                exitTransition = { 
                    fadeOut(tween(300))
                }
            ) {
                PlaceholderScreen("Outgoing Call", NothingColors.OutgoingCall)
            }
            
            composable(
                route = Routes.CALL_ACTIVE,
                enterTransition = { fadeIn(tween(300)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                PlaceholderScreen("Active Call", NothingColors.OngoingCall)
            }
            
            composable(
                route = Routes.CALL_ENDED,
                enterTransition = { 
                    scaleIn(initialScale = 1.1f, animationSpec = tween(300)) + fadeIn(tween(300))
                },
                exitTransition = { 
                    scaleOut(targetScale = 0.9f, animationSpec = tween(400)) + fadeOut(tween(400))
                }
            ) {
                PlaceholderScreen("Call Ended", NothingColors.EndedCall)
            }
            
            // ============================================
            // UTILITY SCREENS
            // ============================================
            
            composable(
                route = Routes.SEARCH,
                enterTransition = { fadeIn(tween(200)) + slideInVertically { -it / 4 } },
                exitTransition = { fadeOut(tween(200)) + slideOutVertically { -it / 4 } }
            ) {
                PlaceholderScreen("Search", NothingColors.LightGray)
            }
            
            composable(Routes.RECORDINGS) {
                PlaceholderScreen("Recordings", NothingColors.NothingRed)
            }
            
            composable(Routes.STATS) {
                PlaceholderScreen("Statistics", NothingColors.NothingRed)
            }
        }
        
        // Removed: DotMatrixWipe overlay (now using SwipeablePager instead)
    }
}

/**
 * Default transition animations with dot matrix feel
 */
private fun dotMatrixEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.normal,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    ) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.normal,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    )
}

private fun dotMatrixExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.fast,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    ) + scaleOut(
        targetScale = 1.05f,
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.fast,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    )
}

private fun dotMatrixPopEnterTransition(): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.normal,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    ) + scaleIn(
        initialScale = 1.05f,
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.normal,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    )
}

private fun dotMatrixPopExitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.fast,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(
            durationMillis = NothingMotion.Duration.fast,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        )
    )
}

/**
 * Placeholder composable for unimplemented screens
 */
@Composable
private fun PlaceholderScreen(
    name: String,
    accentColor: Color = NothingColors.NothingRed
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            color = accentColor
        )
    }
}
