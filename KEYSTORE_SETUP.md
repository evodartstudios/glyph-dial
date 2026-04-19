# GlyphDial Release Keystore Setup Guide

## Overview

This guide walks you through generating a company-grade signing keystore and configuring
it as a GitHub secret for the automated release workflow.

**You only need to do this once.** The keystore should be kept secret and backed up securely.

---

## Step 1: Generate the Release Keystore

Run this on a secure machine (your company dev machine, not a CI runner):

```bash
keytool -genkey -v \
  -keystore glyphdial-release.jks \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -alias glyphdial \
  -dname "CN=EvoDart, OU=Mobile, O=EvoDart Technologies, L=Your City, ST=Your State, C=IN"
```

You'll be prompted to set:
- **Keystore password** — pick a strong one, save it securely
- **Key password** — can be the same as keystore password

> ⚠️ **CRITICAL**: Back up `glyphdial-release.jks` to a secure location (password manager,
> encrypted cloud storage, company vault). If you lose this file, you cannot publish updates
> to apps signed with this key. **Never commit this file to git.**

---

## Step 2: Encode the Keystore as Base64

On Linux/macOS:
```bash
base64 -w 0 glyphdial-release.jks > glyphdial-release.jks.b64
cat glyphdial-release.jks.b64  # Copy this entire long string
```

On Windows (PowerShell):
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("glyphdial-release.jks")) | Set-Clipboard
```

---

## Step 3: Add GitHub Repository Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these 4 secrets:

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | The base64 string from Step 2 |
| `KEYSTORE_PASSWORD` | The keystore password you set |
| `KEY_ALIAS` | `glyphdial` (the alias you used in Step 1) |
| `KEY_PASSWORD` | The key password you set |

---

## Step 4: Create a Release

Push a version tag to trigger the release workflow:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The GitHub Actions workflow will:
1. Build the signed APK
2. Verify the signature
3. Create a GitHub Release with the APK attached
4. Generate release notes from commit messages

---

## Verifying a Release APK Locally

```bash
# With Android SDK installed:
$ANDROID_SDK_ROOT/build-tools/<version>/apksigner verify --print-certs GlyphDial-v1.0.0.apk
```

---

## Key Info to Save Securely

| Item | Notes |
|------|-------|
| `glyphdial-release.jks` | The keystore file — **never share or commit** |
| Keystore password | Needed to access the keystore |
| Key alias | `glyphdial` |
| Key password | Needed to sign with the key |
| Key validity | 10,000 days (~27 years) from creation date |

---

## Adding a PR/CI Check Workflow

Create `.github/workflows/ci.yml` for pull request checks:

```yaml
name: CI Build Check

on:
  pull_request:
    branches: [ main, develop ]
  push:
    branches: [ main ]

jobs:
  build-debug:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'gradle'
      - name: Build debug APK
        run: |
          chmod +x gradlew
          ./gradlew assembleDebug --no-daemon
```
