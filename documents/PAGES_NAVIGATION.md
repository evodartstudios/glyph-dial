# Glyph Dial - Pages & Navigation Structure

> Complete screen hierarchy and navigation architecture

---

## 🗺️ Navigation Overview

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│                    BOTTOM NAVIGATION                        │
│  ┌──────────┬──────────┬──────────┬──────────┬──────────┐   │
│  │ Dial Pad │ Recents  │ Contacts │ Favorites│ Settings │   │
│  └──────────┴──────────┴──────────┴──────────┴──────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📱 Main Screens

### 1. Dial Pad Screen (Home)
**Route:** `/dial`

```
┌─────────────────────────────┐
│ ☰  Glyph Dial        🔍    │
├─────────────────────────────┤
│                             │
│     [Phone Number Field]    │
│         +91 98765 43210     │
│                             │
├─────────────────────────────┤
│ Suggestions from T9 Search  │
│ ┌─────────────────────────┐ │
│ │ 👤 John Doe        📞   │ │
│ │ 👤 Jane Smith      📞   │ │
│ └─────────────────────────┘ │
├─────────────────────────────┤
│                             │
│    [1]    [2]    [3]        │
│            ABC    DEF       │
│                             │
│    [4]    [5]    [6]        │
│    GHI    JKL    MNO        │
│                             │
│    [7]    [8]    [9]        │
│   PQRS    TUV   WXYZ        │
│                             │
│    [✱]    [0]    [#]        │
│            +                │
│                             │
├─────────────────────────────┤
│  [SIM1]  [📞 CALL]  [SIM2]  │
│           ⌫ Backspace       │
└─────────────────────────────┘
```

**Components:**
- `NothingAppBar` - Title, hamburger menu, search icon
- `PhoneNumberDisplay` - Dot matrix styled input display
- `T9SuggestionList` - Contact suggestions from typing
- `NothingDialPad` - 3x4 dial pad with T9 letters
- `CallActionBar` - SIM selectors, call button, backspace

**Features:**
- Long press 0 for "+"
- Long press 1 for voicemail
- Real-time T9 contact search
- Copy/paste phone numbers
- Smart formatting by country
- Dual SIM call buttons

---

### 2. Call History / Recents Screen
**Route:** `/recents`

```
┌─────────────────────────────┐
│ ☰  Recents           🔍 ⋮  │
├─────────────────────────────┤
│ [All] [Missed] [Incoming]   │
│       [Outgoing] [Blocked]  │
├─────────────────────────────┤
│ TODAY                       │
│ ┌─────────────────────────┐ │
│ │ 👤 John Doe         📞  │ │
│ │ ↗️ Outgoing • 2:34 • 5m │ │
│ ├─────────────────────────┤ │
│ │ 👤 Unknown Number   📞  │ │
│ │ ↙️ ❌ Missed • 3 calls  │ │
│ ├─────────────────────────┤ │
│ │ 🚫 Spam Caller      ℹ️  │ │
│ │ 🛑 Blocked • 4:15pm    │ │
│ └─────────────────────────┘ │
│                             │
│ YESTERDAY                   │
│ ┌─────────────────────────┐ │
│ │ 👤 Jane Smith       📞  │ │
│ │ ↙️ Incoming • 45m      │ │
│ └─────────────────────────┘ │
│                             │
│              (+) New Call   │
└─────────────────────────────┘
```

**Components:**
- `NothingAppBar` - With search and menu
- `CallFilterChips` - Filter by call type
- `CallLogSectionHeader` - Date grouping
- `NothingCallLogItem` - Call entry with swipe actions
- `NothingFab` - New call FAB

**Features:**
- Grouped by time (Today, Yesterday, This Week, etc.)
- Swipe right to call back
- Swipe left to delete/block
- Tap for call details
- Long press for multi-select
- Group consecutive calls

---

### 3. Contacts Screen
**Route:** `/contacts`

