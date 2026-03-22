---
phase: 1
plan: 1
wave: 1
---

# Plan 1.1: Fix Critical Bugs & Build Config

## Objective
Fix the minSdk crash (java.time on API 24-25), remove unused Room dependency, and ensure ProGuard rules are in place so the release build doesn't crash. These are foundational fixes that everything else depends on.

## Context
- .gsd/ARCHITECTURE.md
- .gsd/STACK.md
- app/build.gradle.kts
- app/proguard-rules.pro
- gradle/libs.versions.toml

## Tasks

<task type="auto">
  <name>Bump minSdk from 24 to 26</name>
  <files>app/build.gradle.kts</files>
  <action>
    In `app/build.gradle.kts`, change `minSdk = 24` to `minSdk = 26`.
    
    Why: The codebase uses `java.time.Instant` (API 26+) in `CallLogEntry.kt` and `CallLogRepository.kt`. Running on API 24-25 will crash at runtime. API 24-25 represents <2% of active devices.
    
    Do NOT add desugaring as an alternative — it adds APK size and complexity for negligible user base.
  </action>
  <verify>
    grep -n "minSdk" app/build.gradle.kts
    # Should show: minSdk = 26
  </verify>
  <done>minSdk = 26 in build.gradle.kts, no references to API 24 anywhere</done>
</task>

<task type="auto">
  <name>Remove unused Room dependency OR keep for contact caching</name>
  <files>
    app/build.gradle.kts
    gradle/libs.versions.toml
  </files>
  <action>
    KEEP Room (it will be needed for contact caching, speed dial storage, blocked numbers, call notes in Phase 3-4). But for now, just verify the dependency is declared correctly and the KSP compiler is wired up.
    
    No action needed on dependency itself — it's already correctly declared. 
    
    Instead, ensure a minimal Room database shell exists so the build validates correctly:
    - Create `app/src/main/java/com/evodart/glyphdial/data/local/AppDatabase.kt` with an empty `@Database` class (no entities yet — they'll be added in Phase 3-4)
    - Create `app/src/main/java/com/evodart/glyphdial/data/local/di/DatabaseModule.kt` with a Hilt module providing the database
    
    This validates Room + KSP + Hilt integration works before we need it.
  </action>
  <verify>
    # Build should succeed with Room shell
    # Check files exist
    Test-Path "app/src/main/java/com/evodart/glyphdial/data/local/AppDatabase.kt"
    Test-Path "app/src/main/java/com/evodart/glyphdial/data/local/di/DatabaseModule.kt"
  </verify>
  <done>Room database shell created, builds without errors, KSP generates Room implementation</done>
</task>

<task type="auto">
  <name>Add ProGuard keep rules</name>
  <files>app/proguard-rules.pro</files>
  <action>
    Add ProGuard/R8 keep rules to `app/proguard-rules.pro` for:
    1. Hilt — keep generated components (`@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`)
    2. Room — keep entities, DAOs, database classes (for future use)
    3. Data models — keep `Contact`, `CallLogEntry`, `PhoneNumber`, `CallType`, `PhoneNumberType` (used via reflection/serialization)
    4. Kotlin serialization — keep `@Serializable` classes if any
    5. Coil — keep image loader internals
    6. libphonenumber — keep metadata resources
    
    Do NOT add overly broad rules (no `-dontwarn **`). Be specific.
  </action>
  <verify>
    # Check proguard file has content
    (Get-Content "app/proguard-rules.pro").Length -gt 10
  </verify>
  <done>ProGuard rules cover Hilt, Room, data models, Coil, libphonenumber. Release build succeeds.</done>
</task>

## Success Criteria
- [ ] minSdk = 26 in build config
- [ ] Room database shell created and builds
- [ ] ProGuard rules cover all critical libraries
- [ ] `./gradlew assembleRelease` succeeds (or at minimum `./gradlew kspDebugKotlin` succeeds)
