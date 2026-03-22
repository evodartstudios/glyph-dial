# REQUIREMENTS.md

> Generated from SPEC.md on 2026-03-22

## Format

| ID | Requirement | Source | Priority | Status |
|----|-------------|--------|----------|--------|
| **CORE DIALER** | | | | |
| REQ-01 | App can make, receive, answer, reject, and end phone calls as default dialer | Goal 2 | P0 | ✅ Done |
| REQ-02 | Hold, mute, speaker toggle, and audio routing work correctly | Goal 2 | P0 | ✅ Done |
| REQ-03 | In-call DTMF keypad sends tones during active calls | Goal 2 | P0 | Pending |
| REQ-04 | Dual-SIM call routing with user preference (always ask / default SIM) | Goal 2 | P1 | Pending |
| REQ-05 | Call waiting/conference call support | Goal 2 | P1 | Pending |
| **CONTACTS** | | | | |
| REQ-06 | Display device contacts with photo, name, numbers sorted alphabetically | Goal 1 | P0 | ✅ Done |
| REQ-07 | Contact detail screen with call, SMS, edit, share, delete, block actions | Goal 2 | P0 | Partial |
| REQ-08 | Favorite/star contacts and display favorites grid | Goal 2 | P0 | Partial |
| REQ-09 | Add new contact / save unknown number to contacts | Goal 2 | P1 | Pending |
| REQ-10 | Smart T9 search returns results in <100ms for 1000+ contacts | Goal 3 | P0 | Partial |
| **CALL LOG** | | | | |
| REQ-11 | Display call history grouped by date with stacked consecutive calls | Goal 1 | P0 | ✅ Done |
| REQ-12 | Call detail screen with redial, SMS, add contact, block options | Goal 2 | P0 | Partial |
| REQ-13 | Filter call log by type (all, missed, incoming, outgoing) | Goal 2 | P1 | Pending |
| **SMART FEATURES** | | | | |
| REQ-14 | Call recording with consent notification and storage management | Goal 2 | P1 | Pending |
| REQ-15 | Spam number detection and blocking with user-managed block list | Goal 2 | P1 | Pending |
| REQ-16 | Flash on ring (camera flash blinks for incoming calls) | Goal 2 | P1 | Pending |
| REQ-17 | Scheduled calls with notification and auto-dial | Goal 2 | P2 | Pending |
| REQ-18 | Call notes — attach text notes to contacts or call log entries | Goal 2 | P2 | Pending |
| REQ-19 | Auto-answer after configurable delay (with/without speaker) | Goal 2 | P2 | Pending |
| REQ-20 | Speed dial — long-press numpad digits 2-9 for quick dial | Goal 2 | P1 | Pending |
| REQ-21 | Smart dialer — predictive suggestions from recents + favorites + frequency | Goal 2 | P1 | Pending |
| **DESIGN & UX** | | | | |
| REQ-22 | Every interactive element has micro-animation + haptic feedback | Goal 1 | P0 | Pending |
| REQ-23 | Nothing-inspired design system with monochrome + accent, geometric precision | Goal 1 | P0 | Partial |
| REQ-24 | Smooth page transitions and component animations at 60fps | Goal 3 | P0 | Partial |
| REQ-25 | Dark, AMOLED, and Light theme modes | Goal 1 | P1 | Partial |
| REQ-26 | Configurable accent color with 8+ options | Goal 1 | P1 | ✅ Done |
| **PERFORMANCE** | | | | |
| REQ-27 | Cold start < 1 second on mid-range device | Goal 3 | P0 | Pending |
| REQ-28 | Sustained 60fps with no dropped frames during scrolling/animation | Goal 3 | P0 | Pending |
| REQ-29 | APK size < 30MB | Goal 3 | P0 | Pending |
| REQ-30 | Contact loading optimized (no N+1 queries, with caching) | Goal 3 | P0 | Pending |
| **NOTHING EXCLUSIVES** | | | | |
| REQ-31 | Glyph LED patterns for incoming calls on Nothing phones | Goal 5 | P2 | Pending |
| REQ-32 | Nothing OS-specific theme adaptation | Goal 5 | P2 | Pending |
| **PLAY STORE** | | | | |
| REQ-33 | Release signing configuration with keystore | Goal 4 | P0 | Pending |
| REQ-34 | ProGuard/R8 rules for all libraries (Hilt, Room, serialization) | Goal 4 | P0 | Pending |
| REQ-35 | Accessibility — TalkBack navigable, 4.5:1 contrast, content descriptions | Goal 4 | P0 | Pending |
| REQ-36 | Privacy policy URL and in-app link | Goal 4 | P0 | Pending |
| REQ-37 | App store listing assets (icon, screenshots, description, categories) | Goal 4 | P1 | Pending |
| REQ-38 | Crash-free rate > 99.5% verified through testing | Goal 4 | P0 | Pending |
| REQ-39 | Call recording consent mechanism compliant with local laws | Goal 4 | P0 | Pending |

## Priority Legend
- **P0**: Must-have for v1.0 launch
- **P1**: Should-have, significantly impacts user experience
- **P2**: Nice-to-have, can ship in v1.1 update
