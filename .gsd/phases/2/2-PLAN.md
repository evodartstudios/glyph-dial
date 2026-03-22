---
phase: 2
plan: 2
wave: 1
---

# Plan 2.2: DotMatrix Interaction System & Dialpad Refactor

## Objective
Overhaul the complicated full-screen DotMatrix animations into a highly performant, reusable interaction system (like a custom ripple) and apply it to replace the "bad rings" on the Dialpad.

## Context
- .gsd/DECISIONS.md (Overhaul dot matrix, fix dialpad rings)
- app/src/main/java/com/evodart/glyphdial/ui/components/animation/DotMatrixAnimations.kt
- app/src/main/java/com/evodart/glyphdial/ui/screens/dialpad/DialPadScreen.kt
- app/src/main/java/com/evodart/glyphdial/ui/components/dialpad/DialPadButton.kt

## Tasks

<task type="auto">
  <name>Create DotMatrix Indication (Custom Ripple)</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/animation/DotMatrixRipple.kt
    app/src/main/java/com/evodart/glyphdial/ui/components/animation/DotMatrixAnimations.kt
  </files>
  <action>
    - Build a custom `Indication` (e.g., `DotMatrixRippleNodeFactory`) that draws a miniature, performant dot-matrix explosion/implosion on press coordinates.
    - This replaces the Android standard material ripple with a premium Nothing-style dot explosion that is calculated efficiently (caching coordinates).
    - Incorporate layered haptic feedback (tick on press down, light click on release).
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>A reusable `Modifier.dotMatrixClickable()` or standard Compose `Indication` is ready to use.</done>
</task>

<task type="auto">
  <name>Overhaul Dialpad Buttons</name>
  <files>app/src/main/java/com/evodart/glyphdial/ui/components/dialpad/DialPadButton.kt</files>
  <action>
    - Remove the existing generic rings and feedback from `DialPadButton`.
    - Apply the new `DotMatrixRipple` indication.
    - Refine the typography, spacing, and sizing of the Dialpad buttons so they perfectly echo the geometric Nothing aesthetic, scaling down slightly on press.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Dialpad buttons feature a premium, smooth Dot Matrix click reaction instead of generic rings.</done>
</task>

## Success Criteria
- [ ] Dot matrix is used as an interactive, performant feedback layer.
- [ ] Dialpad feels entirely custom and satisfying to type on.
