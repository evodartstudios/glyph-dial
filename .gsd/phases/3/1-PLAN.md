---
phase: 3
plan: 1
wave: 1
---

# Plan 3.1: Rip Out DotMatrix & Fix Core Bugs

## Objective
Remove the DotMatrix ripple entirely globally, fix the broken Call State transitions, and instantly sync Call Logs.

## Context
- .gsd/DECISIONS.md (User requests removing DotMatrix and fixing glitches)
- app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
- app/src/main/java/com/evodart/glyphdial/ui/components/animation/InteractionModifiers.kt

## Tasks

<task type="auto">
  <name>Remove DotMatrix Ripple Globally</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/components/animation/InteractionModifiers.kt
  </files>
  <action>
    - Completely gut the dot calculation and Canvas drawing logic inside `nothingClickable`.
    - Replace the custom `indication = null` with standard Compose `indication = ripple(...)`. Keep the haptic ticks and micro-scale down effect on press (so it still feels premium and tactile), but remove the visual dots entirely.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>All buttons across the app show standard, clean ripples instead of dot matrix explosions.</done>
</task>

<task type="auto">
  <name>Fix Call State Machine Teardown</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallViewModel.kt
  </files>
  <action>
    - Ensure when `onCallRemoved` or state hits `STATE_DISCONNECTED`, the service instantly notifies the ViewModel to close the `InCallActivity`.
    - Remove artificial delays or ensure `CallViewModel` doesn't fallback to `STATE_DIALING` if it was already active before tear down.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Hanging up an active call instantly closes the calling screen without bouncing back to a dialing UI.</done>
</task>

<task type="auto">
  <name>Instant Call Log Syncing</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/data/repository/CallLogRepository.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/CallLogViewModel.kt
  </files>
  <action>
    - Implement a Kotlin Flow or `ContentObserver` in `CallLogRepository` that observes `CallLog.Calls.CONTENT_URI`.
    - `CallLogViewModel` should collect this flow so the Recents UI always stays instantly up-to-date.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>After a call ends, the Recents tab immediately shows the latest record without an app restart.</done>
</task>

## Success Criteria
- [ ] Tapping the dialpad flashes a standard dark ripple.
- [ ] Finishing a call instantly returns to the app.
- [ ] The call is instantly visible in Recents.
