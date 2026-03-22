# Glyph Dial - Project Architecture

> Technical architecture and module structure for the Nothing Dialer app

---

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/evodart/glyphdial/
│   │   │
│   │   ├── GlyphDialApp.kt                    # Application class
│   │   ├── MainActivity.kt                    # Single activity entry
│   │   │
│   │   ├── core/                              # Core utilities
│   │   │   ├── di/                            # Dependency injection
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   └── RepositoryModule.kt
│   │   │   ├── extensions/                    # Kotlin extensions
│   │   │   │   ├── ContextExtensions.kt
│   │   │   │   ├── StringExtensions.kt
│   │   │   │   └── ModifierExtensions.kt
│   │   │   ├── permissions/                   # Permission handling
│   │   │   │   ├── PermissionManager.kt
│   │   │   │   └── PermissionState.kt
│   │   │   └── utils/                         # General utilities
│   │   │       ├── PhoneNumberUtils.kt
│   │   │       ├── DateTimeUtils.kt
│   │   │       └── ContactUtils.kt
│   │   │
│   │   ├── data/                              # Data layer
│   │   │   ├── local/                         # Local database
│   │   │   │   ├── GlyphDialDatabase.kt
│   │   │   │   ├── dao/
│   │   │   │   │   ├── CallLogDao.kt
│   │   │   │   │   ├── BlockedNumberDao.kt
│   │   │   │   │   ├── SpeedDialDao.kt
│   │   │   │   │   └── RecordingDao.kt
│   │   │   │   └── entity/
│   │   │   │       ├── CallLogEntity.kt
│   │   │   │       ├── BlockedNumberEntity.kt
│   │   │   │       ├── SpeedDialEntity.kt
│   │   │   │       └── RecordingEntity.kt
│   │   │   ├── repository/
│   │   │   │   ├── CallLogRepository.kt
│   │   │   │   ├── ContactRepository.kt
│   │   │   │   ├── BlockedNumberRepository.kt
│   │   │   │   └── SettingsRepository.kt
│   │   │   └── datastore/                     # Preferences
│   │   │       └── SettingsDataStore.kt
│   │   │
│   │   ├── domain/                            # Domain layer
│   │   │   ├── model/                         # Domain models
│   │   │   │   ├── Contact.kt
│   │   │   │   ├── CallLog.kt
│   │   │   │   ├── CallType.kt
│   │   │   │   ├── BlockedNumber.kt
│   │   │   │   └── SpeedDial.kt
│   │   │   └── usecase/                       # Business logic
│   │   │       ├── call/
│   │   │       │   ├── MakeCallUseCase.kt
│   │   │       │   ├── EndCallUseCase.kt
│   │   │       │   └── GetCallLogUseCase.kt
│   │   │       ├── contact/
│   │   │       │   ├── GetContactsUseCase.kt
│   │   │       │   ├── SearchContactsUseCase.kt
│   │   │       │   └── GetFavoritesUseCase.kt
│   │   │       └── blocking/
│   │   │           ├── BlockNumberUseCase.kt
│   │   │           └── IsSpamUseCase.kt
│   │   │
│   │   ├── ui/                                # Presentation layer
│   │   │   ├── theme/                         # Nothing Design System
│   │   │   │   ├── Theme.kt
│   │   │   │   ├── Color.kt
│   │   │   │   ├── Typography.kt
│   │   │   │   ├── Spacing.kt
│   │   │   │   ├── Shape.kt
│   │   │   │   └── Motion.kt
│   │   │   │
│   │   │   ├── components/                    # Reusable components
│   │   │   │   ├── cards/
│   │   │   │   │   ├── NothingCard.kt
│   │   │   │   │   ├── NothingCircleCard.kt
│   │   │   │   │   ├── NothingSquareCard.kt
│   │   │   │   │   └── NothingBannerCard.kt
│   │   │   │   ├── dialpad/
│   │   │   │   │   ├── NothingDialPad.kt
│   │   │   │   │   ├── NothingDialButton.kt
│   │   │   │   │   ├── PhoneNumberDisplay.kt
│   │   │   │   │   └── CallActionBar.kt
│   │   │   │   ├── contact/
│   │   │   │   │   ├── NothingContactItem.kt
│   │   │   │   │   ├── NothingContactAvatar.kt
│   │   │   │   │   ├── AlphabetIndexer.kt
│   │   │   │   │   └── FavoriteContactCard.kt
│   │   │   │   ├── calllog/
│   │   │   │   │   ├── NothingCallLogItem.kt
│   │   │   │   │   ├── CallLogSectionHeader.kt
│   │   │   │   │   └── CallFilterChips.kt
│   │   │   │   ├── incall/
│   │   │   │   │   ├── CallerInfoCard.kt
│   │   │   │   │   ├── DotMatrixTimer.kt
│   │   │   │   │   ├── InCallControlGrid.kt
│   │   │   │   │   └── CallActionButton.kt
│   │   │   │   ├── common/
│   │   │   │   │   ├── NothingButton.kt
│   │   │   │   │   ├── NothingTextField.kt
│   │   │   │   │   ├── NothingSwitch.kt
│   │   │   │   │   ├── NothingChip.kt
│   │   │   │   │   ├── NothingDialog.kt
│   │   │   │   │   └── NothingBottomSheet.kt
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── NothingBottomNav.kt
│   │   │   │   │   ├── NothingTopBar.kt
│   │   │   │   │   └── NothingFab.kt
│   │   │   │   └── animation/
│   │   │   │       ├── DotMatrixText.kt
│   │   │   │       ├── PulsingGlow.kt
│   │   │   │       └── ProgressArc.kt
│   │   │   │
│   │   │   ├── navigation/                    # App navigation
│   │   │   │   ├── GlyphDialNavigation.kt
│   │   │   │   ├── NavigationRoutes.kt
│   │   │   │   └── NavAnimations.kt
│   │   │   │
│   │   │   └── screens/                       # Feature screens
│   │   │       ├── dialpad/
│   │   │       │   ├── DialPadScreen.kt
│   │   │       │   └── DialPadViewModel.kt
│   │   │       ├── recents/
│   │   │       │   ├── RecentsScreen.kt
│   │   │       │   └── RecentsViewModel.kt
│   │   │       ├── contacts/
│   │   │       │   ├── ContactsScreen.kt
│   │   │       │   ├── ContactsViewModel.kt
│   │   │       │   ├── ContactDetailScreen.kt
│   │   │       │   ├── ContactDetailViewModel.kt
│   │   │       │   ├── EditContactScreen.kt
│   │   │       │   └── EditContactViewModel.kt
│   │   │       ├── favorites/
│   │   │       │   ├── FavoritesScreen.kt
│   │   │       │   └── FavoritesViewModel.kt
│   │   │       ├── settings/
│   │   │       │   ├── SettingsScreen.kt
│   │   │       │   ├── SettingsViewModel.kt
│   │   │       │   ├── BlockedNumbersScreen.kt
│   │   │       │   ├── SpeedDialScreen.kt
│   │   │       │   └── GlyphSettingsScreen.kt
│   │   │       ├── search/
│   │   │       │   ├── SearchScreen.kt
│   │   │       │   └── SearchViewModel.kt
│   │   │       ├── call/
│   │   │       │   ├── IncomingCallScreen.kt
│   │   │       │   ├── OutgoingCallScreen.kt
│   │   │       │   ├── ActiveCallScreen.kt
│   │   │       │   ├── CallEndedScreen.kt
│   │   │       │   └── CallViewModel.kt
│   │   │       └── stats/
│   │   │           ├── StatisticsScreen.kt
│   │   │           └── StatisticsViewModel.kt
│   │   │
│   │   ├── service/                           # Background services
│   │   │   ├── call/
│   │   │   │   ├── GlyphDialerService.kt      # InCallService
│   │   │   │   ├── CallStateManager.kt
│   │   │   │   └── CallNotificationService.kt
│   │   │   ├── recording/
│   │   │   │   └── CallRecordingService.kt
│   │   │   └── glyph/
│   │   │       └── GlyphIntegrationService.kt # Nothing Phone only
│   │   │
│   │   └── receiver/                          # Broadcast receivers
│   │       ├── BootReceiver.kt
│   │       └── CallReceiver.kt
│   │
│   └── res/
│       ├── drawable/                          # Icons and graphics
│       ├── font/                              # Custom fonts
│       │   ├── ndot_55.ttf
│       │   ├── ndot_57.ttf
│       │   └── jetbrains_mono.ttf
│       ├── values/
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── xml/
│           └── backup_rules.xml
│
├── build.gradle.kts
└── proguard-rules.pro
```

---

## 📦 Dependency Stack

### Core Android
```kotlin
// libs.versions.toml additions
[versions]
kotlin = "2.0.21"
agp = "8.7.3"
composeBom = "2024.12.01"
hilt = "2.51.1"
room = "2.6.1"
navigation = "2.8.5"
lifecycle = "2.8.7"
coroutines = "1.9.0"
datastore = "1.1.1"

