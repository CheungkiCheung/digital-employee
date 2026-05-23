#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

fail() {
  echo "ERROR: $1" >&2
  echo "FIX: $2" >&2
  exit 1
}

if find . -path '*/target' -prune -o -type d \( -name persistent -o -name scenario \) -print | grep -q .; then
  find . -path '*/target' -prune -o -type d \( -name persistent -o -name scenario \) -print >&2
  fail "Forbidden DDD package detected." "Do not create persistent/ or scenario/. Use infrastructure/adapter/repository, infrastructure/dao, infrastructure/dao/po, infrastructure/redis, or infrastructure/gateway."
fi

INFRA_ROOT="digital-employee-infrastructure/src/main/java/com/digitalemployee/infrastructure"
if [ -d "$INFRA_ROOT" ]; then
  while IFS= read -r dir; do
    name="$(basename "$dir")"
    case "$name" in
      adapter|dao|redis|gateway|config) ;;
      *)
        fail "Unexpected infrastructure top-level package: $dir" "Only adapter, dao, redis, gateway, and config are allowed under infrastructure."
        ;;
    esac
  done < <(find "$INFRA_ROOT" -mindepth 1 -maxdepth 1 -type d | sort)
fi

if rg -n 'import com\.digitalemployee\.infrastructure|import org\.mybatis|import java\.sql|import javax\.sql|import org\.springframework\.(jdbc|data|web|boot)' digital-employee-domain/src/main/java >/tmp/digital-employee-domain-boundary.txt 2>/dev/null; then
  cat /tmp/digital-employee-domain-boundary.txt >&2
  fail "Domain layer imports infrastructure or technical framework APIs." "Define repository/port interfaces in Domain and implement them in Infrastructure adapter packages."
fi

if rg -n 'import com\.digitalemployee\.infrastructure' digital-employee-case/src/main/java digital-employee-trigger/src/main/java >/tmp/digital-employee-upstream-boundary.txt 2>/dev/null; then
  cat /tmp/digital-employee-upstream-boundary.txt >&2
  fail "Case or Trigger imports Infrastructure directly." "Route through Case/Domain services and Domain-defined interfaces; keep Infrastructure behind adapters."
fi

PORT_DIR="$INFRA_ROOT/adapter/port"
if [ -d "$PORT_DIR" ] && rg -n 'import com\.digitalemployee\.infrastructure\.(dao|redis)' "$PORT_DIR" >/tmp/digital-employee-port-boundary.txt 2>/dev/null; then
  cat /tmp/digital-employee-port-boundary.txt >&2
  fail "Infrastructure adapter/port imports dao or redis." "Port adapters may call gateway clients only. Move local persistence work to adapter/repository."
fi

REPO_DIR="$INFRA_ROOT/adapter/repository"
if [ -d "$REPO_DIR" ] && rg -n 'import com\.digitalemployee\.infrastructure\.gateway' "$REPO_DIR" >/tmp/digital-employee-repository-boundary.txt 2>/dev/null; then
  cat /tmp/digital-employee-repository-boundary.txt >&2
  fail "Infrastructure adapter/repository imports gateway." "Repository adapters may call dao and redis only. Move remote calls to adapter/port and gateway."
fi

echo "OK: DDD architecture boundaries verified."

