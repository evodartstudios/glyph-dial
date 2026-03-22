package com.evodart.glyphdial.ui.components.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.evodart.glyphdial.ui.components.animation.SwipeDotTransition
import com.evodart.glyphdial.ui.components.animation.TransitionDirection
import com.evodart.glyphdial.ui.theme.NothingColors
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Page routes - single source of truth
 */
object SwipeablePages {
    val pages = listOf("recents", "contacts", "dial", "favorites", "settings")
    
    fun getPageIndex(route: String): Int = pages.indexOf(route).takeIf { it >= 0 } ?: 2
    fun getRoute(pageIndex: Int): String = pages.getOrElse(pageIndex) { "dial" }
}

/**
 * Pager that exposes its state for external control
 * PAGER IS THE SINGLE SOURCE OF TRUTH
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeablePagePager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable (route: String) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            val route = SwipeablePages.getRoute(page)
            val pageOffset = calculatePageOffset(pagerState, page)
            
            PageItem(
                pageOffset = pageOffset,
                content = { pageContent(route) }
            )
        }
        
        // Dot transition overlay
        DotTransitionOverlay(pagerState)
    }
}

/**
 * Create and remember pager state
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberAppPagerState(initialRoute: String = "dial"): PagerState {
    return rememberPagerState(
        initialPage = SwipeablePages.getPageIndex(initialRoute),
        pageCount = { SwipeablePages.pages.size }
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun calculatePageOffset(pagerState: PagerState, page: Int): Float {
    return (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DotTransitionOverlay(pagerState: PagerState) {
    val scrollProgress = abs(pagerState.currentPageOffsetFraction)
    
    if (pagerState.isScrollInProgress && scrollProgress > 0.02f) {
        val direction = if (pagerState.currentPageOffsetFraction > 0) {
            TransitionDirection.RIGHT_TO_LEFT
        } else {
            TransitionDirection.LEFT_TO_RIGHT
        }
        
        SwipeDotTransition(
            progress = (scrollProgress * 1.5f).coerceAtMost(1f),
            direction = direction,
            color = NothingColors.NothingRed,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PageItem(
    pageOffset: Float,
    content: @Composable () -> Unit
) {
    val absOffset = abs(pageOffset).coerceIn(0f, 1f)
    val scale = 1f - (absOffset * 0.05f)
    val alpha = 1f - (absOffset * 0.15f)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    ) {
        content()
    }
}
