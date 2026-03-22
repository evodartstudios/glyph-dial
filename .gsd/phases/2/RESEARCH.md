# Phase 2 Research: Design System & Premium UX

## Animation Performance Context
The current `DotMatrixAnimations.kt` relies on `Canvas` to draw dozens of independent circles per frame to simulate explosions, implosions, and floating effects. While visually striking, recalculating `sin`/`cos` and drawing many individual `drawCircle` commands each composed frame can exceed the 2ms/16ms budget on lower-end devices. 
**Optimization Strategy**:
- Use `RenderNode` or `graphicsLayer` caching where possible.
- If profiling proves >2ms overhead, replace computationally heavy floating math with static SVG/Lottie-based pre-rendered animations or simplify the number of moving parts.

## Micro-animations & Haptics
Nothing OS design heavily relies on physical-feeling feedback.
- **Visual**: Buttons should slightly scale down (`0.95f`) on press with an `emphasized` easing curve.
- **Haptic**: Tie into `HapticFeedback` via `LocalHapticFeedback` (long press vs click). If `Vibrator` and `VibrationEffect` are available, create strong/medium/light ticks.

## Theme Implementation
The app currently provides `NothingColors`. A true Light theme will require mapping pure blacks to pure whites and inverting gray tones. Ensure `accentColor` remains vibrant across both themes.

## Screen Transitions
Compose Navigation or custom AnimatedVisibility handles transitions. We must ensure `slideInHorizontally` and `fadeIn` combinations map correctly to `NothingMotion.Easing`.
