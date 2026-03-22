---
phase: 1
plan: 2
wave: 1
---

# Plan 1.2: Optimize Contact Loading (N+1 Query Fix)

## Objective
Fix the critical N+1 query pattern in `ContactRepository.getAllContacts()` where each contact triggers a separate query for phone numbers. Replace with a single efficient query. This is a P0 performance requirement (REQ-30).

## Context
- .gsd/ARCHITECTURE.md (Technical Debt section)
- app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
- app/src/main/java/com/evodart/glyphdial/data/model/Contact.kt

## Tasks

<task type="auto">
  <name>Replace N+1 contact query with single join query</name>
  <files>app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt</files>
  <action>
    Rewrite `getAllContacts()` to use a SINGLE ContentResolver query against `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` instead of the current two-phase approach (query contacts → query phones per contact).
    
    The single query should:
    1. Query `Phone.CONTENT_URI` with projection: `CONTACT_ID`, `DISPLAY_NAME_PRIMARY`, `PHOTO_URI`, `STARRED`, `NUMBER`, `TYPE`, `LABEL`
    2. Sort by `DISPLAY_NAME_PRIMARY ASC`
    3. Group results by `CONTACT_ID` in memory (using `groupBy`)
    4. Map each group to a `Contact` with its `List<PhoneNumber>`
    5. Deduplicate phone numbers by digits (keep existing `distinctBy` logic)
    
    This reduces from O(N) queries to O(1) query.
    
    Also apply the same optimization to `searchContacts()` — rewrite to query `Phone.CONTENT_URI` with a `LIKE` filter on `DISPLAY_NAME_PRIMARY`.
    
    Keep `getStarredContacts()` and `lookupContactByNumber()` as-is — they have different access patterns.
    
    Do NOT change the method signatures or return types — downstream consumers should not need changes.
  </action>
  <verify>
    # Check the file no longer has the getPhoneNumbers(contactId) per-contact call in getAllContacts
    # The private fun getPhoneNumbers should still exist for lookupContactByNumber but not be called from getAllContacts
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt" -Pattern "Phone.CONTENT_URI"
  </verify>
  <done>getAllContacts() uses single query against Phone.CONTENT_URI. No N+1 pattern. Method signatures unchanged.</done>
</task>

<task type="auto">
  <name>Add error handling to repositories</name>
  <files>
    app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt
    app/src/main/java/com/evodart/glyphdial/data/repository/CallLogRepository.kt
  </files>
  <action>
    Currently the `.catch { e -> }` blocks in the ViewModel are empty — errors are silently swallowed.
    
    1. In both repositories, wrap ContentResolver queries in try-catch and emit empty list on failure (with logging via `android.util.Log.e`)
    2. Add `@Throws` documentation to suspend functions
    3. In `ContactRepository.lookupContactByNumber()`, ensure SecurityException is caught (permission may be revoked mid-use)
    
    Do NOT add complex error state classes yet — that's Phase 1.3 (ViewModel split). Just ensure no crashes from ContentResolver failures.
  </action>
  <verify>
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/data/repository/ContactRepository.kt" -Pattern "Log.e"
    Select-String -Path "app/src/main/java/com/evodart/glyphdial/data/repository/CallLogRepository.kt" -Pattern "Log.e"
  </verify>
  <done>All repository methods have try-catch with Log.e. No uncaught exceptions from ContentResolver operations.</done>
</task>

## Success Criteria
- [ ] `getAllContacts()` uses single ContentResolver query (no N+1)
- [ ] `searchContacts()` optimized similarly
- [ ] All repository methods have error handling with logging
- [ ] No change in method signatures — existing callers unaffected
