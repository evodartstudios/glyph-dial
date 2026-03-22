# Glyph Dial - Design System

> Nothing-inspired design language for a next-generation dialer experience

---

## 🎨 Color Palette

### Primary Colors

```kotlin
// Nothing Core Colors
object NothingColors {
    // Primary Red - Signature Nothing accent
    val NothingRed = Color(0xFFD71921)
    val NothingRedLight = Color(0xFFFF4D4D)
    val NothingRedDark = Color(0xFF9E1218)
    
    // Monochrome Scale
    val PureBlack = Color(0xFF000000)
    val DeepBlack = Color(0xFF0A0A0A)
    val CharcoalBlack = Color(0xFF141414)
    val DarkGray = Color(0xFF1E1E1E)
    val MediumGray = Color(0xFF2D2D2D)
    val Gray = Color(0xFF3D3D3D)
    val LightGray = Color(0xFF6B6B6B)
    val SilverGray = Color(0xFF9E9E9E)
    val OffWhite = Color(0xFFE5E5E5)
    val PureWhite = Color(0xFFFFFFFF)
    
    // Surface Colors
    val SurfaceDark = Color(0xFF121212)
    val SurfaceCard = Color(0xFF1A1A1A)
    val SurfaceCardElevated = Color(0xFF242424)
    val SurfaceOverlay = Color(0x99000000)
    
    // Semantic Colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFFC107)
    val Error = Color(0xFFD71921)
    val Info = Color(0xFF2196F3)
    
    // Call States
    val IncomingCall = Color(0xFF4CAF50)
    val OutgoingCall = Color(0xFF2196F3)
    val MissedCall = Color(0xFFD71921)
    val OngoingCall = Color(0xFF4CAF50)
    val EndedCall = Color(0xFF6B6B6B)
    
    // Spam/Block
    val SpamRed = Color(0xFFE53935)
    val BlockedGray = Color(0xFF424242)
}
```

### Theme Modes

```kotlin
// Dark Theme (Default)
val DarkColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = PureWhite,
    secondary = LightGray,
    onSecondary = PureWhite,
    background = PureBlack,
    onBackground = PureWhite,
    surface = SurfaceDark,
    onSurface = PureWhite,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = OffWhite,
    error = Error,
    onError = PureWhite
)

// AMOLED Black Theme
val AmoledColorScheme = darkColorScheme(
    primary = NothingRed,
    background = PureBlack,
    surface = PureBlack,
    surfaceVariant = CharcoalBlack
)

// Light Theme
val LightColorScheme = lightColorScheme(
    primary = NothingRed,
    onPrimary = PureWhite,
    background = PureWhite,
    onBackground = DeepBlack,
    surface = OffWhite,
    onSurface = DeepBlack
)
```

---

## ✏️ Typography

### Font Family

```kotlin
// Primary: NDot (Nothing's dot matrix font for displays)
// Secondary: Inter or custom Nothing Sans for body

object NothingTypography {
    
    // Dot Matrix Display Font (for numbers, timers, headers)
    val DotMatrix = FontFamily(
        Font(R.font.ndot_55, FontWeight.Normal),
        Font(R.font.ndot_57, FontWeight.Bold)
    )
    
    // Body Font
    val NothingSans = FontFamily(
        Font(R.font.nothing_sans_regular, FontWeight.Normal),
        Font(R.font.nothing_sans_medium, FontWeight.Medium),
        Font(R.font.nothing_sans_bold, FontWeight.Bold)
    )
    
    // Monospace for numbers
    val Mono = FontFamily(
        Font(R.font.jetbrains_mono, FontWeight.Normal)
    )
}
```

### Type Scale

```kotlin
val NothingTypographyScale = Typography(
    
    // Dot Matrix Hero - Large call timer, main temperature
    displayLarge = TextStyle(
        fontFamily = DotMatrix,
        fontSize = 96.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-2).sp
    ),
    
    // Dot Matrix Large - Section headers, call duration
    displayMedium = TextStyle(
        fontFamily = DotMatrix,
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-1).sp
    ),
    
    // Dot Matrix Medium - Card primary values
    displaySmall = TextStyle(
        fontFamily = DotMatrix,
        fontSize = 48.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp
    ),
    
    // Headlines - Screen titles
    headlineLarge = TextStyle(
        fontFamily = NothingSans,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    ),
    
    headlineMedium = TextStyle(
        fontFamily = NothingSans,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.sp
    ),
    
    headlineSmall = TextStyle(
        fontFamily = NothingSans,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    
    // Titles - Card titles, list headers
    titleLarge = TextStyle(
        fontFamily = NothingSans,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp
    ),
    
    titleMedium = TextStyle(
        fontFamily = NothingSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    
    titleSmall = TextStyle(
        fontFamily = NothingSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    
    // Body text
    bodyLarge = TextStyle(
        fontFamily = NothingSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp
    ),
    
    bodyMedium = TextStyle(
        fontFamily = NothingSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp
    ),
    
    bodySmall = TextStyle(
        fontFamily = NothingSans,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp
    ),
    
    // Labels - Buttons, chips, captions
    labelLarge = TextStyle(
        fontFamily = NothingSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    ),
    
    labelMedium = TextStyle(
        fontFamily = NothingSans,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    ),
    
    labelSmall = TextStyle(
        fontFamily = NothingSans,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
)
```