```
┌─────────────────────────────┐
│ ☰  Contacts          🔍 ⋮  │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ 🔍 Search contacts...   │ │
│ └─────────────────────────┘ │
├─────────────────────────────┤
│ FREQUENT                    │
│ ┌───┐ ┌───┐ ┌───┐ ┌───┐     │
│ │JD │ │JS │ │MK │ │+5 │     │
│ └───┘ └───┘ └───┘ └───┘     │
├─────────────────────────────┤
│ A                         ▲ │
│ ┌─────────────────────────┐ │
│ │ 👤 Adam Smith       📞  │ │
│ │ 👤 Alice Johnson    📞  │ │
│ │ 👤 Andrew Brown     📞  │ │
│ └─────────────────────────┘ │
│ B                         │ │
│ ┌─────────────────────────┐ │
│ │ 👤 Bob Williams     📞  │ │
│ └─────────────────────────┘ │
│                           ▼ │
│              (+) Add Contact│
└─────────────────────────────┘
```

**Components:**
- `NothingSearchBar` - Inline search
- `FrequentContactsRow` - Horizontal scroll of avatars
- `AlphabetIndexer` - Side scrubber A-Z
- `ContactSectionHeader` - Letter divider
- `NothingContactItem` - Contact row
- `NothingFab` - Add contact

**Features:**
- Alphabetically sorted with fast scroll
- Search by name, number, company
- Frequent contacts quick access
- Long press for quick actions
- Avatar with online indicator

---

### 4. Favorites Screen
**Route:** `/favorites`

```
┌─────────────────────────────┐
│ ☰  Favorites         🔍 ⋮  │
├─────────────────────────────┤
│                             │
│  ┌─────────┐  ┌─────────┐   │
│  │         │  │         │   │
│  │  👤 JD  │  │  👤 JS  │   │
│  │  John   │  │  Jane   │   │
│  │   Doe   │  │  Smith  │   │
│  │  📞 💬  │  │  📞 💬  │   │
│  └─────────┘  └─────────┘   │
│                             │
│  ┌─────────┐  ┌─────────┐   │
│  │         │  │         │   │
│  │  👤 MK  │  │  👤 AB  │   │
│  │  Mike   │  │  Alice  │   │
│  │   King  │  │  Baker  │   │
│  │  📞 💬  │  │  📞 💬  │   │
│  └─────────┘  └─────────┘   │
│                             │
│  ┌─────────────────────────┐│
│  │  + Add Favorite         ││
│  └─────────────────────────┘│
│                             │
└─────────────────────────────┘
```

**Components:**
- `NothingAppBar`
- `FavoriteContactGrid` - 2-column grid of favorites
- `FavoriteContactCard` - Large contact card with actions
- `AddFavoriteCard` - Placeholder to add new

**Features:**
- Grid layout with large avatars
- Quick call/message buttons
- Drag to reorder
- Long press to remove
- Empty state with "Add Favorites" prompt

---

### 5. Settings Screen
**Route:** `/settings`

