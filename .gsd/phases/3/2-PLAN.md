---
phase: 3
plan: 2
wave: 2
---

# Plan 3.2: Universal Contact Management & Call History UI

## Objective
Ensure adding/managing contacts is accessible from everywhere, and provide deep call history context on the Contact Details screen.

## Context
- .gsd/DECISIONS.md (User requests for FABs, unknown number flows, and detailed history)
- app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt
- app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
- app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactDetailScreen.kt
- app/src/main/java/com/evodart/glyphdial/ui/screens/dialpad/DialPadScreen.kt

## Tasks

<task type="auto">
  <name>Implement Universal "Add Contact" Flows</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/dialpad/DialPadScreen.kt
    - app/src/main/java/com/evodart/glyphdial/utils/ContactIntents.kt
  </files>
  <action>
    - Ensure `ContactIntents.kt` has robust `createContact(number)` logic.
    - **ContactsScreen**: Add a FloatingActionButton (FAB) to the bottom right for "New Contact".
    - **RecentsScreen**: For any row where the caller is unknown, add an "Add to Contacts" quick action icon/button.
    - **DialPadScreen**: When the user has dialed a number that doesn't match an existing contact, show an "Add to contacts" row directly above the dialpad.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>User can seamlessly add contacts from the dialpad, recents list, and main contacts list.</done>
</task>

<task type="auto">
  <name>Build Contact-Specific Call History UI</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactDetailScreen.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt
  </files>
  <action>
    - Expand `CallLogViewModel` to support fetching the history for a specific phone number or `contactId`.
    - Revamp the `ContactDetailScreen` to show a scrolling list of all previous calls with this specific person (incoming, outgoing, missed, duration) below the primary action buttons.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Viewing a contact reveals a comprehensive history of interactions with them.</done>
</task>

<task type="auto">
  <name>Favorite Toggle FAB</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactDetailScreen.kt
    - app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
  </files>
  <action>
    - Add a FloatingActionButton (FAB) to `ContactDetailScreen` representing the "Favorite" state (Star outline vs Filled Star).
    - Implement `ContactRepository.toggleFavorite(contactId: Long, isFavorite: Boolean)` using `ContentProviderOperation` to update `ContactsContract.Contacts.STARRED`.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Contacts can be starred and unstarred via the detail screen FAB.</done>
</task>

## Success Criteria
- [ ] Unknown numbers in the call log have an "add contact" option.
- [ ] Dialpad shows "Add to contacts" when a raw number is typed.
- [ ] Contact details screen shows full call history and a favorite FAB.
