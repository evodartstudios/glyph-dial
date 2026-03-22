# Glyph Dial - Components Library

> Complete catalog of reusable UI components for the Nothing Dialer app

---

## 📐 Base Components

### NothingCard

The fundamental building block for the Nothing UI grid system.

```kotlin
@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(NothingRadius.lg),
    backgroundColor: Color = NothingColors.SurfaceCard,
    borderColor: Color = Color.Transparent,
    borderWidth: Dp = 0.dp,
    elevation: Dp = NothingElevation.none,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
```

**Variants:**
- `NothingSquareCard` - 1:1 aspect ratio info card
- `NothingCircleCard` - Circular gauge card
- `NothingBannerCard` - Wide 2:1 ratio card
- `NothingTallCard` - Tall 1:2 ratio card

---

### NothingCircleCard

Circular progress/gauge card inspired by Nothing Weather.

```kotlin
@Composable
fun NothingCircleCard(
    modifier: Modifier = Modifier,
    value: String,                    // "72%" or "47"
    label: String,                    // "Blocked" or "Calls"
    progress: Float = 0f,             // 0.0 to 1.0
    progressColor: Color = NothingColors.NothingRed,
    trackColor: Color = NothingColors.Gray,
    progressStrokeWidth: Dp = 8.dp,
    icon: ImageVector? = null,        // Center icon
    iconSize: Dp = 24.dp,
    animateProgress: Boolean = true,
    onClick: (() -> Unit)? = null
)
```

**Use Cases:**
- Spam blocked percentage
- Weekly call count
- Battery during call
- Signal strength

---

### NothingSquareCard

Standard square information card.

```kotlin
@Composable
fun NothingSquareCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconBackgroundColor: Color = NothingColors.NothingRed,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
)
```

**Use Cases:**
- Quick contact tiles
- Recent call summary
- Settings shortcuts
- Feature toggles

---

### NothingBannerCard

Wide horizontal card for primary information.

```kotlin
@Composable
fun NothingBannerCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = NothingColors.SurfaceCard,
    onClick: (() -> Unit)? = null
)
```

**Use Cases:**
- Caller ID display
- Active call banner
- Missed call summary
- Tip/hint banners

---

## 📱 Dial Pad Components

### NothingDialPad

Complete T9-style dial pad.

```kotlin
@Composable
fun NothingDialPad(
    modifier: Modifier = Modifier,
    onDigitPressed: (Char) -> Unit,
    onDigitLongPressed: ((Char) -> Unit)? = null,
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,  // Clear all
    enabled: Boolean = true,
    hapticEnabled: Boolean = true
)
```

---

### NothingDialButton

Individual dial pad button.

```kotlin
@Composable
fun NothingDialButton(
    modifier: Modifier = Modifier,
    digit: Char,
    letters: String = "",             // "ABC" for 2
    size: Dp = 72.dp,
    onPress: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    enabled: Boolean = true,
    hapticEnabled: Boolean = true
)

// Digit to letters mapping
val T9_LETTERS = mapOf(
    '1' to "  ",
    '2' to "ABC",
    '3' to "DEF",
    '4' to "GHI",
    '5' to "JKL",
    '6' to "MNO",
    '7' to "PQRS",
    '8' to "TUV",
    '9' to "WXYZ",
    '*' to "",
    '0' to "+",
    '#' to ""
)
```

---

### PhoneNumberDisplay

Dot matrix styled phone number input display.

```kotlin
@Composable
fun PhoneNumberDisplay(
    modifier: Modifier = Modifier,
    number: String,
    placeholder: String = "Enter number",
    formatted: Boolean = true,
    countryCode: String? = null,
    fontSize: TextUnit = 32.sp,
    animateInput: Boolean = true
)
```

---

### CallActionBar

Bottom action bar with SIM selection and call button.

```kotlin
@Composable
fun CallActionBar(
    modifier: Modifier = Modifier,
    number: String,
    onCall: (simSlot: Int?) -> Unit,
    onBackspace: () -> Unit,
    onBackspaceLongPress: () -> Unit,
    dualSimEnabled: Boolean = false,
    sim1Name: String = "SIM 1",
    sim2Name: String = "SIM 2",
    defaultSim: Int? = null
)
```

