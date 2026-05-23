# Digital Employee Agent Harness

This repository is the source of truth for the Digital Employee project. It is a Java 17, Spring Boot 3.4, Maven multi-module DDD hexagonal architecture application generated from the xfg DDD scaffold.

## Startup Workflow

Before writing code in a new session:

1. Confirm the repository root with `pwd`.
2. Read this file completely.
3. Run `./init.sh` and fix baseline failures before adding scope.
4. Read `feature_list.json` to find the single active or next unblocked feature.
5. Read `progress.md` and `session-handoff.md` for continuity.
6. If this is a git checkout, review `git status --short` and `git log --oneline -5`.

## Standard Commands

```bash
# Full harness verification
./init.sh

# Architecture constraints only
bash scripts/check-architecture.sh

# Feature-state constraints only
bash scripts/check-harness-state.sh

# Maven build used by the harness
mvn clean install -DskipTests
```

## Architecture Invariants

- Dependency direction is `trigger -> api/case/domain`, `case -> domain`, `domain <- infrastructure`, `app -> trigger/infrastructure`.
- Domain owns business rules and defines outbound interfaces under `domain/{bounded-context}/adapter/port` and `domain/{bounded-context}/adapter/repository`.
- Infrastructure implements domain interfaces only through `infrastructure/adapter/port` and `infrastructure/adapter/repository`.
- Repository implementations belong only in `infrastructure/adapter/repository`.
- DAO interfaces belong only in `infrastructure/dao`; PO objects belong only in `infrastructure/dao/po`.
- Redis operations belong only in `infrastructure/redis`.
- HTTP/RPC clients belong only in `infrastructure/gateway` and DTOs in `infrastructure/gateway/dto`.
- Never create `persistent` or `scenario` packages.
- Domain, Case, and Trigger must not import Infrastructure implementation packages.
- Infrastructure must not contain business decisions; put business validation in Domain services, entities, aggregates, value objects, strategies, or filters.

More detail lives in `docs/harness/architecture-constraints.md`.

## Work Rules

- WIP limit is 1: work on exactly one feature from `feature_list.json`.
- Do not start a second feature while any feature is `active`.
- A feature can become `passing` only after its verification command has actually run and evidence is recorded.
- Do not rely on chat history as project memory. Persist decisions in `progress.md`, `session-handoff.md`, or a relevant project doc.
- Keep `AGENTS.md` short. Put detailed topic rules under `docs/harness/` or near the code they constrain.
- Do not broaden scope with "while here" refactors unless the active feature explicitly requires them.
- If requirements are unclear, mark the feature `blocked` and ask for clarification rather than guessing.

## Feature List Rules

`feature_list.json` is the authoritative feature surface.

Valid statuses:

- `not-started`: defined but not yet active.
- `active`: exactly one feature currently being worked.
- `blocked`: cannot proceed without user input or an external dependency.
- `passing`: verification passed and evidence is recorded.

Every feature needs a behavior, verification command, dependency list, status, and evidence field. Passing features must include evidence.

## Definition of Done

A session is complete only when all applicable checks are true:

- The active feature behavior is implemented.
- `./init.sh` has run successfully, or any failure is documented as an existing blocker.
- Feature status and evidence are updated in `feature_list.json`.
- `progress.md` and `session-handoff.md` describe what changed, what was verified, and the next step.
- No temporary debug code, generated junk, or unrelated edits are left behind.
- The next session can start from the Startup Workflow without oral context.

## Git And GitHub

The intended remote is:

```bash
https://github.com/CheungkiCheung/digital-employee.git
```

Use conventional commits such as `feat: add employee profile domain` or `chore: add harness constraints`. Do not push without explicit user confirmation.

