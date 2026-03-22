# Architectural Decisions & Discussions

## Phase 2 Decisions

**Date:** 2026-03-22

### Scope & User Feedback
- **DotMatrix animations**: Currently overcomplicated, poorly implemented, and underutilized. Must be completely overhauled to look "cool", smooth, and performant. 
- **Page Transitions**: The current `SwipeablePagePager` has a bug where slow swipes cause the page to randomly teleport mid-transition. This needs to be completely rewritten for flawless physics-based gestures.
- **Dialpad & Button Feedback**: Current "rings" look bad. Replace standard ripples with a custom, premium Nothing-style dot-matrix interaction or highly polished scale/haptic feedback.
- **Global Goal**: Every element across the app must have proper, smooth, and premium transitions.

### Approach
- **Chose**: 
  1. Complete rewrite of `SwipeablePagePager` to fix state management and gesture handling.
  2. Implement a unified `NothingInteractionSource` and `dotMatrixRipple` to replace Android's default `RippleTheme`.
  3. Overhaul `DotMatrixAnimations.kt` to be performant enough to be used natively on standard buttons (like the Dialpad) instead of just single full-screen elements.
- **Reason**: The app's identity relies on being a "next-gen" dialer. The default Compose components and current rushed animations are detrimental to this goal.

### Constraints
- Animations must maintain 60fps (no dropped frames during complex dot matrix math).
- The transition bug must be fixed before adding more complex UI elements.
