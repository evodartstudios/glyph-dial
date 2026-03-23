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

## Phase 3 Decisions

**Date:** 2026-03-23

### Scope
- **DotMatrix animations**: Rip them out completely. The custom ripple we built in Phase 2 "looks too bad" to the user. Replace all `nothingClickable` with standard Android `ripple()` but themed appropriately to fit Nothing's clean aesthetic.
- **Add Contacts**: Must be accessible from everywhere (Recents list, a dedicated FAB in Contacts list, and native to the Dialpad when an unknown number is typed).
- **Contact Details**: Must show the extensive call history for that specific number, along with a FAB to favorite the contact.
- **Call State Transitions**: Fix the "bouncing" delay state when ending a call where it flashes back to dialing.
- **Call Log Sync**: Ensure recents/history updates immediately.

## Phase 3 Discussions & Decisions

**Date:** 2026-03-23

### Scope & User Feedback (Pre-Planning)
- **Call State Transitions**: User reported a bug where ending a call drops them back to the "calling screen" for seconds before finally disconnecting. The InCall Service state machine and `CallViewModel` teardown is lagging/incorrect.
- **Call Log Sync**: History does not automatically update after a call ends. `CallLogRepository` needs proactive invalidation or a `ContentObserver`.
- **Unknown Contacts**: Need an intuitive "Add to Contacts" flow immediately accessible from Recents and Call Details for unknown numbers.
- **Missing Core Flows**: User noted "lots of important features missing right now". We need to audit and expand Phase 3 bounds to ensure ALL foundational dialer flows (DTMF, Call Waiting, Hold, Mute, Speaker, Multi-SIM) are functional before moving to "Smart Features" (Phase 4).
