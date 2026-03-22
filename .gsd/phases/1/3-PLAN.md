---
phase: 1
plan: 3
wave: 1
---

# Plan 1.3: Extract Shared Components & Centralize Constants

## Objective
Extract duplicated code (SearchBar, action constants) into shared locations. Clean code hygiene that prevents divergence as features are added.

## Context
- .gsd/ARCHITECTURE.md (Technical Debt → duplicated SearchBar, action constants)
- app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt (lines 421-481)
- app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt (lines 188-248)
- app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt (lines 411-416)
- app/src/main/java/com/evodart/glyphdial/service/CallActionReceiver.kt (lines 20-22)

## Tasks

<task type="auto">
  <name>Extract SearchBar to shared component</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/search/NothingSearchBar.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt
  </files>
  <action>
    1. Create `ui/components/search/NothingSearchBar.kt` with a public `@Composable fun NothingSearchBar(query, onQueryChange, placeholder, modifier)` that combines the best of both existing implementations:
       - Use `LocalAccentColor.current` for cursor (RecentsScreen version) not hardcoded `NothingColors.NothingRed` (ContactsScreen version)
       - Keep clear button, search icon, BasicTextField
       - Add `leadingIcon` and `trailingContent` parameters for extensibility
    
    2. In `RecentsScreen.kt`: Delete the private `SearchBar` composable, replace usage with `NothingSearchBar`
    
    3. In `ContactsScreen.kt`: Delete the private `SearchBar` composable, replace usage with `NothingSearchBar`
    
    Ensure both screens behave identically to before — this is a pure refactor.
  </action>
  <verify>
    # Verify shared component exists
    Test-Path "app/src/main/java/com/evodart/glyphdial/ui/components/search/NothingSearchBar.kt"
    # Verify no private SearchBar left in screens
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt" -Pattern "private fun SearchBar"
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt" -Pattern "private fun SearchBar"
    # Both should return no matches
  </verify>
  <done>NothingSearchBar is a single shared component, both screens use it, no duplicate code</done>
</task>

<task type="auto">
  <name>Centralize action constants</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/service/CallConstants.kt [NEW]
    app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
    app/src/main/java/com/evodart/glyphdial/service/CallActionReceiver.kt
  </files>
  <action>
    1. Create `service/CallConstants.kt` with an `object CallConstants` containing:
       - `ACTION_ANSWER`, `ACTION_DECLINE`, `ACTION_HANG_UP`
       - `CHANNEL_INCOMING`, `CHANNEL_ACTIVE`
       - `NOTIFICATION_ID`
    
    2. Update `GlyphDialCallService.kt`: Remove private constants at bottom of file, import from `CallConstants`
    
    3. Update `CallActionReceiver.kt`: Remove private constants at bottom of file, import from `CallConstants`
    
    This eliminates the duplication risk where one file gets updated but not the other.
  </action>
  <verify>
    Test-Path "app/src/main/java/com/evodart/glyphdial/service/CallConstants.kt"
    # Verify old constants removed from both files
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/service/CallActionReceiver.kt" -Pattern "private const val ACTION"
    # Should return no matches
  </verify>
  <done>All call-related constants in one place. No duplicated action strings.</done>
</task>

## Success Criteria
- [ ] `NothingSearchBar` is a single shared composable used by both RecentsScreen and ContactsScreen
- [ ] No duplicate `private fun SearchBar` in any screen
- [ ] All call action constants live in `CallConstants.kt`
- [ ] No duplicate constant definitions across service files
