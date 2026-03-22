package com.evodart.glyphdial.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.evodart.glyphdial.ui.theme.NothingGrid
import com.evodart.glyphdial.ui.theme.NothingSpacing
import kotlin.random.Random

/**
 * Grid item style
 */
enum class GridItemStyle {
    SQUARE,          // Standard square with rounded corners
    CIRCLE,          // Circular card with progress ring
    WIDE,            // Spans 2 columns (banner style)
    TALL             // Double height
}

/**
 * Configuration for organic grid items
 */
data class OrganicGridItem(
    val id: String,
    val style: GridItemStyle = GridItemStyle.SQUARE,
    val span: Int = 1,
    val content: @Composable () -> Unit
)

/**
 * Configuration for organic grid layout
 */
data class OrganicGridConfig(
    val columns: Int = 2,
    val spacing: Dp = NothingGrid.gridSpacing,
    val contentPadding: Dp = NothingGrid.screenPadding,
    val enableRandomness: Boolean = true,
    val circleChance: Float = 0.25f,      // 25% of eligible items become circles
    val staggerAnimation: Boolean = true,
    val staggerDelayMs: Int = 50
)

/**
 * Organic Grid Layout
 * 
 * Creates a Nothing/YOU3-style grid with controlled randomness.
 * Cards can be circles, squares, or wide banners with organic spacing.
 */
@Composable
fun OrganicGrid(
    items: List<OrganicGridItem>,
    modifier: Modifier = Modifier,
    config: OrganicGridConfig = OrganicGridConfig()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(config.columns),
        modifier = modifier,
        contentPadding = PaddingValues(config.contentPadding),
        horizontalArrangement = Arrangement.spacedBy(config.spacing),
        verticalArrangement = Arrangement.spacedBy(config.spacing)
    ) {
        items(
            items = items,
            key = { it.id },
            span = { item ->
                GridItemSpan(item.span.coerceIn(1, config.columns))
            }
        ) { item ->
            val staggerDelay = if (config.staggerAnimation) {
                items.indexOf(item) * config.staggerDelayMs
            } else {
                0
            }
            
            OrganicGridCell(
                item = item,
                staggerDelay = staggerDelay
            )
        }
    }
}

@Composable
private fun OrganicGridCell(
    item: OrganicGridItem,
    staggerDelay: Int
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        item.content()
    }
}

/**
 * Predefined organic grid layouts
 * 
 * These create visually interesting asymmetric layouts
 */
object OrganicGridPatterns {
    
    /**
     * Pattern 1: Circle + Square row, then Square + Circle row
     * Creates a diagonal visual flow
     */
    fun diagonalFlow(itemCount: Int): List<GridItemStyle> {
        return (0 until itemCount).map { index ->
            when (index % 4) {
                0 -> GridItemStyle.CIRCLE
                1 -> GridItemStyle.SQUARE
                2 -> GridItemStyle.SQUARE
                3 -> GridItemStyle.CIRCLE
                else -> GridItemStyle.SQUARE
            }
        }
    }
    
    /**
     * Pattern 2: Wide banner, then 2 squares, then 2 circles
     * Good for highlighting primary content
     */
    fun heroPattern(itemCount: Int): List<GridItemStyle> {
        return (0 until itemCount).map { index ->
            when (index) {
                0 -> GridItemStyle.WIDE
                1, 2 -> GridItemStyle.SQUARE
                3, 4 -> GridItemStyle.CIRCLE
                else -> if ((index - 5) % 4 < 2) GridItemStyle.SQUARE else GridItemStyle.CIRCLE
            }
        }
    }
    
    /**
     * Pattern 3: Random with constraints
     * Never two circles adjacent, always starts with non-circle
     */
    fun controlledRandom(itemCount: Int, seed: Long = System.currentTimeMillis()): List<GridItemStyle> {
        val random = Random(seed)
        val result = mutableListOf<GridItemStyle>()
        
        for (i in 0 until itemCount) {
            val wasCircle = result.lastOrNull() == GridItemStyle.CIRCLE
            
            result.add(
                when {
                    i == 0 -> GridItemStyle.SQUARE // First is always square
                    wasCircle -> GridItemStyle.SQUARE // Never two circles in a row
                    random.nextFloat() < 0.3f -> GridItemStyle.CIRCLE
                    else -> GridItemStyle.SQUARE
                }
            )
        }
        
        return result
    }
    
    /**
     * Pattern 4: Nothing Weather inspired
     * Specific layout matching Nothing Weather app
     */
    fun nothingWeatherStyle(): List<GridItemStyle> {
        return listOf(
            GridItemStyle.WIDE,      // Main temperature banner
            GridItemStyle.CIRCLE,    // Humidity
            GridItemStyle.SQUARE,    // Wind
            GridItemStyle.SQUARE,    // Pressure
            GridItemStyle.CIRCLE,    // UV Index
            GridItemStyle.WIDE,      // Sunrise/Sunset
            GridItemStyle.SQUARE,    // Feels Like
            GridItemStyle.SQUARE     // Visibility
        )
    }
}

/**
 * Helper function to create grid items from data
 */
fun <T> createOrganicGridItems(
    data: List<T>,
    pattern: List<GridItemStyle> = OrganicGridPatterns.controlledRandom(data.size),
    content: @Composable (T, GridItemStyle, Int) -> Unit
): List<OrganicGridItem> {
    return data.mapIndexed { index, item ->
        val style = pattern.getOrElse(index) { GridItemStyle.SQUARE }
        OrganicGridItem(
            id = "grid_${index}",
            style = style,
            span = if (style == GridItemStyle.WIDE) 2 else 1,
            content = { content(item, style, index) }
        )
    }
}

/**
 * Simple grid without LazyVerticalGrid (for smaller grids)
 */
@Composable
fun SimpleOrganicGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    spacing: Dp = NothingGrid.gridSpacing,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        content()
    }
}

/**
 * Grid row helper
 */
@Composable
fun GridRow(
    modifier: Modifier = Modifier,
    spacing: Dp = NothingGrid.gridSpacing,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}
