#!/usr/bin/env bash
set -euo pipefail

echo "=== Digital Employee Harness Initialization ==="

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "Repository: $ROOT"

echo "=== Runtime ==="
java -version
mvn -version | head -3

echo "=== Harness State ==="
bash scripts/check-harness-state.sh

echo "=== DDD Architecture ==="
bash scripts/check-architecture.sh

echo "=== Maven Build ==="
mvn clean install -DskipTests

echo "=== Verification Complete ==="
echo "Next:"
echo "1. Read feature_list.json."
echo "2. Work on exactly one active or unblocked feature."
echo "3. Record verification evidence before marking a feature passing."