```
┌─────────────────────────────┐
│ ←  Settings                 │
├─────────────────────────────┤
│ DISPLAY                     │
│ ┌─────────────────────────┐ │
│ │ Theme              Dark │ │
│ │ Accent Color        Red │ │
│ │ Font Size        Medium │ │
│ └─────────────────────────┘ │
│                             │
│ CALLS                       │
│ ┌─────────────────────────┐ │
│ │ Default SIM       SIM 1 │ │
│ │ Call Recording      Off │ │
│ │ Speed Dial          →   │ │
│ │ Voicemail           →   │ │
│ └─────────────────────────┘ │
│                             │
│ BLOCKING                    │
│ ┌─────────────────────────┐ │
│ │ Block Unknown       On  │ │
│ │ Blocked Numbers     →   │ │
│ │ Spam Protection     On  │ │
│ └─────────────────────────┘ │
│                             │
│ GLYPH (Nothing Phone)       │
│ ┌─────────────────────────┐ │
│ │ Glyph for Calls     On  │ │
│ │ Custom Patterns     →   │ │
│ └─────────────────────────┘ │
│                             │
│ ABOUT                       │
│ ┌─────────────────────────┐ │
│ │ Version         1.0.0   │ │
│ │ Privacy Policy      →   │ │
│ │ Licenses            →   │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

**Components:**
- `NothingTopBar` - With back navigation
- `SettingsSection` - Grouped settings
- `SettingItem` - Toggle/chevron/value
- `SettingSwitch` - Toggle setting
- `SettingSelector` - Dropdown setting

---

## 📞 Call Screens

### 6. Incoming Call Screen
**Route:** `/call/incoming`

```
┌─────────────────────────────┐
│                             │
│                             │
│         ┌───────┐           │
│         │       │           │
│         │  👤   │           │
│         │       │           │
│         └───────┘           │
│                             │
│        John Doe             │
│     +91 98765 43210         │
│                             │
│       Mobile • SIM 1        │
│                             │
│    ⚠️ Possible Spam         │
│                             │
│                             │
│                             │
│                             │
│                             │
│  ┌─────┐    ┌─────────────┐ │
│  │ 💬  │    │  Quick SMS  │ │
│  └─────┘    └─────────────┘ │
│                             │
│   ┌─────┐        ┌─────┐    │
│   │  🔴 │        │  🟢 │    │
│   │Decline│      │Accept│   │
│   └─────┘        └─────┘    │
│                             │
│  ──── Swipe up to answer ───│
└─────────────────────────────┘
```

**Components:**
- `CallerAvatar` - Large centered avatar
- `CallerInfo` - Name, number, type
- `SpamIndicator` - If suspected spam
- `QuickReplyButton` - SMS quick reply
- `CallActionButtons` - Accept/Decline
- `SwipeToAnswer` - Gesture hint

**Features:**
- Full screen overlay
- Glyph animation sync
- Quick reply with templates
- Reminder option
- Block option

---

### 7. Outgoing Call Screen
**Route:** `/call/outgoing`

```
┌─────────────────────────────┐
│                             │
│                             │
│         ┌───────┐           │
│         │       │           │
│         │  👤   │           │
│         │       │           │
│         └───────┘           │
│                             │
│        John Doe             │
│     +91 98765 43210         │
│                             │
│        Calling...           │
│          ●●●                │
│                             │
│       Using SIM 1           │
│         VoLTE HD            │
│                             │
│                             │
│                             │
│                             │
│                             │
│                             │
│                             │
│         ┌─────┐             │
│         │  🔴 │             │
│         │ End │             │
│         └─────┘             │
│                             │
└─────────────────────────────┘
```

**Components:**
- `CallerAvatar`
- `CallerInfo`
- `CallStatusIndicator` - Calling.../Ringing...
- `SimStatusBadge` - SIM and VoLTE info
- `EndCallButton` - Big red end button

---

### 8. Active Call Screen
**Route:** `/call/active`

```
┌─────────────────────────────┐
│                             │
│         ┌───────┐           │
│         │       │           │
│         │  👤   │           │
│         │       │           │
│         └───────┘           │
│                             │
│        John Doe             │
│                             │
│      ██ 05:23 ██            │
│    (Dot Matrix Timer)       │
│                             │
│      🔊 HD • SIM 1          │
│                             │
├─────────────────────────────┤
│                             │
│  ┌─────┐ ┌─────┐ ┌─────┐    │
│  │ 🔇  │ │ ⌨️  │ │ 🔊  │    │
│  │Mute │ │Keypad│ │Speaker│  │
│  └─────┘ └─────┘ └─────┘    │
│                             │
│  ┌─────┐ ┌─────┐ ┌─────┐    │
│  │ ➕  │ │ ⏸️  │ │ 🎙️  │    │
│  │ Add │ │Hold │ │Record│   │
│  └─────┘ └─────┘ └─────┘    │
│                             │
│         ┌─────┐             │
│         │  🔴 │             │
│         │ End │             │
│         └─────┘             │
│                             │
└─────────────────────────────┘
```

**Components:**
- `CallerAvatar`
- `CallerName`
- `DotMatrixTimer` - Call duration in dot matrix
- `CallQualityBadge` - HD, VoLTE, etc.
- `InCallControlGrid` - 3x2 control buttons
- `EndCallButton`

**Features:**
- Mute toggle
- Speaker toggle
- Show dial pad
- Add call (conference)
- Hold
- Record (with legal notice)

---

### 9. Call Ended Screen
**Route:** `/call/ended`

```
┌─────────────────────────────┐
│                             │
│         ┌───────┐           │
│         │       │           │
│         │  👤   │           │
│         │       │           │
│         └───────┘           │
│                             │
│        John Doe             │
│                             │
│      Call Ended             │
│       05:23                 │
│                             │
├─────────────────────────────┤
│                             │
│  ┌─────┐ ┌─────┐ ┌─────┐    │
│  │ 📞  │ │ 💬  │ │ ℹ️  │    │
│  │Call │ │ SMS │ │Details│  │
│  │Again│ │     │ │      │   │
│  └─────┘ └─────┘ └─────┘    │
│                             │
│  ┌─────────────────────────┐│
│  │ ⭐ Rate call quality   ││
│  │ ★☆☆☆☆                  ││
│  └─────────────────────────┘│
│                             │
│         [Done]              │
│                             │
└─────────────────────────────┘
```

**Components:**
- `CallerAvatar`
- `CallerName`
- `CallSummary` - Duration, time
- `PostCallActions` - Call again, SMS, details
- `CallQualityRating` - Optional quality feedback
- `DoneButton`

---

## 👤 Contact Screens

### 10. Contact Details Screen
**Route:** `/contact/{id}`

```
┌─────────────────────────────┐
│ ←                     ⋮     │
├─────────────────────────────┤
│                             │
│         ┌───────┐           │
│         │       │           │
│         │  👤   │           │
│         │       │           │
│         └───────┘           │
│        John Doe             │
│    Company Name, Title      │
│                             │
│  ┌─────┐ ┌─────┐ ┌─────┐    │
│  │ 📞  │ │ 💬  │ │ 📹  │    │
│  │Call │ │ SMS │ │Video│    │
│  └─────┘ └─────┘ └─────┘    │
├─────────────────────────────┤
│ PHONE NUMBERS               │
│ ┌─────────────────────────┐ │
│ │ 📱 Mobile               │ │
│ │ +91 98765 43210    📞💬 │ │
│ ├─────────────────────────┤ │
│ │ ☎️ Work                 │ │
│ │ +91 11234 56789    📞💬 │ │
│ └─────────────────────────┘ │
│                             │
│ EMAIL                       │
│ ┌─────────────────────────┐ │
│ │ ✉️ john@email.com       │ │
│ └─────────────────────────┘ │
│                             │
│ ADDRESS                     │
│ ┌─────────────────────────┐ │
│ │ 🏠 123 Main Street...   │ │
│ └─────────────────────────┘ │
│                             │
│ NOTES                       │
│ ┌─────────────────────────┐ │
│ │ 📝 Personal notes...    │ │
│ └─────────────────────────┘ │
│                             │
│ CALL HISTORY                │
│ ┌─────────────────────────┐ │
│ │ ↗️ Today 2:30 PM • 5min │ │
│ │ ↙️ Yesterday • 15min    │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