---

## 📐 Spacing & Layout

### Spacing Scale

```kotlin
object NothingSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
    val massive = 64.dp
}
```

### Corner Radius

```kotlin
object NothingRadius {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val pill = 50.dp    // For buttons, chips
    val circle = 100.dp // For circular elements
}
```

### Elevation

```kotlin
object NothingElevation {
    val none = 0.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
    val overlay = 16.dp
}
```

---

## 🔲 Grid System

### Nothing Card Grid

The Nothing UI uses a distinctive grid system with mixed shapes:

```kotlin
// Grid Configuration
object NothingGrid {
    // Standard column width for 2-column layout
    val columnCount = 2
    val gridSpacing = 12.dp
    val cardPadding = 16.dp
    
    // Card aspect ratios
    val squareRatio = 1f          // 1:1 - Standard info card
    val wideRatio = 2.1f          // 2:1 - Wide info card
    val tallRatio = 0.5f          // 1:2 - Tall status card
    val circleSize = 170.dp       // Circular gauges
    
    // Minimum heights
    val cardMinHeight = 140.dp
    val circleCardHeight = 170.dp
    val wideCardHeight = 100.dp
}

// Card Types for Dialer
enum class CardType {
    CIRCLE_GAUGE,    // Circular progress (call stats, spam blocked %)
    SQUARE_INFO,     // Square info card (quick contact, recent call)
    WIDE_BANNER,     // Wide card (caller ID, call in progress)
    TALL_LIST,       // Tall card (recent calls mini-list)
    DIAL_PAD         // Special dial pad layout
}
```

### Grid Layout Examples

```
┌──────────────────────────────┐
│  [  Recent Calls (WIDE)   ]  │
├──────────────┬───────────────┤
│              │               │
│   (CIRCLE)   │   [SQUARE]    │
│  Call Stats  │ Quick Contact │
│              │               │
├──────────────┼───────────────┤
│              │               │
│   [SQUARE]   │   (CIRCLE)    │
│   Favorite   │   Blocked %   │
│              │               │
├──────────────┴───────────────┤
│                              │
│    [   Dial Pad Widget  ]    │
│                              │
└──────────────────────────────┘
```

---

## 🧩 Component Library

### Widget Cards

#### Circular Card (NothingCircleCard)
```kotlin
@Composable
fun NothingCircleCard(
    value: String,              // Main value (e.g., "72%")
    label: String,              // Description label
    progress: Float = 0f,       // 0-1 for arc progress
    progressColor: Color,       // Arc color
    icon: ImageVector? = null,  // Optional center icon
    onClick: () -> Unit = {}
)
```

#### Square Card (NothingSquareCard)
```kotlin
@Composable
fun NothingSquareCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconBackgroundColor: Color = NothingRed,
    onClick: () -> Unit = {}
)
```

#### Wide Banner Card (NothingBannerCard)
```kotlin
@Composable
fun NothingBannerCard(
    title: String,
    subtitle: String,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
)
```

### Dial Pad

```kotlin
@Composable
fun NothingDialPad(
    onDigitPressed: (Char) -> Unit,
    onBackspace: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 4x3 grid with dot matrix styled buttons
    // Long press for + on 0
    // Long press for voicemail on 1
    // T9 letter labels under numbers
}
```

### Dial Button

```kotlin
@Composable
fun NothingDialButton(
    digit: Char,
    letters: String = "",      // T9 letters (e.g., "ABC" for 2)
    onPress: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Dot matrix number
    // Subtle letters below
    // Ripple effect with haptic
}
```

### Contact Item

```kotlin
@Composable
fun NothingContactItem(
    name: String,
    phoneNumber: String,
    avatar: Any?,              // Bitmap, Uri, or null for initials
    isOnline: Boolean = false,
    lastCallTime: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
)
```

### Call Log Item

