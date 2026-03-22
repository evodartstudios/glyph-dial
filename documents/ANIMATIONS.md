# Glyph Dial - Advanced Animation System

> Next-generation dot matrix animations and organic grid layouts

---

## 🎆 Dot Matrix Animation Philosophy

The entire app should feel like a **living digital display** - where UI elements are composed of individual dots that can:
- Materialize from nothing
- Dissolve into particles
- Reform into new shapes
- Pulse and breathe
- Create ripple effects

---

## 🔴 Core Dot Matrix Transitions

### 1. Button to Screen Expansion
**Used for:** Accept call → Active call screen

```kotlin
@Composable
fun DotMatrixExplosionTransition(
    isExpanded: Boolean,
    originOffset: Offset,          // Center of button
    onComplete: () -> Unit,
    content: @Composable () -> Unit
) {
    // Animation phases:
    // 1. Button shrinks slightly (anticipation)
    // 2. Button dissolves into ~50-100 dots
    // 3. Dots scatter outward in circular pattern
    // 4. Dots spread to fill entire screen
    // 5. Dots merge into destination content
    
    val dotCount = 80
    val animationDuration = 600 // ms
    
    // Each dot has:
    // - Starting position (button center with slight randomness)
    // - End position (distributed across screen)
    // - Delay offset (center dots start first = explosion effect)
    // - Size animation (small → large → content size)
}
```

### 2. Screen to Button Collapse  
**Used for:** End call → Return to dial pad

```kotlin
@Composable
fun DotMatrixCollapseTransition(
    isCollapsing: Boolean,
    targetOffset: Offset,
    content: @Composable () -> Unit
) {
    // Reverse of explosion:
    // 1. Screen content breaks into dots
    // 2. Dots converge toward button position
    // 3. Dots form button shape
    // 4. Button appears with slight bounce
}
```

### 3. Text Typing Animation
**Used for:** Phone number display, caller name reveal

```kotlin
@Composable
fun DotMatrixTextReveal(
    text: String,
    fontSize: TextUnit,
    revealDuration: Int = 50,      // Per character
    style: RevealStyle = RevealStyle.TYPEWRITER
) {
    // Each character:
    // 1. Invisible initially
    // 2. Dots scatter/rain into position
    // 3. Form the character shape
    // 4. Subtle glow pulse on completion
}

enum class RevealStyle {
    TYPEWRITER,        // Left to right, one at a time
    RAIN,              // Dots fall from top
    SCATTER,           // Dots come from random directions
    CENTER_OUT,        // Center characters first
    MATRIX             // Classic matrix rain effect
}
```

### 4. Card Materialization
**Used for:** Cards appearing on screen load

```kotlin
@Composable
fun DotMatrixCardAppear(
    visible: Boolean,
    delayMs: Int = 0,             // For staggered grid appearance
    style: MaterializeStyle = MaterializeStyle.EDGE_TO_CENTER
) {
    // Card edges form first from scattered dots
    // Then interior fills in
    // Finally content fades in
}

enum class MaterializeStyle {
    EDGE_TO_CENTER,    // Border draws, then fills
    SCATTER,           // Random dots converge
    TOP_DOWN,          // Wipes from top
    PIXELATE,          // Random pixels appear
    PULSE_IN           // Expands from center with dots
}
```

### 5. Dial Button Press
**Used for:** Number pad interaction

```kotlin
@Composable
fun DotMatrixRipple(
    pressed: Boolean,
    color: Color = NothingColors.NothingRed
) {
    // On press:
    // 1. Button content briefly dissolves into dots
    // 2. Dots ripple outward like water
    // 3. Quick reformation with slight bounce
    // Total duration: 150ms
}
```

---

## 🌀 Screen Transitions

### Call State Transitions