[libraries]
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version = "1.15.0" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version = "1.7.0" }

# Compose
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-compose-animation = { group = "androidx.compose.animation", name = "animation" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }

# Lifecycle
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }

# Room Database
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Hilt DI
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# Image Loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version = "2.7.0" }

# Phone Number Parsing
libphonenumber = { group = "com.googlecode.libphonenumber", name = "libphonenumber", version = "8.13.50" }

# Accompanist (permissions, etc.)
accompanist-permissions = { group = "com.google.accompanist", name = "accompanist-permissions", version = "0.36.0" }

# Splash Screen
androidx-core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version = "1.0.1" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-ksp = { id = "com.google.devtools.ksp", version = "2.0.21-1.0.27" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
room = { id = "androidx.room", version.ref = "room" }
```

---

## 🔐 Required Permissions

```xml
<!-- AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Core Dialer Permissions -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />
    <uses-permission android:name="android.permission.READ_CALL_LOG" />
    <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
    <uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS" />
    
    <!-- Contacts -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    
    <!-- Audio/Recording -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    
    <!-- Vibration/Haptics -->
    <uses-permission android:name="android.permission.VIBRATE" />
    
    <!-- Notifications -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_PHONE_CALL" />
    
    <!-- Default Dialer -->
    <uses-permission android:name="android.permission.MANAGE_OWN_CALLS" />
    
    <!-- Connectivity -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Storage (for recordings) -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
        android:maxSdkVersion="29" />

    <application ...>
        
        <!-- Declare as Dialer App -->
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.DIAL" />
                <data android:scheme="tel" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <data android:scheme="tel" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
            </intent-filter>
        </activity>
        
        <!-- InCallService for call handling -->
        <service
            android:name=".service.call.GlyphDialerService"
            android:permission="android.permission.BIND_INCALL_SERVICE"
            android:exported="true">
            <meta-data
                android:name="android.telecom.IN_CALL_SERVICE_UI"
                android:value="true" />
            <meta-data
                android:name="android.telecom.IN_CALL_SERVICE_RINGING"
                android:value="true" />
            <intent-filter>
                <action android:name="android.telecom.InCallService" />
            </intent-filter>
        </service>
        
    </application>
</manifest>
```

---

## 🎯 Implementation Phases

### Phase 1: Foundation (Week 1-2)
1. Project setup with Clean Architecture
2. Design system implementation (theme, colors, typography)
3. Base components (NothingCard variants, buttons, text fields)
4. Navigation structure

### Phase 2: Core Dialer (Week 3-4)
1. Dial pad with T9 search
2. InCallService implementation
3. Call screens (incoming, active, ended)
4. Basic call handling

### Phase 3: Contacts & History (Week 5-6)
1. Contact list with system contacts integration
2. Contact detail screen
3. Call history with filtering
4. Favorites management

### Phase 4: Advanced Features (Week 7-8)
1. Call blocking and spam detection
2. Speed dial
3. Call recording
4. Settings screens

### Phase 5: Nothing Exclusive (Week 9-10)
1. Glyph integration (if SDK available)
2. Dot matrix animations
3. Widget implementation
4. Statistics dashboard

### Phase 6: Polish (Week 11-12)
1. Animations and transitions
2. Accessibility
3. Performance optimization
4. Testing and bug fixes

---

## 🧪 Testing Strategy

### Unit Tests
- ViewModel logic
- Use cases
- Repository implementations
- Utility functions

### UI Tests
- Component rendering
- Navigation flows
- User interactions

### Integration Tests
- Database operations
- Permission handling
- Call service integration

---

## 📱 Nothing Phone Specific

### Glyph SDK Integration
```kotlin
// Check for Nothing Phone
fun isNothingPhone(): Boolean {
    return Build.MANUFACTURER.equals("Nothing", ignoreCase = true)
}

// Glyph SDK usage (when available)
// The Nothing Glyph SDK provides APIs for:
// - Glyph.setPattern(pattern: GlyphPattern)
// - Glyph.startAnimation(animation: GlyphAnimation)
// - Glyph.stopAnimation()
```

### Nothing-Specific Features
- Glyph light patterns for contacts
- Incoming call Glyph animation
- Missed call notification Glyph
- Quick flip to silence detection