```kotlin
@Composable
fun NothingCallLogItem(
    contact: Contact?,
    phoneNumber: String,
    callType: CallType,        // INCOMING, OUTGOING, MISSED, BLOCKED
    timestamp: Long,
    duration: Long,
    simSlot: Int? = null,
    onClick: () -> Unit,
    onCallClick: () -> Unit
)
```

### Search Bar

```kotlin
@Composable
fun NothingSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    leadingIcon: ImageVector = Icons.Default.Search,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null
)
```

### Bottom Navigation

```kotlin
@Composable
fun NothingBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<BottomNavItem>  // icon, selectedIcon, label
)
```

### Floating Action Button

```kotlin
@Composable
fun NothingFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    extended: Boolean = false,
    text: String = ""
)
```

---

## ✨ Animations & Transitions

### Timing

```kotlin
object NothingMotion {
    // Duration
    val instant = 50
    val fast = 150
    val normal = 300
    val slow = 450
    val dramatic = 600
    
    // Easing
    val easeOut = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val easeIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val easeInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val bounce = CubicBezierEasing(0.68f, -0.55f, 0.265f, 1.55f)
}
```

### Key Animations

| Element | Animation | Duration | Notes |
|---------|-----------|----------|-------|
| Card appear | Scale + Fade | 300ms | Staggered per card |
| Card press | Scale down | 100ms | Scale to 0.96 |
| Dial button | Scale + Ripple | 150ms | Haptic feedback |
| Call button | Pulse glow | Continuous | Breathing effect |
| Screen transition | Shared element | 350ms | Cross-fade with slide |
| Number typing | Character pop | 50ms | Each digit animates in |
| Progress arc | Draw arc | 500ms | Animated on appear |
| Incoming call | Glyph pulse | 1000ms | Synced with ringtone |

### Dot Matrix Animation

```kotlin
@Composable
fun DotMatrixText(
    text: String,
    fontSize: TextUnit,
    animateIn: Boolean = false,
    scrolling: Boolean = false    // For long text marquee
)

// Animation: Each dot appears with slight delay
// Creates typewriter/digital display effect
```

---

## 📱 Screen Specifications

### Status Bar
- **Dark Theme**: Transparent, light icons
- **Light Theme**: Transparent, dark icons
- **In Call**: Colored bar with call indicator

### Navigation Bar
- **Style**: Gesture navigation / 3-button compatible
- **Color**: Matches background (edge-to-edge)
- **Safe area**: Proper insets handling

### Safe Areas
```kotlin
val windowInsets = WindowInsets.systemBars
val displayCutout = WindowInsets.displayCutout
// Handle punch-hole camera on Nothing Phone
```

---

## 🎭 Iconography

### Icon Style
- **Line weight**: 1.5-2dp stroke
- **Style**: Outlined (not filled) for nav, filled for actions
- **Size**: 24dp standard, 20dp small, 32dp large
- **Color**: Mono by default, red for accent/danger

### Custom Icons Needed
```kotlin
// Dialer specific
val GlyphPhone       // Phone with Glyph lines
val DotMatrixCall    // Retro phone icon
val SpamShield       // Block icon with shield
val RecordingWave    // Audio waveform
val T9Keyboard       // T9 pad icon
val SimDual          // Dual SIM indicator
val HDVoice          // HD badge
val WifiCall         // Wi-Fi calling
```

---

## 🖼️ Imagery

### Contact Avatars
- **Shape**: Circle with Nothing ring border
- **Fallback**: Initials on colored background
- **Online indicator**: Green dot, bottom-right
- **Size variants**: 32dp, 40dp, 48dp, 64dp, 96dp

### Placeholder Images
- **Style**: Dot matrix silhouette
- **Color**: Muted gray on dark background

---

## 🔊 Haptics

```kotlin
object NothingHaptics {
    // Dial pad press
    val digitPress = HapticFeedbackType.LongPress
    
    // Button tap
    val buttonTap = HapticFeedbackType.LongPress
    
    // Success action
    val success = HapticFeedbackType.LongPress  // Custom: two pulses
    
    // Error/blocked call
    val error = HapticFeedbackType.LongPress    // Custom: buzz pattern
    
    // Incoming call
    val incomingRing = // Synced with Glyph pattern
}
```

---

## 🌐 Responsive Design

### Breakpoints
```kotlin
object NothingBreakpoints {
    val compact = 0.dp..599.dp        // Phones portrait
    val medium = 600.dp..839.dp       // Phones landscape, small tablets
    val expanded = 840.dp..Int.MAX_VALUE.dp  // Tablets, foldables
}
```

### Adaptive Layouts
- **Compact**: Single column, bottom nav
- **Medium**: Two-pane, side nav
- **Expanded**: Three-pane, rail nav
