# Verification

`./init.sh` is the standard startup and completion gate.

## Current Baseline

```bash
./init.sh
```

This runs:

1. Harness feature-state checks.
2. DDD architecture boundary checks.
3. Maven build:

```bash
mvn clean install -DskipTests
```

The scaffold currently has no meaningful business tests. For every future feature, add feature-specific tests or runtime checks and record them in `feature_list.json`.

## Verification Levels

Use the strongest applicable level.

| Level | Required When | Examples |
|---|---|---|
| Static | Every change | compile, architecture scripts |
| Unit | Domain entities, services, utilities | JUnit tests in module |
| Integration | Repository, DAO, Redis, gateway behavior | Spring/MyBatis tests, Testcontainers when added |
| End-to-end | HTTP flows, jobs, cross-module behavior | start app, call API, assert response and side effects |

## Evidence Format

Record evidence like this:

```text
2026-05-23: `./init.sh` passed. Feature-specific check `mvn -pl digital-employee-domain test` passed with 6 tests.
```

If a check cannot run, do not mark the feature `passing`. Mark it `blocked` and record the reason.

