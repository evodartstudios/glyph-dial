---
phase: 3
plan: 2
wave: 2
---

# Plan 3.2: Smart T9 Search & Trie Index

## Objective
Implement blazing-fast (<100ms) T9 contact search using a predictive Trie algorithm.

## Context
- .gsd/SPEC.md (REQ-09)
- app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
- app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt

## Tasks

<task type="auto">
  <name>Build T9 Trie Data Structure</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/utils/T9Trie.kt
  </files>
  <action>
    - Create a fast `T9Trie` class mapping characters (a-c -> 2, d-f -> 3, etc.).
    - Implement `insert(name: String, number: String, contact: Contact)` logic that inserts prefixes for both letters and direct numbers.
    - Implement `search(digits: String): List<Contact>` that performs a DFS to retrieve contacts.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>A reusable, self-contained `T9Trie` exists that maps numeric keypad sequences directly to contact objects instantly.</done>
</task>

<task type="auto">
  <name>Integrate with DialerViewModel</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/dialpad/DialPadScreen.kt
  </files>
  <action>
    - Rewrite `DialerViewModel` search logic: upon loading contacts, initialize the `T9Trie`.
    - Every time the `dialedNumber` flow updates, query the Trie. If the output > 0, display suggestions.
    - Rate current searches using a basic scoring rule (isStarred > frequency > alpha).
    - Limit suggestions to max 10 to keep the UI snappy.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Typing numbers on the dialpad brings up the suggestions list with extremely low latency regardless of contact count.</done>
</task>

## Success Criteria
- [ ] No dropped frames when tying rapidly on the dialpad.
- [ ] The search handles both phone numbers and words (223 = a,b,c + a,b,c + d,e,f -> "Abe").