**Components:**
- `ContactHeader` - Avatar, name, company
- `QuickActionRow` - Call, SMS, Video
- `ContactDetailSection` - Phone, email, address
- `ContactNotes` - Editable notes
- `ContactCallHistory` - Recent calls with this contact

**Features:**
- Edit contact
- Share contact
- Add to favorites
- Delete contact
- Block number
- Assign Glyph pattern
- Set ringtone

---

### 11. Edit Contact Screen
**Route:** `/contact/{id}/edit` or `/contact/new`

```
┌─────────────────────────────┐
│ ✕  Edit Contact      Save   │
├─────────────────────────────┤
│                             │
│         ┌───────┐           │
│         │  📷   │           │
│         │ + Add │           │
│         │ Photo │           │
│         └───────┘           │
│                             │
│ NAME                        │
│ ┌─────────────────────────┐ │
│ │ First Name              │ │
│ │ John                    │ │
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ Last Name               │ │
│ │ Doe                     │ │
│ └─────────────────────────┘ │
│                             │
│ PHONE                       │
│ ┌─────────────────────────┐ │
│ │ Mobile ▼                │ │
│ │ +91 98765 43210     ✕   │ │
│ └─────────────────────────┘ │
│      [+ Add Phone]          │
│                             │
│ EMAIL                       │
│ ┌─────────────────────────┐ │
│ │ Personal ▼              │ │
│ │ john@email.com      ✕   │ │
│ └─────────────────────────┘ │
│      [+ Add Email]          │
│                             │
│      [+ More Fields]        │
│                             │
└─────────────────────────────┘
```

