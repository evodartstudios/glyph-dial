---
phase: 2
plan: 1
wave: 1
---

# Plan 2.1: Micro-Animations & Haptics

## Objective
Establish the foundational physical feel of the Nothing aesthetic by auditing heavy animations and introducing scalable micro-animations and haptic feedback.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/evodart/glyphdial/ui/components/animation/DotMatrixAnimations.kt
- app/src/main/java/com/evodart/glyphdial/ui/theme/NothingMotion.kt

## Tasks

<task type="auto">
  <name>Audit and Optimize DotMatrix Animations</name>
  <files>app/src/main/java/com/evodart/glyphdial/ui/components/animation/DotMatrixAnimations.kt</files>
  <action>
    Optimize the Canvas drawing in DotMatrix animations. Reduce the `dotCount` defaults for explosion/implosion. Introduce `graphicsLayer` for caching if applicable, or simplify the math (pre-calculate `sin`/`cos` arrays during initialization). Ensure default frame time drops.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>mathematical overhead is cached and animation parameters are tuned for performance</done>
</task>

<task type="auto">
  <name>Implement Micro-Animation and Haptic Modifiers</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/animation/InteractionModifiers.kt
    app/src/main/java/com/evodart/glyphdial/ui/theme/NothingMotion.kt
  </files>
  <action>
    Create a new file `InteractionModifiers.kt`. Implement a `Modifier.nothingClickable` that extends standard clickable to include:
    1. A slight scale down to `0.95f` using `animateFloatAsState` on press.
    2. Integrated `HapticFeedback` (light tick on press).
    Expose these interaction tokens in `NothingMotion`.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>A reusable Modifier is available for premium button presses with haptics.</done>
</task>

## Success Criteria
- [ ] DotMatrix animations are optimized.
- [ ] `nothingClickable` modifier provides scale and haptic feedback.
