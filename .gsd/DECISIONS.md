# DECISIONS.md — Architecture Decision Records

> Log of significant technical decisions made during the project.

## ADR-001: Bump minSdk from 24 to 26
**Date**: 2026-03-22
**Status**: Proposed
**Context**: Current code uses `java.time.Instant` which requires API 26+. Running on API 24-25 devices will crash.
**Decision**: Raise minSdk to 26. API 24-25 is <2% of active devices.
**Consequences**: Minor loss in device compatibility, eliminates crash risk.

## ADR-002: Keep Single-Module Architecture (for now)
**Date**: 2026-03-22
**Status**: Proposed
**Context**: Modularization adds complexity. Current codebase is 42 files.
**Decision**: Stay single-module through v1.0. Re-evaluate if build times exceed 60s or team grows.
**Consequences**: Simpler build process, slower builds at scale.
