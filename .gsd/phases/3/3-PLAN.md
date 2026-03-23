---
phase: 3
plan: 3
wave: 2
---

# Plan 3.3: Advanced In-Call Features

## Objective
Wire up the `GlyphDialCallService` and `InCallScreen` to support DTMF tones and Dual-SIM operations.

## Context
- .gsd/SPEC.md (REQ-07, REQ-12)
- app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
- app/src/main/java/com/evodart/glyphdial/ui/components/dialpad/CallActionBar.kt

## Tasks

<task type="auto">
  <name>Implement DTMF Control in Call Service</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
    - app/src/main/java/com/evodart/glyphdial/service/CallConstants.kt
  </files>
  <action>
    - Expose methods `playDtmfTone(char)` and `stopDtmfTone()` in the `GlyphDialCallService` bound interface.
    - Under the hood, safely apply these to the active `android.telecom.Call`.
    - Provide an audio fallback using `ToneGenerator(AudioManager.STREAM_DTMF, 80)` so the user hears the feedback.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Service layer manages DTMF start/stop states predictably and safely routes them to the active phone call.</done>
</task>

<task type="auto">
  <name>Connect UI Keypad to DTMF</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/screens/incall/InCallScreen.kt
  </files>
  <action>
    - Provide a keypad overlay state in the active call screen.
    - Wire the `PointerInput` `ACTION_DOWN` / `ACTION_UP` gestures of the `NothingDialButton` to trigger `playDtmfTone` and `stopDtmfTone` respectively via the View Model or bound service interface.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Pressing digits during an active call relays the tones into the IVR via TelecomManager correctly timed with the user's touch.</done>
</task>

<task type="auto">
  <name>Dual-SIM Capability Routing</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/components/dialpad/CallActionBar.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt
  </files>
  <action>
    - Use `TelecomManager.callCapablePhoneAccounts` to extract available SIM metadata (carrier names, slot ids).
    - If multiple SIMs are detected (list size > 1) and there is no default selected, pop up a bottom sheet in `CallActionBar` when dialing.
    - Attach the selected `PhoneAccountHandle` into the `EXTRA_PHONE_ACCOUNT_HANDLE` argument when executing the `ACTION_CALL` intent.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Users with dual-SIM devices will correctly be prompted to select a calling SIM, avoiding default drops.</done>
</task>

## Success Criteria
- [ ] Pressing 1 during a call plays the DTMF tone for the entire duration of the press.
- [ ] Calling from a multi-SIM environment correctly routes using the user's selected Handle without defaulting.
