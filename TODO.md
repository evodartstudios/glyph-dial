# 📌 Nothing Dialer — Roadmap & TODO

The core dialer is **production-ready** as of v1.0. The items below are stretch goals and community contributions we'd love to see.

> **How to pick up a task:** Comment on the relevant GitHub Issue (or open one), mention you're working on it, then submit a PR against `main`. See [`CONTRIBUTING.md`](./CONTRIBUTING.md) for the full workflow.

---

## 🔴 High Priority

- [ ] **Light / System Theme** — Currently dark-only. Needs a light-mode color scheme that matches the Nothing aesthetic (off-white backgrounds, dark text).
- [ ] **Contact Editing** — Bridge to `ContactsContract` edit intent with pre-filled fields, or implement a native edit form in Compose.
- [ ] **Speed Dial Configuration** — The Settings → Calls → "Configure Speed Dial" entry exists; wire it up so users can assign contacts to keys 2–9.
- [ ] **Blocked Numbers** — Settings → Calls → "Blocked Numbers" needs a backend: store blocked numbers in a local Room table and intercept calls in `GlyphDialCallService`.

---

## 🟡 Medium Priority

- [ ] **Voicemail Tab** — Add a dedicated voicemail screen that reads carrier visual voicemail (where supported) or provides a quick-dial shortcut to the mailbox.
- [ ] **In-App Call Recording** — Where legally permitted: use `MediaRecorder` on the audio stream. Respect locale laws — add a prominent user consent dialog.
- [ ] **Spam / CNAM Lookup** — Optionally query a community-driven spam API and show caller reputation on the incoming call screen.
- [ ] **RCS / WhatsApp Call Redirects** — Detect if a contact is reachable on a VoIP platform and surface it as a secondary call option on the contact detail screen.

---

## 🟢 Low Priority / Polish

- [ ] **Custom Ringtone per Contact** — Per-contact ringtone assignment stored in `ContactsContract`.
- [ ] **Recents Tabs (All / Missed / Incoming / Outgoing)** — Filter the call log by type with a segmented control above the list.
- [ ] **Page Transition Animations** — Custom Bezier/spring-based cross-page transitions in Compose beyond the current slide.
- [ ] **Tablet / Foldable Layout** — Two-pane UI when screen width ≥ 600 dp.
- [ ] **Wear OS Companion** — Reject / Answer / basic call controls from a connected watch.

---

## ✅ Completed (v1.0)

These were previously missing and have been implemented:

- ✅ Full call lifecycle (incoming / outgoing / hold / conference / merge / call-waiting)
- ✅ Live notification with duration counter, Mute / Speaker / Merge quick-actions
- ✅ Reject with SMS (presets + custom message)
- ✅ Audio route picker (Earpiece / Speaker / Bluetooth)
- ✅ Proximity sensor screen-off during ear call
- ✅ Emergency number bypass (112, 911, 999, 110…)
- ✅ Default SIM preference persisted in Settings
- ✅ Voicemail speed-dial (long-press 1 on dialpad)
- ✅ Swipe-to-delete call log entries + Clear All
- ✅ "Add to Contacts" persistent chip for unknown numbers
- ✅ Missed call notification
- ✅ DTMF in-call keypad overlay
- ✅ Dual-SIM detection and account routing
- ✅ T9 predictive dialing (Trie-based)