---

## 👤 Contact Components

### NothingContactItem

List item for contact display.

```kotlin
@Composable
fun NothingContactItem(
    modifier: Modifier = Modifier,
    name: String,
    phoneNumber: String? = null,
    photoUri: Uri? = null,
    initials: String? = null,          // Fallback if no photo
    isOnline: Boolean = false,
    subtitle: String? = null,          // Company, label, etc.
    trailing: @Composable (() -> Unit)? = null,  // Call button, etc.
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
)
```

---

### NothingContactAvatar

Contact avatar with Nothing-style ring.

```kotlin
@Composable
fun NothingContactAvatar(
    modifier: Modifier = Modifier,
    photoUri: Uri? = null,
    initials: String,
    size: Dp = 48.dp,
    showOnlineIndicator: Boolean = false,
    ringColor: Color = NothingColors.Gray,
    ringWidth: Dp = 2.dp,
    onClick: (() -> Unit)? = null
)
```

**Sizes:**
- `small` - 32.dp (list items, chips)
- `medium` - 48.dp (standard list)
- `large` - 64.dp (details header)
- `xlarge` - 96.dp (call screens)

---

### ContactQuickActions

Quick action buttons for contact.

```kotlin
@Composable
fun ContactQuickActions(
    modifier: Modifier = Modifier,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onVideoCall: (() -> Unit)? = null,
    onEmail: (() -> Unit)? = null,
    arrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly
)
```

---

### AlphabetIndexer

Fast scroll alphabetic indexer.

```kotlin
@Composable
fun AlphabetIndexer(
    modifier: Modifier = Modifier,
    letters: List<Char> = ('A'..'Z').toList() + '#',
    selectedLetter: Char?,
    onLetterSelected: (Char) -> Unit
)
```

---

### FavoriteContactCard

Large contact card for favorites grid.

```kotlin
@Composable
fun FavoriteContactCard(
    modifier: Modifier = Modifier,
    name: String,
    photoUri: Uri? = null,
    initials: String,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onRemove: () -> Unit,
    onLongClick: () -> Unit
)
```

---

## 📞 Call History Components

### NothingCallLogItem

Call history list item.

```kotlin
@Composable
fun NothingCallLogItem(
    modifier: Modifier = Modifier,
    callerName: String?,
    phoneNumber: String,
    callType: CallType,               // INCOMING, OUTGOING, MISSED, BLOCKED, SPAM
    timestamp: Long,
    duration: Long,                   // In seconds, 0 for missed
    simSlot: Int? = null,
    isSpam: Boolean = false,
    groupedCount: Int = 1,            // If multiple consecutive calls
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onSwipeToDelete: (() -> Unit)? = null,
    onSwipeToBlock: (() -> Unit)? = null
)

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    BLOCKED,
    REJECTED,
    VOICEMAIL
}
```

---

### CallLogSectionHeader

Date section divider.

```kotlin
@Composable
fun CallLogSectionHeader(
    modifier: Modifier = Modifier,
    title: String,                    // "Today", "Yesterday", "December 10"
    count: Int? = null
)
```

---

### CallFilterChips

Filter chips for call type filtering.

```kotlin
@Composable
fun CallFilterChips(
    modifier: Modifier = Modifier,
    selectedFilter: CallFilter,
    onFilterSelected: (CallFilter) -> Unit,
    missedCount: Int = 0              // Badge on Missed chip
)

enum class CallFilter {
    ALL,
    MISSED,
    INCOMING,
    OUTGOING,
    BLOCKED
}
```

---

## 📲 Call Screen Components

### CallerInfoCard

Large caller display for call screens.

```kotlin
@Composable
fun CallerInfoCard(
    modifier: Modifier = Modifier,
    name: String?,
    phoneNumber: String,
    photoUri: Uri? = null,
    label: String? = null,            // "Mobile", "Work"
    simSlot: Int? = null,
    isSpam: Boolean = false,
    spamConfidence: Float? = null,    // 0.0 to 1.0
    avatarSize: Dp = 96.dp
)
```