**Components:**
- `NothingTopBar` - Cancel, Save
- `AvatarPicker` - Photo selection
- `NothingTextField` - Styled inputs
- `LabelSelector` - Dropdown for field type
- `AddFieldButton`

---

## 🎛️ Utility Screens

### 12. Search Screen
**Route:** `/search`

```
┌─────────────────────────────┐
│ ←  🔍 Search...             │
├─────────────────────────────┤
│ RECENT SEARCHES             │
│ ┌─────────────────────────┐ │
│ │ 🕐 John                 │ │
│ │ 🕐 +91 98765            │ │
│ │            [Clear All]  │ │
│ └─────────────────────────┘ │
│                             │
│ (Search results appear here)│
│                             │
│ CONTACTS                    │
│ ┌─────────────────────────┐ │
│ │ 👤 John Doe         📞  │ │
│ │ 👤 Johnny Smith     📞  │ │
│ └─────────────────────────┘ │
│                             │
│ CALL HISTORY                │
│ ┌─────────────────────────┐ │
│ │ John Doe • Today 2pm    │ │
│ │ Johnny • Yesterday      │ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
```

**Components:**
- `SearchInputBar` - Auto-focus
- `RecentSearches` - Clearable history
- `SearchResultsSection` - Contacts, Calls
- `NothingContactItem` / `NothingCallLogItem`

---

### 13. Blocked Numbers Screen
**Route:** `/settings/blocked`

```
┌─────────────────────────────┐
│ ←  Blocked Numbers          │
├─────────────────────────────┤
│ ┌─────────────────────────┐ │
│ │ 🔍 Search blocked...    │ │
│ └─────────────────────────┘ │
├─────────────────────────────┤
│ 3 blocked numbers           │
│                             │
│ ┌─────────────────────────┐ │
│ │ 🚫 +91 12345 67890      │ │
│ │ Blocked on Dec 10       │ │
│ │             [Unblock]   │ │
│ ├─────────────────────────┤ │
│ │ 🚫 Spam Caller          │ │
│ │ +91 99999 00000         │ │
│ │ Blocked on Dec 8        │ │
│ │             [Unblock]   │ │
│ └─────────────────────────┘ │
│                             │
│           (+) Add Number    │
└─────────────────────────────┘
```

---

### 14. Call Recording Library
**Route:** `/recordings`

```
┌─────────────────────────────┐
│ ←  Recordings        🔍 ⋮   │
├─────────────────────────────┤
│ DECEMBER 2024               │
│ ┌─────────────────────────┐ │
│ │ 🎙️ John Doe             │ │
│ │ Dec 10 • 5:23           │ │
│ │ ▶️ ─────────────── 📤    │ │
│ ├─────────────────────────┤ │
│ │ 🎙️ Jane Smith           │ │
│ │ Dec 8 • 12:45           │ │
│ │ ▶️ ─────────────── 📤    │ │
│ └─────────────────────────┘ │
│                             │
│ NOVEMBER 2024               │
│ ┌─────────────────────────┐ │
│ │ 🎙️ Unknown Number       │ │
│ │ Nov 30 • 2:15           │ │
│ │ ▶️ ─────────────── 📤    │ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
```

---

### 15. Speed Dial Settings
**Route:** `/settings/speed-dial`

