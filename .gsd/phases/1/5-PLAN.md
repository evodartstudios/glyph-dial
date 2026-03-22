---
phase: 1
plan: 5
wave: 2
---

# Plan 1.5: Performance Baseline & Build Verification

## Objective
Establish measurable performance baselines and verify the entire Phase 1 cleanup compiles and runs correctly. This is the final validation step — if the app builds and we have baseline measurements, Phase 1 is complete.

**Wave 2** because it must run AFTER all code changes from Plans 1.1-1.4 are complete.

## Context
- .gsd/SPEC.md (Success Criteria — cold start, 60fps, APK size)
- .gsd/ARCHITECTURE.md
- app/build.gradle.kts

## Tasks

<task type="auto">
  <name>Verify clean build succeeds</name>
  <files>
    app/build.gradle.kts
  </files>
  <action>
    Run a clean debug build to verify all Phase 1 changes compile:
    
    ```
    ./gradlew clean assembleDebug
    ```
    
    If build fails:
    1. Read the error output carefully
    2. Fix the compilation error (likely import issues from ViewModel split)
    3. Re-run until clean build succeeds
    
    Then attempt release build:
    ```
    ./gradlew assembleRelease
    ```
    
    If release build fails (ProGuard):
    1. Check the mapping file for missing rules
    2. Add keep rules to proguard-rules.pro
    3. Re-run until release build succeeds
  </action>
  <verify>
    # APK should exist after build
    Test-Path "app/build/outputs/apk/debug/app-debug.apk"
  </verify>
  <done>Both debug and release builds succeed. APK files generated.</done>
</task>

<task type="auto">
  <name>Record APK size baseline</name>
  <files>
    .gsd/phases/1/BASELINE.md [NEW]
  </files>
  <action>
    After successful build, record the baseline metrics:
    
    1. Debug APK size: `(Get-Item app/build/outputs/apk/debug/app-debug.apk).Length`
    2. Release APK size: `(Get-Item app/build/outputs/apk/release/app-release.apk).Length` (if exists, might be unsigned)
    3. Method count: Check via build output or APK analyzer if available
    4. Total source file count and line count
    
    Create `.gsd/phases/1/BASELINE.md` documenting these numbers as the Phase 1 baseline.
    
    This gives concrete numbers to compare against the SPEC success criteria (<30MB APK).
  </action>
  <verify>
    Test-Path ".gsd/phases/1/BASELINE.md"
  </verify>
  <done>BASELINE.md created with APK sizes and source metrics. Numbers documented for future comparison.</done>
</task>

## Success Criteria
- [ ] `./gradlew clean assembleDebug` succeeds
- [ ] `./gradlew assembleRelease` succeeds (or issues documented)
- [ ] APK size baseline recorded in BASELINE.md
- [ ] All Phase 1 changes compile without warnings (or warnings documented)
