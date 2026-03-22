---
phase: 2
plan: 3
wave: 2
---

# Plan 2.3: Screen Refinements & Transitions

## Objective
Apply the newly created premium components, theming rules, and micro-animations to all screens. Implement premium Nothing-style page transitions.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/evodart/glyphdial/MainActivity.kt
- app/src/main/java/com/evodart/glyphdial/ui/components/pager/SwipeablePagePager.kt
- app/src/main/java/com/evodart/glyphdial/ui/screens/

## Tasks

<task type="auto">
  <name>Refine Main Screens and Bottom Nav</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/screens/dialpad/DialPadScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/contacts/ContactsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/recents/RecentsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/favorites/FavoritesScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/screens/settings/SettingsScreen.kt
    app/src/main/java/com/evodart/glyphdial/ui/components/navigation/NothingBottomNav.kt
  </files>
  <action>
    - Replace standard Material buttons/cards/toggles in all 5 screens with the new `NothingButton`, `NothingCard`, `NothingIconToggle` developed in Plan 2.2.
    - Polish `NothingBottomNav.kt` by adding an active indicator (a small dot under the active icon) that smoothly translates between icons on page change.
    - Ensure all text fields, icons, and dividers align perfectly with `NothingTextStyles` and spacing tokens.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>All screens utilize the finalized Phase 2 design system components.</done>
</task>

<task type="auto">
  <name>Implement Premium Page Transitions</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/MainActivity.kt
    app/src/main/java/com/evodart/glyphdial/ui/components/pager/SwipeablePagePager.kt
  </files>
  <action>
    - Upgrade the routing logic in `MainActivity` overlays (`ContactDetailOverlay`, `CallDetailOverlay`) to use a polished `slideInVertically` or `scaleIn` combined with `fadeIn` using `NothingMotion.Easing`.
    - If needed, adjust `SwipeablePagePager` to ensure adjacent pages parallax or fade cleanly during swipes.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Overlays animate smoothly with physics-based or bezier curves instead of linear moves.</done>
</task>

## Success Criteria
- [ ] Dialer, Contacts, Recents, and Settings strictly adhere to the unified component library.
- [ ] Bottom Nav features a smooth translating indicator.
- [ ] Detail overlays transition smoothly.
