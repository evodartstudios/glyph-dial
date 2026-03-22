---
phase: 2
plan: 2
wave: 1
---

# Plan 2.2: Component Polish & Theming

## Objective
Establish reusable, pixel-perfect UI components and wire up full Light Theme support.

## Context
- .gsd/SPEC.md
- app/src/main/java/com/evodart/glyphdial/ui/theme/Theme.kt
- app/src/main/java/com/evodart/glyphdial/ui/components/

## Tasks

<task type="auto">
  <name>Implement Light Theme Support</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/theme/Theme.kt
    app/src/main/java/com/evodart/glyphdial/ui/theme/Color.kt
  </files>
  <action>
    Update `GlyphDialTheme` to handle `isSystemInDarkTheme()`. Map dark colors (e.g., PureBlack -> PureWhite, SilverGray -> DarkGray) to create a stark, high-contrast Nothing-style light theme. Ensure LocalAccentColor works seamlessly across both.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Themes switch based on system setting seamlessly.</done>
</task>

<task type="auto">
  <name>Build Premium Component Library</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/ui/components/buttons/NothingButtons.kt
    app/src/main/java/com/evodart/glyphdial/ui/components/cards/NothingCards.kt
  </files>
  <action>
    Create reusable `NothingButton`, `NothingIconToggle`, and `NothingCard`.
    - Apply the `nothingClickable` modifier from Plan 2.1 to these components.
    - Standardize spacing, border widths (e.g., 1dp borders for outlined elements), and corner radiuses.
  </action>
  <verify>./gradlew assembleDebug</verify>
  <done>Standardized components exist for buttons, toggles, and cards.</done>
</task>

## Success Criteria
- [ ] Light theme properly inverts monochrome colors.
- [ ] Reusable buttons and cards exist using the new interaction modifiers.