```
┌─────────────────────────────────────────────────────────────┐
│                    CALL FLOW ANIMATIONS                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  [Dial Pad] ──→ [Calling Screen]                            │
│     │              Dial button explodes into dots,          │
│     │              dots form caller avatar outline,         │
│     │              then fill to complete transition         │
│     ▼                                                       │
│  [Calling...] ──→ [Connected]                               │
│     │              Pulsing dots around avatar               │
│     │              suddenly stabilize and turn green,       │
│     │              timer dots start counting                │
│     ▼                                                       │
│  [Active Call] ──→ [Call Ended]                             │
│     │              Content dissolves to dots,               │
│     │              dots form "Call Ended" text,             │
│     │              then timer shows final duration          │
│     ▼                                                       │
│  [End Screen] ──→ [Dial Pad]                                │
│                    All dots collapse to center,             │
│                    reform as dial pad buttons               │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### Incoming Call Animation

```kotlin
@Composable
fun IncomingCallAnimation(
    callerName: String,
    callerAvatar: Uri?,
    isRinging: Boolean
) {
    // Stage 1: Dots rain from top forming caller name
    // Stage 2: Avatar circle forms from spiraling dots
    // Stage 3: Accept/Decline buttons pulse with expanding dot rings
    // Stage 4: Background has subtle floating dots (like snow)
    
    // On Accept:
    // - Accept button explodes, dots turn green
    // - Green dots spread and fill screen
    // - Active call UI emerges from green
    
    // On Decline:
    // - Decline button implodes
    // - Red dots are sucked into center
    // - Screen fades to black with dot pattern
}
```

---

## 📐 Organic Grid System

### Philosophy
Unlike rigid Material Design grids, Nothing/YOU3 style uses **controlled randomness**:
- Cards can be circles OR rounded squares
- Sizes vary (small, medium, large)
- Gaps can be uneven
- Items can span multiple cells
- Some "empty" cells create breathing room

### Grid Configuration

```kotlin
data class OrganicGridConfig(
    val columns: Int = 2,
    val baseSpacing: Dp = 12.dp,
    val randomSpacingRange: Dp = 4.dp,      // ±4dp variation
    val allowCircles: Boolean = true,
    val circleChance: Float = 0.3f,         // 30% of eligible items
    val allowSpan: Boolean = true,
    val spanChance: Float = 0.2f,           // 20% items span 2 columns
    val allowEmptyCells: Boolean = true,
    val emptyCellChance: Float = 0.1f       // 10% cells are empty
)

@Composable
fun OrganicGrid(
    items: List<GridItem>,
    config: OrganicGridConfig = OrganicGridConfig(),
    modifier: Modifier = Modifier
) {
    // Algorithm:
    // 1. Create base 2-column grid
    // 2. Randomly assign some items as circles
    // 3. Randomly assign some items to span 2 columns
    // 4. Add slight spacing variations
    // 5. Insert empty cells for "breathing room"
    // 6. Apply staggered entrance animation
}
```

### Grid Item Types

```kotlin
sealed class GridCardStyle {
    // Square cards with rounded corners
    data class Square(
        val size: GridSize = GridSize.NORMAL,
        val cornerRadius: Dp = 20.dp
    ) : GridCardStyle()
    
    // Perfect circles
    data class Circle(
        val hasProgressRing: Boolean = false,
        val ringProgress: Float = 0f
    ) : GridCardStyle()
    
    // Pill shapes (wider than tall)
    data class Pill(
        val span: Int = 2  // Spans full width
    ) : GridCardStyle()
    
    // Asymmetric blob (organic shape)
    data class Organic(
        val seed: Int = 0  // For reproducible random shape
    ) : GridCardStyle()
}