---

### DotMatrixTimer

Retro dot-matrix style call timer.

```kotlin
@Composable
fun DotMatrixTimer(
    modifier: Modifier = Modifier,
    durationSeconds: Long,
    fontSize: TextUnit = 48.sp,
    color: Color = NothingColors.PureWhite,
    animateSeconds: Boolean = true
)

// Display format: "05:23" or "1:05:23" for long calls
```

---

### CallStatusIndicator

Call state display (Calling..., Ringing..., etc.)

```kotlin
@Composable
fun CallStatusIndicator(
    modifier: Modifier = Modifier,
    status: CallStatus,
    animated: Boolean = true
)

enum class CallStatus {
    DIALING,
    RINGING,
    CONNECTING,
    CONNECTED,
    ON_HOLD,
    ENDED
}
```

---

### InCallControlButton

In-call action button.

```kotlin
@Composable
fun InCallControlButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    activeColor: Color = NothingColors.NothingRed,
    enabled: Boolean = true,
    onClick: () -> Unit
)
```

---

### InCallControlGrid

Grid of in-call controls.

```kotlin
@Composable
fun InCallControlGrid(
    modifier: Modifier = Modifier,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isOnHold: Boolean,
    isRecording: Boolean,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onHoldToggle: () -> Unit,
    onRecordToggle: () -> Unit,
    onKeypadClick: () -> Unit,
    onAddCallClick: () -> Unit
)
```

---

### CallActionButton

Large call action button (Accept/Decline).

```kotlin
@Composable
fun CallActionButton(
    modifier: Modifier = Modifier,
    type: CallActionType,
    size: Dp = 72.dp,
    onClick: () -> Unit
)

enum class CallActionType {
    ACCEPT,           // Green, phone icon
    DECLINE,          // Red, end icon
    END_CALL,         // Red, end icon
    ANSWER_VIDEO,     // Green, video icon
    ANSWER_SPEAKER    // Green, speaker icon
}
```

---

## ⚙️ Settings Components

### SettingsSection

Grouped settings container.

```kotlin
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
)
```

---

### SettingItem

Base setting item.

```kotlin
@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
)
```

---

### SettingSwitch

Toggle setting.

```kotlin
@Composable
fun SettingSwitch(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
)
```

---

### SettingSelector

Dropdown/dialog picker setting.

```kotlin
@Composable
fun SettingSelector(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    selectedValue: String,
    onClick: () -> Unit
)
```

---

## 🔍 Search Components

### NothingSearchBar

Expandable search bar.

```kotlin
@Composable
fun NothingSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    leadingIcon: ImageVector = Icons.Default.Search,
    onClear: () -> Unit,
    onBack: (() -> Unit)? = null,
    expanded: Boolean = false,
    onExpandChange: ((Boolean) -> Unit)? = null
)
```

---

### SearchResultSection

Search results grouping.

```kotlin
@Composable
fun SearchResultSection(
    modifier: Modifier = Modifier,
    title: String,                    // "Contacts", "Call History"
    count: Int,
    content: @Composable () -> Unit
)
```

---

## 🧭 Navigation Components

### NothingBottomNav

Bottom navigation bar.

```kotlin
@Composable
fun NothingBottomNav(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<BottomNavItem>
)

data class BottomNavItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val badge: Int? = null             // Notification count
)
```

---

### NothingTopBar

App bar with Nothing styling.

```kotlin
@Composable
fun NothingTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
)
```

---

### NothingFab

Floating action button.

```kotlin
@Composable
fun NothingFab(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    extended: Boolean = false,
    text: String = "",
    containerColor: Color = NothingColors.NothingRed,
    contentColor: Color = NothingColors.PureWhite
)
```

---

## 🎨 Common Components

### NothingButton

Primary button.

```kotlin
@Composable
fun NothingButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.PRIMARY
)

enum class ButtonVariant {
    PRIMARY,          // Red filled
    SECONDARY,        // Outlined
    GHOST,            // Text only
    DANGER            // Red for destructive
}
```

