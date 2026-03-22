---
phase: 1
plan: 4
wave: 2
---

# Plan 1.4: Split MainViewModel Into Feature ViewModels

## Objective
Break the monolithic `MainViewModel` (handles contacts, call log, search, permissions, settings) into focused per-feature ViewModels. This is the biggest architectural smell — a single ViewModel handling everything violates separation of concerns and will become unmanageable as smart features are added in Phase 3-4.

**Wave 2** because this depends on Plan 1.2 (repository error handling) and Plan 1.3 (shared components) being complete.

## Context
- .gsd/ARCHITECTURE.md (Technical Debt → single ViewModel)
- app/src/main/java/com/evodart/glyphdial/ui/viewmodel/MainViewModel.kt
- app/src/main/java/com/evodart/glyphdial/MainActivity.kt (consumes ViewModel)

## Tasks

<task type="auto">
  <name>Create per-feature ViewModels</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/viewmodel/ContactsViewModel.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/ui/viewmodel/SettingsViewModel.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/ui/viewmodel/MainViewModel.kt
  </files>
  <action>
    Create 4 new `@HiltViewModel` classes:
    
    1. **ContactsViewModel** — Owns `_contacts`, `_starredContacts`, `isLoading`. Injects `ContactRepository`. Methods: `loadContacts()`, `searchContacts()`.
    
    2. **CallLogViewModel** — Owns `_recentCalls`, `_missedCalls`, `isLoading`. Injects `CallLogRepository`. Methods: `loadRecentCalls()`, `loadMissedCalls()`.
    
    3. **DialerViewModel** — Owns `_searchQuery`, `_t9Suggestions`. Injects `ContactRepository`. Methods: `updateSearchQuery()`, `getT9Suggestions()`.
    
    4. **SettingsViewModel** — Owns settings flows. Injects `SettingsDataStore`. Methods: `setDefaultStartPage()`, `setScrollbarPosition()`, `setAccentColor()`, `setShowRecommendations()`.
    
    Each ViewModel should:
    - Be annotated with `@HiltViewModel` and `@Inject constructor`
    - Expose read-only `StateFlow` properties
    - Handle errors with `Result` wrapper or `UiState` sealed class
    - Use `viewModelScope.launch` for coroutines
    
    5. **Strip MainViewModel** down to: only permission state management (`hasPermissions`, `onPermissionsGranted`). It will coordinate high-level app state only.
    
    Do NOT break any existing screen composable signatures yet — the view parameter changes happen in the next task.
  </action>
  <verify>
    Test-Path "app/src/main/java/com/evodart/glyphdial/ui/viewmodel/ContactsViewModel.kt"
    Test-Path "app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt"
    Test-Path "app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt"
    Test-Path "app/src/main/java/com/evodart/glyphdial/ui/viewmodel/SettingsViewModel.kt"
  </verify>
  <done>4 new ViewModels exist with injected repositories. MainViewModel only handles permissions.</done>
</task>

<task type="auto">
  <name>Wire new ViewModels into MainActivity</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/MainActivity.kt
  </files>
  <action>
    Update `MainActivity.kt` to inject and use the new ViewModels:
    
    1. Add `hiltViewModel()` calls for each new ViewModel in the composable that needs it
    2. Replace all `viewModel.contacts` references with `contactsViewModel.contacts`
    3. Replace all `viewModel.recentCalls` references with `callLogViewModel.recentCalls`
    4. Replace all `viewModel.t9Suggestions` references with `dialerViewModel.t9Suggestions`
    5. Replace all settings reads/writes with `settingsViewModel.*`
    6. Keep `mainViewModel` for permissions only
    
    Each screen composable should receive its data from the appropriate ViewModel:
    - `DialPadScreen` ← DialerViewModel
    - `RecentsScreen` ← CallLogViewModel
    - `ContactsScreen` ← ContactsViewModel
    - `FavoritesScreen` ← ContactsViewModel (starred)
    - `SettingsScreen` ← SettingsViewModel
    
    Use `hiltViewModel()` at the point of use in each screen's composable, NOT at the Activity level for all of them.
  </action>
  <verify>
    # MainViewModel should no longer reference ContactRepository or CallLogRepository
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/ui/viewmodel/MainViewModel.kt" -Pattern "ContactRepository"
    # Should return no matches
  </verify>
  <done>MainActivity uses 5 ViewModels. Each screen gets data from its dedicated ViewModel. No monolithic ViewModel.</done>
</task>

## Success Criteria
- [ ] 4 new feature ViewModels created with Hilt injection
- [ ] MainViewModel reduced to permissions-only
- [ ] Each screen uses its dedicated ViewModel
- [ ] All existing functionality preserved (no regressions)
