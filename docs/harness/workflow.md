# Agent Workflow

This workflow turns the lessons from learn-harness-engineering L01-L12 into project rules.

## Cold Start

1. Read `AGENTS.md`.
2. Run `./init.sh`.
3. Read `feature_list.json`.
4. Read `progress.md` and `session-handoff.md`.
5. Select exactly one unblocked feature.

If `./init.sh` fails, stop feature work and repair the baseline first.

## WIP=1

Only one feature may be `active`. Do not begin another feature until the active feature is either:

- `passing` with evidence, or
- `blocked` with a clear blocker and next question.

## Sprint Contract

Before implementing a non-trivial feature, write a short contract in `progress.md`:

```markdown
## Sprint Contract: feat-XXX - Name

### Scope
- Files or modules expected to change.
- User-visible behavior to implement.

### Verification
- Exact commands or manual checks that prove completion.

### Exclusions
- Related work that must not be done in this session.
```

## Completion Gate

Do not claim completion until:

1. Feature-specific verification passes.
2. `./init.sh` passes.
3. `feature_list.json` evidence is updated.
4. `progress.md` and `session-handoff.md` are updated.

## Failure Handling

When something fails, classify it before fixing:

- Requirement gap
- Context gap
- Environment gap
- Verification gap
- State/continuity gap
- Architecture boundary violation

Fix the harness layer if the failure came from missing or unclear process, not just the immediate code symptom.