---

### NothingChip

Filter/selection chip.

```kotlin
@Composable
fun NothingChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    badge: Int? = null
)
```

---

### NothingTextField

Text input field.

```kotlin
@Composable
fun NothingTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
)
```

---

### NothingSwitch

Toggle switch.

```kotlin
@Composable
fun NothingSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    activeColor: Color = NothingColors.NothingRed
)
```

---

### NothingDialog

Modal dialog.

```kotlin
@Composable
fun NothingDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    content: @Composable (() -> Unit)? = null
)
```

---

### NothingBottomSheet

Bottom sheet.

```kotlin
@Composable
fun NothingBottomSheet(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
)
```

---

### NothingSnackbar

Snackbar notification.

```kotlin
@Composable
fun NothingSnackbar(
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    type: SnackbarType = SnackbarType.DEFAULT
)

enum class SnackbarType {
    DEFAULT,
    SUCCESS,
    ERROR,
    WARNING
}
```

---

## 🎬 Animation Components

### DotMatrixText

Animated dot matrix text display.

```kotlin
@Composable
fun DotMatrixText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 24.sp,
    color: Color = NothingColors.PureWhite,
    animateIn: Boolean = false,
    animationDelay: Long = 50,         // Per character
    scrolling: Boolean = false,        // Marquee effect
    scrollSpeed: Long = 100
)
```

---

### PulsingGlow

Pulsing glow effect for buttons.

```kotlin
@Composable
fun PulsingGlow(
    modifier: Modifier = Modifier,
    color: Color = NothingColors.NothingRed,
    pulseSpeed: Long = 1000,
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 0.8f,
    content: @Composable () -> Unit
)
```

---

### RippleButton

Button with enhanced ripple effect.

```kotlin
@Composable
fun RippleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    rippleColor: Color = NothingColors.NothingRed,
    enabled: Boolean = true,
    content: @Composable () -> Unit
)
```

---

### AnimatedCounter

Animated number counter.

```kotlin
@Composable
fun AnimatedCounter(
    modifier: Modifier = Modifier,
    count: Int,
    duration: Int = 300,
    fontSize: TextUnit = 48.sp,
    color: Color = NothingColors.PureWhite
)
```

---

## 📊 Data Display Components

### ProgressArc

Circular progress arc.

```kotlin
@Composable
fun ProgressArc(
    modifier: Modifier = Modifier,
    progress: Float,                   // 0.0 to 1.0
    color: Color = NothingColors.NothingRed,
    trackColor: Color = NothingColors.Gray,
    strokeWidth: Dp = 8.dp,
    startAngle: Float = 135f,
    sweepAngle: Float = 270f,
    animated: Boolean = true,
    animationDuration: Int = 500
)
```

---

### StatCard

Statistics display card.

```kotlin
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trend: StatTrend? = null,
    trendValue: String? = null,
    icon: ImageVector? = null
)

enum class StatTrend {
    UP,
    DOWN,
    NEUTRAL
}
```

---

### BarChart

Simple bar chart for call patterns.

```kotlin
@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<Float>,                 // Normalized 0-1 values
    barColor: Color = NothingColors.NothingRed,
    labels: List<String>? = null,
    animated: Boolean = true
)
```

---

## 🔔 Notification Components

### SpamIndicator

Spam warning badge.

```kotlin
@Composable
fun SpamIndicator(
    modifier: Modifier = Modifier,
    confidence: Float,                 // 0.0 to 1.0
    showLabel: Boolean = true
)
```

---

### CallBadge

Call type/status badge.

```kotlin
@Composable
fun CallBadge(
    modifier: Modifier = Modifier,
    type: BadgeType,
    text: String? = null
)

enum class BadgeType {
    HD_VOICE,
    VOLTE,
    WIFI_CALL,
    RECORDING,
    SIM1,
    SIM2,
    VERIFIED,
    SPAM
}
```

---

### EmptyState

Empty state placeholder.

```kotlin
@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
)
```
