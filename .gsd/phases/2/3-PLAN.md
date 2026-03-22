---
phase: 2
plan: 3
wave: 2
---

# Plan 2.3: App-Wide Polish & Theming

## Objective
Apply the newly created DotMatrix interaction system to all touch targets, finalize spacing, provide an animated Bottom Nav, and wire up Light Theme.

## Context
- .gsd/DECISIONS.md
- app/src/main/java/com/evodart/glyphdial/ui/screens/
- app/src/main/java/com/evodart/glyphdial/ui/components/navigation/NothingBottomNav.kt
- app/src/main/java/com/evodart/glyphdial/ui/theme/Theme.kt

## Tasks

<task type="auto">
  <name>Global Animation Application</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/settings/SettingsScreen.kt
  </files>
  <action>
    - Go through all list items (Contact rows, Call log rows) and setting toggles.
    - Replace default clickable interactions with the premium `dotMatrixClickable`.
    - Ensure every element is properly animated and feels cohesive.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Zero standard Android ripples remain; replaced entirely by the Nothing ecosystem interactions.</done>
</task>

<task type="auto">
  <name>Bottom Nav and Light Theme Finish</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/navigation/NothingBottomNav.kt
    app/src/main/java/com/evodart/glyphdial/ui/theme/Theme.kt
  </files>
  <action>
    - Complete `NothingBottomNav.kt` by ensuring the selected tab indicator physically slides between icons when swapping pages.
    - Implement Light Theme support in `Theme.kt`, ensuring the `accentColor` remains visible and monochrome rules flip gracefully.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>App supports light/dark mode and navigation is fully animated.</done>
</task>

## Success Criteria
- [ ] Every tap across the app yields a premium animated response.
- [ ] Bottom Nav highlights transition incredibly smoothly.
- [ ] System light/dark theme toggle works.