```
┌─────────────────────────────┐
│ ←  Speed Dial               │
├─────────────────────────────┤
│ Long press dial pad keys    │
│ to quick dial               │
│                             │
│ ┌─────────────────────────┐ │
│ │ [1] → Voicemail     ✏️  │ │
│ │ [2] → John Doe      ✏️  │ │
│ │ [3] → Jane Smith    ✏️  │ │
│ │ [4] → Not assigned  +   │ │
│ │ [5] → Not assigned  +   │ │
│ │ [6] → Not assigned  +   │ │
│ │ [7] → Not assigned  +   │ │
│ │ [8] → Not assigned  +   │ │
│ │ [9] → Not assigned  +   │ │
│ └─────────────────────────┘ │
│                             │
│ Tip: 1 is always voicemail  │
│                             │
└─────────────────────────────┘
```

---

### 16. Statistics Dashboard
**Route:** `/stats`

```
┌─────────────────────────────┐
│ ←  Call Statistics          │
├─────────────────────────────┤
│ THIS WEEK                   │
│                             │
│  ┌──────────┐ ┌──────────┐  │
│  │          │ │          │  │
│  │   (47)   │ │  3:42    │  │
│  │ Calls    │ │ Avg Time │  │
│  │          │ │          │  │
│  └──────────┘ └──────────┘  │
│                             │
│  ┌──────────┐ ┌──────────┐  │
│  │  ↗️ 23   │ │  ↙️ 20   │  │
│  │ Outgoing │ │ Incoming │  │
│  │          │ │          │  │
│  │  ❌ 4    │ │  🛑 12   │  │
│  │ Missed   │ │ Blocked  │  │
│  └──────────┘ └──────────┘  │
│                             │
│ TOP CONTACTS                │
│ ┌─────────────────────────┐ │
│ │ 1. John Doe    15 calls │ │
│ │ 2. Jane Smith   8 calls │ │
│ │ 3. Mike King    6 calls │ │
│ └─────────────────────────┘ │
│                             │
│ CALL TIMES                  │
│ ┌─────────────────────────┐ │
│ │ ▁▃▅▇█▅▃▁▁▁▁▂▃▅▆▄▃▁▁▁▁▁ │ │
│ │ 6am        12pm      12am│ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
```

---

## 🧭 Navigation Architecture

### Navigation Graph

```kotlin
@Composable
fun GlyphDialNavigation() {
    NavHost(
        startDestination = "dial"
    ) {
        // Main screens with bottom nav
        composable("dial") { DialPadScreen() }
        composable("recents") { RecentsScreen() }
        composable("contacts") { ContactsScreen() }
        composable("favorites") { FavoritesScreen() }
        
        // Settings flow
        navigation(
            startDestination = "settings/main",
            route = "settings"
        ) {
            composable("settings/main") { SettingsScreen() }
            composable("settings/blocked") { BlockedNumbersScreen() }
            composable("settings/speed-dial") { SpeedDialScreen() }
            composable("settings/display") { DisplaySettingsScreen() }
            composable("settings/glyph") { GlyphSettingsScreen() }
        }
        
        // Contact flow
        composable("contact/{id}") { ContactDetailsScreen() }
        composable("contact/{id}/edit") { EditContactScreen() }
        composable("contact/new") { NewContactScreen() }
        
        // Call screens (usually full-screen dialogs)
        dialog("call/incoming") { IncomingCallScreen() }
        dialog("call/outgoing") { OutgoingCallScreen() }
        dialog("call/active") { ActiveCallScreen() }
        dialog("call/ended") { CallEndedScreen() }
        
        // Utilities
        composable("search") { SearchScreen() }
        composable("recordings") { RecordingsScreen() }
        composable("stats") { StatisticsScreen() }
    }
}
```

### Deep Links

| URI | Screen | Purpose |
|-----|--------|---------|
| `tel:{number}` | Dial Pad | Pre-fill number |
| `glyphdial://contact/{id}` | Contact Details | Open contact |
| `glyphdial://recents` | Recents | Show call history |
| `glyphdial://dial` | Dial Pad | Open dialer |

---

## 🔄 Screen Transitions

| From → To | Animation |
|-----------|-----------|
| Bottom Nav | Fade + Slide horizontal |
| List → Details | Shared element (avatar) |
| Press dial button | Bounce scale |
| Incoming call | Slide up + fade overlay |
| End call | Fade out + slide down |
| Search open | Slide down + expand |
