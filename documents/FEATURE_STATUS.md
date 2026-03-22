<!-- @format -->

# GlyphDial Feature Status

> Last Updated: December 17, 2024

## Feature Implementation Status

### ✅ Implemented & Working

| Feature                    | Status | Notes                              |
| -------------------------- | ------ | ---------------------------------- |
| **T9 Dialpad**             | ✅     | Full dialpad with T9 search        |
| **T9 Search**              | ✅     | Works from 1st character           |
| **Contacts List**          | ✅     | With search and alphabet scrollbar |
| **Recents List**           | ✅     | Grouped by date with search        |
| **Favorites Grid**         | ✅     | 2-column grid with photos          |
| **Contact Detail**         | ✅     | Slide-in overlay with actions      |
| **Call Detail**            | ✅     | Call info and actions              |
| **Multi-Page Settings**    | ✅     | 6 sub-screens with animations      |
| **Accent Color**           | ✅     | 8 colors, applies everywhere       |
| **Scrollbar Position**     | ✅     | Left/Right toggle                  |
| **Default Start Page**     | ✅     | Dropdown selection                 |
| **Recommendations Toggle** | ✅     | Show frequent contacts             |
| **Haptic Feedback**        | ✅     | On dialpad/nav taps                |
| **Edge-to-Edge UI**        | ✅     | Full screen design                 |
| **InCallService**          | ✅     | Service with call state management |
| **InCallActivity**         | ✅     | Custom call UI over lock screen    |
| **Call Notifications**     | ✅     | Full-screen intent notifications   |
| **Default Dialer Request** | ✅     | System dialog to set as default    |

### 🔄 Partially Implemented

| Feature             | Status | Notes                                  |
| ------------------- | ------ | -------------------------------------- |
| **Theme Mode**      | 🔄     | Dark only, light mode has placeholders |
| **Dot Matrix Text** | 🔄     | Needs accent color integration         |
| **Speed Dial**      | 🔄     | UI placeholder, no backend             |
| **Blocked Numbers** | 🔄     | UI placeholder, no backend             |

### ❌ Not Yet Implemented

| Feature             | Status | Notes                       |
| ------------------- | ------ | --------------------------- |
| **Light Mode**      | ❌     | Need color scheme for light |
| **Voicemail**       | ❌     | Not implemented             |
| **Call Recording**  | ❌     | Not implemented             |
| **Contact Editing** | ❌     | Opens system editor         |
| **Contact Sharing** | ❌     | Not implemented             |

---

## Architecture

### Key Components

- `MainActivity.kt` - Main app with pager and overlays
- `MainViewModel.kt` - State management
- `SettingsDataStore.kt` - Persistent preferences
- `LocalAccentColor` - Dynamic theme accent

### Theme System

```kotlin
// Accent color provided via CompositionLocal
CompositionLocalProvider(LocalAccentColor provides accentColor.toColor()) {
    // Children can access via LocalAccentColor.current
}
```

### Navigation

- Bottom nav controls `HorizontalPager`
- `pagerState` is single source of truth
- Detail screens are overlays with slide animations

---

## Required for Full Dialer Functionality

### 1. Default Dialer (ConnectionService)

To handle incoming/outgoing calls in-app:

- Implement `InCallService`
- Register in manifest
- Handle call states
- Show custom call UI

### 2. Call Notifications

- Create notification channel
- Show heads-up for incoming
- Ongoing notification for active calls
- Full-screen intent for incoming

### 3. Permissions Required

- `READ_CONTACTS`
- `WRITE_CONTACTS`
- `READ_CALL_LOG`
- `WRITE_CALL_LOG`
- `CALL_PHONE`
- `READ_PHONE_STATE`
- `MANAGE_OWN_CALLS`
- `ANSWER_PHONE_CALLS`

---

## Settings Sections

### General

- Set as Default (system dialog)
- Default Page (dropdown)

### Appearance

- Accent Color (8 swatches)
- Theme Mode (TODO: Dark/Light)

### Contacts

- Scrollbar Position (Left/Right)

### Search

- Show Recommendations (toggle)

### Calls

- Speed Dial (TODO)
- Blocked Numbers (TODO)

### About

- Version, Developer info
