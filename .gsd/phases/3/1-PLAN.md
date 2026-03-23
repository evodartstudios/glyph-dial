---
phase: 3
plan: 1
wave: 1
---

# Plan 3.1: Contact Actions & Call Log Filters

## Objective
Implement missing core UI functionality for managing contacts and navigating the call log efficiently.

## Context
- .gsd/SPEC.md (REQ-04, REQ-05, REQ-08)
- .gsd/ROADMAP.md (Phase 3)
- app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
- app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt

## Tasks

<task type="auto">
  <name>Implement System Intent Wrappers for Contacts</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/utils/ContactIntents.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactDetailScreen.kt
  </files>
  <action>
    - Create a singleton `ContactIntents` utility class.
    - Add methods to generate intents for: `createContact()`, `editContact(uri)`, `shareContact(uri)`.
    - In `ContactDetailScreen.kt`, wire up the Edit, Share, and "Add to contacts" (for unknown numbers) buttons to launch these intents using `LocalContext.current.startActivity()`.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>User can cleanly hand off complex contact creation/editing to the system app, keeping our dialer fast and scoped.</done>
</task>

<task type="auto">
  <name>Implement Direct Content Actions (Favorite & Block)</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/ContactsViewModel.kt
  </files>
  <action>
    - Add `toggleFavorite(contactId: Long, isFavorite: Boolean)` in `ContactRepository` using `ContentProviderOperation` to update `ContactsContract.Contacts.STARRED`.
    - Add `blockNumber(number: String)` and `unblockNumber(number: String)` using `BlockedNumberContract.BlockedNumbers` (with try/catch for SecurityException if not default dialer).
    - Expose these actions through the view model and trigger them from the UI.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Contacts can be starred and unstarred directly from our app, and numbers can be added to the system blocklist.</done>
</task>

<task type="auto">
  <name>Call Log Filtering Tabs</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
  </files>
  <action>
    - Add a `CallLogFilter` enum (ALL, MISSED, INCOMING, OUTGOING).
    - Update `CallLogViewModel` to hold the current filter state and derive a `filteredCallLog` flow.
    - In `RecentsScreen.kt`, add a custom Nothing-style pill tab row below the top bar to switch between these filters.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Call logs can be filtered instantly by type without requiring a database requery, using in-memory flows.</done>
</task>

## Success Criteria
- [ ] Tapping "Edit" on a contact opens the system contact editor.
- [ ] Tapping the "Star" icon instantly updates the favorite status locally and in the system database.
- [ ] Recents screen includes pill filters for All, Missed, Outgoing, and Incoming calls.