enum class GridSize {
    SMALL,      // 0.8x normal
    NORMAL,     // Standard 1:1
    LARGE,      // 1.2x normal  
    SPAN        // Spans 2 columns
}
```

### Example Organic Layout

```
┌─────────────────────────────────────────┐
│                                         │
│   ┌──────────────────────────────┐      │  <- Wide banner (span 2)
│   │      Recent Calls • 3        │      │
│   └──────────────────────────────┘      │
│                                         │
│   ┌─────────┐    ╭──────────╮           │  <- Square + Circle
│   │         │    │          │           │
│   │ 47      │    │   72%    │           │
│   │ Calls   │    │ Blocked  │           │
│   │         │    │          │           │
│   └─────────┘    ╰──────────╯           │
│                                         │
│        ╭────────╮                       │  <- Offset circle + larger square
│        │        │    ┌────────────┐     │
│        │  JD    │    │            │     │
│        │        │    │  Quick     │     │
│        ╰────────╯    │  Contact   │     │
│                      │            │     │
│   ┌─────────┐        └────────────┘     │  <- Smaller square 
│   │ Speed   │                           │
│   │ Dial    │        ╭────────╮         │  <- Another circle
│   └─────────┘        │  SIM   │         │
│                      ╰────────╯         │
│                                         │
└─────────────────────────────────────────┘
```

---

## ✨ Micro-Interactions

### Button Press Feedback

```kotlin
@Composable
fun NothingButtonWithDots(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        // On press:
        // 1. Scale down to 0.95
        // 2. Spawn 6-8 dots that orbit briefly
        // 3. On release, dots absorbed back
        // 4. Scale back with slight overshoot
    }
}
```

### Pull-to-Refresh with Dots

```kotlin
@Composable
fun DotMatrixRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    // On pull:
    // 1. Dots fall from top into loading area
    // 2. Form spinning circular pattern
    // 3. On complete, dots scatter and content refreshes with wave effect
}
```

### Scroll Effects

```kotlin
@Composable
fun DotMatrixScrollEffect(
    scrollState: ScrollState,
    content: @Composable () -> Unit
) {
    // As user scrolls:
    // - Items leaving view dissolve into dots at edge
    // - Items entering form from dots
    // - Creates "digital waterfall" effect
}
```

---

## 🎨 Dot Specifications

```kotlin
object DotConfig {
    // Dot sizes
    val dotSizeSmall = 2.dp
    val dotSizeNormal = 4.dp
    val dotSizeLarge = 6.dp
    val dotSizeHero = 8.dp
    
    // Animation timing
    val dotSpawnDuration = 30L    // ms between spawning dots
    val dotTravelDuration = 200L  // ms for dot to reach destination
    val dotFadeDuration = 100L    // ms for dot fade in/out
    
    // Physics
    val dotGravity = 0.5f         // For falling effects
    val dotFriction = 0.95f       // Slowdown factor
    val dotBounce = 0.3f          // Bounce on collision
    
    // Colors
    val dotColors = listOf(
        NothingRed,
        NothingRed.copy(alpha = 0.8f),
        NothingRed.copy(alpha = 0.6f),
        Color.White,
        Color.White.copy(alpha = 0.6f)
    )
}
```

---

## 🔊 Haptic Integration

Every dot animation should be paired with subtle haptics:

```kotlin
object DotHaptics {
    // Single dot spawn
    fun singleDot(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    // Multiple dots (button press)
    fun dotBurst(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    
    // Screen transition
    fun dotExplosion(haptic: HapticFeedback) {
        // Custom pattern: quick succession of light taps
        // Mimics the visual of dots scattering
    }
    
    // Dots converging (end of animation)
    fun dotConverge(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}
```

---

## 📱 Performance Considerations

```kotlin
// For smooth 60fps with many dots:
object DotPerformance {
    const val MAX_DOTS = 150           // Never exceed this
    const val BATCH_SIZE = 10          // Spawn in batches
    const val SIMPLIFY_THRESHOLD = 30  // Below 30fps, reduce dots
    
    // Use hardware acceleration
    val useHardwareLayer = true
    
    // Simplify on low-end devices
    fun shouldSimplify(deviceTier: DeviceTier): Boolean {
        return deviceTier == DeviceTier.LOW
    }
}
```
