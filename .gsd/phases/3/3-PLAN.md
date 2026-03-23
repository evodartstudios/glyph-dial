---
phase: 3
plan: 3
wave: 2
---

# Plan 3.3: Smart T9 Search & Advanced Telecom

## Objective
Implement blazing-fast T9 predictive search and wire up essential in-call Telecom features like DTMF and Dual-SIM.

## Context
- .gsd/SPEC.md (REQ-09, REQ-07, REQ-12)
- app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
- app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt
- app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt

## Tasks

<task type="auto">
  <name>Build T9 Trie Index</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/utils/T9Trie.kt
    - app/src/main/java/com/evodart/glyphdial/ui/viewmodel/DialerViewModel.kt
  </files>
  <action>
    - Create a `T9Trie` class mapping characters (a-c -> 2, d-f -> 3, etc.).
    - Update `DialerViewModel` search logic to use the `T9Trie` for sub-100ms lookup instead of basic `.contains()` filtering.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Typing numbers on the dialpad brings up suggestions instantly based on T9 mapping.</done>
</task>

<task type="auto">
  <name>Implement DTMF Control</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/service/GlyphDialCallService.kt
    - app/src/main/java/com/evodart/glyphdial/ui/screens/incall/InCallScreen.kt
  </files>
  <action>
    - Expose `playDtmfTone(char)` and `stopDtmfTone()` in the `GlyphDialCallService` bound interface, interacting with `android.telecom.Call`.
    - Provide a keypad overlay state in the active call screen, triggering these methods on `ACTION_DOWN` / `ACTION_UP`.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Pressing digits during an active call relays the tones into the IVR via TelecomManager correctly timed with the user's touch.</done>
</task>

<task type="auto">
  <name>Dual-SIM Capability Routing</name>
  <files>
    - app/src/main/java/com/evodart/glyphdial/ui/components/dialpad/CallActionBar.kt
  </files>
  <action>
    - Use `TelecomManager.callCapablePhoneAccounts` to extract available SIM metadata (carrier names, slot ids).
    - If multiple SIMs are detected without a system default, pop up a bottom sheet when dialing.
    - Attach the selected `PhoneAccountHandle` into the `EXTRA_PHONE_ACCOUNT_HANDLE` argument when executing the `ACTION_CALL` intent.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Users with dual-SIM devices will correctly be prompted to select a calling SIM.</done>
</task>

## Success Criteria
- [ ] No dropped frames when typing rapidly on the dialpad.
- [ ] Pressing 1 during a call plays the DTMF tone for the entire duration of the press.
- [ ] Calling from a multi-SIM environment correctly routes using the user's selected Handle without defaulting.
