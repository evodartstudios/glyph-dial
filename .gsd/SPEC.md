# SPEC.md — Project Specification

> **Status**: `FINALIZED`

## Vision

GlyphDial is a **next-generation Android dialer** inspired by Nothing's design philosophy — minimal, premium, and alive with subtle motion. While rooted in Nothing's aesthetic DNA, it's built for **all Android users** who want to escape the generic Google Dialer. The app delivers a comprehensive feature set (call recording, spam blocking, smart dialer, scheduled calls, flash alerts, call notes, auto-answer) with **butter-smooth 60fps performance**, every element meticulously designed and animated. It ships as a **complete Play Store-ready product** — not an MVP, but a polished, production-grade phone app that makes users feel like their dialer finally caught up to 2026.

## Goals

1. **Premium UX for everyone** — A dialer that makes users say "wow" at every interaction. Every button, every transition, every micro-animation designed to perfection. Nothing's design philosophy (monochrome + accent, geometric precision, restrained motion) as the foundation, accessible to all Android users.
2. **Feature-complete smart dialer** — All the features users wish their stock dialer had: call recording, spam detection/blocking, flash on ring, scheduled calls, call notes, auto-answer, speed dial, smart T9/predictive search, dual-SIM management, and more.
3. **Maximum performance** — Sub-1s cold start, consistent 60fps, minimal memory footprint, tiny APK. Remove or optimize any element (like dot matrix animations) that compromises performance. No janky scrolling, no dropped frames, ever.
4. **Production Play Store release** — Complete with signing config, ProGuard optimization, accessibility compliance, privacy policy, proper app listing assets, crash-free release quality.
5. **Nothing-exclusive bonus features** — Glyph light integration for incoming calls (when on Nothing hardware), Nothing OS-specific theming hooks. These features gracefully degrade on non-Nothing devices.

## Non-Goals (Out of Scope)

- VoIP / Wi-Fi calling (system-level feature)
- Video calling functionality
- Instant messaging / chat
- Contact sync with cloud services (beyond device contacts)
- iOS version
- Custom ROM integration
- Carrier-specific features (VoLTE toggle, etc.)

## Users

### Primary: Android Power Users
People dissatisfied with Google Dialer's blandness. They want a dialer that looks premium, has useful features (call recording, spam blocking), and performs flawlessly. Age 18-45, tech-savvy, care about aesthetics.

### Secondary: Nothing Phone Owners
Get exclusive glyph integration features on top of the core experience. These users already expect premium design from their hardware.

### Tertiary: Feature Seekers
Users specifically searching for call recording, spam blocking, or scheduled calls — features missing from stock dialers. They discover the app through Play Store search.

## Constraints

- **Technical**: Android minSdk 26 (fix current API 24 issue with java.time), single-module architecture (may need modularization), must be default dialer capable
- **Performance**: <1s cold start, 60fps sustained, <30MB APK, <100MB RAM in active call
- **Design**: Nothing design philosophy (monochrome base, single accent color, geometric precision, dot-matrix inspiration where performant)
- **Platform**: Must handle all Android Telecom API edge cases (call waiting, conference, SIM switching)
- **Legal**: Call recording laws vary by jurisdiction — must include recording notification/consent mechanisms
- **Play Store**: Must meet all Google Play policy requirements including Large Screen support guidelines

## Success Criteria

- [ ] Cold start < 1 second on mid-range device
- [ ] Sustained 60fps during all animations and scrolling
- [ ] APK size < 30MB
- [ ] All core dialer functions work (call, answer, reject, hold, mute, speaker, DTMF)
- [ ] Call recording functional with consent mechanism
- [ ] Spam detection/blocking operational
- [ ] Smart T9 search returns results in <100ms for 1000+ contacts
- [ ] Flash on ring works across device models
- [ ] Scheduled calls execute reliably
- [ ] 0 crash reports in pre-release testing
- [ ] Play Store listing approved
- [ ] Accessibility: TalkBack navigable, 4.5:1 contrast ratios
- [ ] Every interactive element has haptic feedback + micro-animation
