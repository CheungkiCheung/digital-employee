#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

python3 - <<'PY'
import json
import sys
from pathlib import Path

path = Path("feature_list.json")
if not path.exists():
    print("ERROR: feature_list.json is missing. FIX: create it at repository root.", file=sys.stderr)
    sys.exit(1)

data = json.loads(path.read_text(encoding="utf-8"))
features = data.get("features")
if not isinstance(features, list):
    print("ERROR: feature_list.json must contain a top-level 'features' array.", file=sys.stderr)
    sys.exit(1)

allowed = {"not-started", "active", "blocked", "passing"}
ids = []
active = []
errors = []

for index, feature in enumerate(features):
    prefix = f"features[{index}]"
    feature_id = feature.get("id")
    ids.append(feature_id)

    for field in ("id", "name", "behavior", "verification", "dependencies", "status", "evidence"):
        if field not in feature:
            errors.append(f"{prefix}: missing required field '{field}'.")

    status = feature.get("status")
    if status not in allowed:
        errors.append(f"{feature_id}: status must be one of {sorted(allowed)}, got {status!r}.")
    if status == "active":
        active.append(feature_id)
    if status == "passing" and not str(feature.get("evidence", "")).strip():
        errors.append(f"{feature_id}: passing features must include verification evidence.")
    if not isinstance(feature.get("dependencies", []), list):
        errors.append(f"{feature_id}: dependencies must be a list.")

if len(ids) != len(set(ids)):
    errors.append("feature ids must be unique.")

known = set(ids)
for feature in features:
    for dependency in feature.get("dependencies", []):
        if dependency not in known:
            errors.append(f"{feature.get('id')}: dependency {dependency!r} does not exist.")

if len(active) > 1:
    errors.append(f"WIP=1 violation: more than one active feature: {', '.join(active)}.")

if errors:
    print("ERROR: feature_list.json failed harness validation.", file=sys.stderr)
    for error in errors:
        print(f"- {error}", file=sys.stderr)
    print("FIX: keep exactly one active feature and record evidence before setting status to passing.", file=sys.stderr)
    sys.exit(1)

print(f"OK: feature_list.json valid ({len(features)} features, {len(active)} active).")
PY

