# ROADMAP.md

> **Current Phase**: Not started
> **Milestone**: v1.0 — Play Store Launch

## Must-Haves (from SPEC — P0)

- [ ] Rock-solid core dialer (call, answer, reject, hold, mute, speaker, DTMF)
- [ ] Optimized contact/call log loading with caching
- [ ] Premium Nothing-inspired design system with micro-animations
- [ ] Smart T9 search (<100ms for 1000+ contacts)
- [ ] Full contact management (edit, share, delete, favorite, block)
- [ ] Performance targets met (<1s cold start, 60fps, <30MB APK)
- [ ] Accessibility compliance (TalkBack, contrast, descriptions)
- [ ] Play Store release config (signing, ProGuard, privacy policy)
- [ ] Zero-crash quality bar

## Phases

### Phase 1: Foundation & Architecture Cleanup
**Status**: ✅ Complete
**Objective**: Fix critical bugs, clean up architecture, establish performance baseline
**Requirements**: REQ-27, REQ-28, REQ-29, REQ-30, REQ-34

**Deliverables:**
- [x] Fix minSdk 24 → 26 (java.time crash on older devices)
- [x] Remove unused Room dependency OR implement contact caching with Room
- [x] Fix N+1 query in ContactRepository (single join query)
- [x] Split MainViewModel into per-feature ViewModels (DialerViewModel, ContactsViewModel, CallLogViewModel, SettingsViewModel)
- [x] Extract duplicated SearchBar to shared component
- [x] Centralize action constants (ACTION_ANSWER/DECLINE/HANG_UP)
- [x] Add proper error handling in repositories (error states in ViewModels)
- [x] Implement ProGuard keep rules for Hilt, Room, serialization
- [x] Establish performance benchmarks (baseline measurements - 23MB APK)

---

### Phase 2: Design System & Premium UX
**Status**: 🔄 In Progress
**Objective**: Build a world-class Nothing-inspired design system with butter-smooth animations
**Requirements**: REQ-22, REQ-23, REQ-24, REQ-25, REQ-26

**Deliverables:**
- Audit and optimize/remove DotMatrix animations (profile GPU rendering, keep only if <2ms frame time)
- Design and implement micro-animation library (button press, ripple, scale, glow effects)
- Haptic feedback system (light/medium/heavy per interaction type)
- Premium page transitions (shared element, crossfade, spring physics)
- Refine all screens to pixel-perfect Nothing aesthetic
- Polish bottom navigation with indicator animation
- Light theme implementation (wire existing color scheme)
- Component library: premium cards, buttons, inputs, toggles, sliders
- Every text field, icon, divider reviewed for spacing/alignment

---

### Phase 3: Core Dialer Completion
**Status**: ⬜ Not Started
**Objective**: Complete all missing core dialer functionality
**Requirements**: REQ-03, REQ-04, REQ-05, REQ-07, REQ-08, REQ-09, REQ-10, REQ-12, REQ-13

**Deliverables:**
- In-call DTMF keypad with tones and haptics
- Dual-SIM selection dialog and preference management
- Call waiting UI and conference call support
- Contact actions: toggle favorite, edit, share, delete
- Add unknown number to contacts flow
- Block/unblock number functionality
- Call log filtering (all/missed/incoming/outgoing tabs)
- Optimized T9 search with trie-based index for <100ms results
- Smart dialer — predictive suggestions from frequency + recents + favorites

---

### Phase 4: Smart Features
**Status**: ⬜ Not Started
**Objective**: Implement differentiating features that set GlyphDial apart
**Requirements**: REQ-14, REQ-15, REQ-16, REQ-17, REQ-18, REQ-19, REQ-20, REQ-21

**Deliverables:**
- **Call Recording**: Background recording service, storage management, consent dialog (region-aware), recording list screen, playback
- **Spam Blocking**: Local spam database, caller ID lookup integration, block list management, spam indicator in call log
- **Flash on Ring**: Camera flash control for incoming calls, settings toggle, pattern options
- **Speed Dial**: Long-press numpad 2-9 configuration, speed dial management screen
- **Scheduled Calls**: Schedule UI, AlarmManager-based trigger, notification before dial, history
- **Call Notes**: Attach text notes to contacts/calls, note editor, search notes
- **Auto-Answer**: Configurable delay, speaker mode option, Bluetooth support
- **Vibrate on Connect**: Haptic feedback when outgoing call connects

---

### Phase 5: Play Store Launch
**Status**: ⬜ Not Started
**Objective**: Production-ready Play Store release
**Requirements**: REQ-33, REQ-35, REQ-36, REQ-37, REQ-38, REQ-39

**Deliverables:**
- Release signing keystore and build configuration
- Full accessibility audit and fixes (TalkBack, contrast, touch targets)
- Privacy policy creation and hosting
- Terms of service
- App icon (adaptive icon with Nothing-style design)
- Play Store listing: title, description, screenshots, feature graphic, categories
- Call recording consent mechanism (legal compliance)
- Crash-free testing (monkey testing, edge case testing)
- Performance validation against all success criteria
- Nothing-exclusive features (Glyph LED integration — P2, can ship in v1.1)
