# Phase 3 Research: Core Dialer Features

## 1. Contact Management (Edit, Share, Delete, Add)
In Android dialer applications, implementing a full contact editor (vCard, multiple phone numbers, custom labels, emails, addresses) is an immense duplication of effort and highly prone to edge cases (sync adapters, read-only corporate contacts).
**Standard approach:**
- **Add/Edit**: Launch `Intent.ACTION_INSERT` or `Intent.ACTION_EDIT` with `ContactsContract.Contacts.CONTENT_URI`.
- **Share**: Create a vCard stream and launch `Intent.ACTION_SEND`.
- **Delete**: Can be done directly via `ContentResolver.delete()` or via `Intent.ACTION_VIEW` and letting the system app handle deletion.
- **Favorite (Star)**: Update `ContactsContract.Contacts.STARRED` directly via `ContentResolver.update()`.
- **Block**: Use `BlockedNumberContract.BlockedNumbers` (API 24+ requires app to be default dialer or messaging app).

## 2. Smart T9 Search & Predictive Dialer
To meet the `<100ms` threshold for 1000+ contacts, standard `filter { it.name.contains(...) }` becomes too slow, especially when mapping digits to multiple letters.
**Solution: Trie-based Indexing**
- When contacts are loaded, build an in-memory `Trie<Contact>`.
- Map characters `a,b,c -> 2`, `d,e,f -> 3`, etc.
- As the user types digits on the dialpad (`2-3-5`), traverse the Trie to instantly return all matching contacts (O(K) lookup where K is search length, rather than O(N) where N is contact count).
- Display predictive suggestions based on a combined score (Starred first, then high-frequency, then recent).

## 3. In-Call Advanced Features (DTMF, Conference)
- **DTMF**: The `android.telecom.Call` object provides `.playDtmfTone(char)` and `.stopDtmfTone()`. The UI must trigger `playDtmfTone` on `ACTION_DOWN` (press) and `stopDtmfTone` on `ACTION_UP` (release) to generate proper tone durations.
- **Audio Feedback**: Standard Android `ToneGenerator` can be used to play local feedback to the user, synced with the DTMF tone sent to the network.
- **Conference/Call Waiting**: Standard `InCallService` handles multiple `Call` objects in its `calls` list. If a new call enters the `STATE_RINGING` state while another call is `STATE_ACTIVE`, the UI must present a "Hold & Answer" vs "Drop & Answer" vs "Reject" dialog.
- **Dual-SIM**: Use `TelecomManager.phoneAccountHandles` to retrieve available SIMs. If there are multiple active `PhoneAccountHandle`s, present a bottom sheet selection before placing an `ACTION_CALL`.
