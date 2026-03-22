---
phase: 2
plan: 1
wave: 1
---

# Plan 2.1: Flawless Page Transitions & Pager Fix

## Objective
Fix the buggy, teleporting page swipe behavior and ensure butter-smooth navigation across the core app screens.

## Context
- .gsd/DECISIONS.md (User constraint on teleporting page bug)
- app/src/main/java/com/evodart/glyphdial/ui/components/pager/SwipeablePagePager.kt
- app/src/main/java/com/evodart/glyphdial/MainActivity.kt

## Tasks

<task type="auto">
  <name>Rewrite SwipeablePagePager</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/pager/SwipeablePagePager.kt
    app/src/main/java/com/evodart/glyphdial/MainActivity.kt
  </files>
  <action>
    - The current `SwipeablePagePager` has a bug where slow swipes cause the teleportation back to start/midway. This is likely due to bad custom gesture calculus.
    - Rewrite `SwipeablePagePager` to use Compose Foundation's official `HorizontalPager` or a robust `AnchoredDraggable` setup that perfectly tracks finger velocity and respects boundaries without teleporting.
    - Keep the existing `pagerState` API if possible, or refactor `MainActivity` to use `androidx.compose.foundation.pager.PagerState`.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>User can swipe slowly or quickly between pages without any visual teleportation or bugs.</done>
</task>

<task type="auto">
  <name>Premium Detail Screen Transitions</name>
  <files>app/src/main/java/com/evodart/glyphdial/MainActivity.kt</files>
  <action>
    - In `MainActivity.kt`, the `ContactDetailScreen` and `CallDetailScreen` currently use basic `slideInHorizontally` overlays.
    - Upgrade these to perform a combination of a subtle scale (0.95f -> 1.0f), a fade, and an emphasized slide, creating a physical "card rising" or "shared element" feel.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Overlays transition in and out with high-end, smooth physics instead of basic linear slides.</done>
</task>

## Success Criteria
- [ ] No teleporting on slow edge swipes.
- [ ] Detail screens animate luxuriously.
