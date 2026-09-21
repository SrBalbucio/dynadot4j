#!/usr/bin/env bash
set -euo pipefail

DOC_URL="${DYNADOT_API_SPEC_URL:-https://www.dynadot.com/domain/api-document?getCommandInfoData=1&apiVersion=2.0.0}"
BASELINE_PATH="${DYNADOT_API_BASELINE:-docs/dynadot-api/latest.normalized}"
WORK_DIR="${DYNADOT_API_WORK_DIR:-target/dynadot-api-watch}"
RAW_PATH="$WORK_DIR/current.raw"
CURRENT_PATH="$WORK_DIR/current.normalized"
UPDATE_BASELINE="false"

if [[ "${1:-}" == "--update-baseline" ]]; then
  UPDATE_BASELINE="true"
fi

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 2
  fi
}

require_command curl
require_command diff
require_command perl

PYTHON_BIN=""
if command -v python3 >/dev/null 2>&1; then
  PYTHON_BIN="python3"
elif command -v python >/dev/null 2>&1; then
  PYTHON_BIN="python"
fi

mkdir -p "$WORK_DIR"

echo "Fetching Dynadot API documentation from $DOC_URL"
curl -fsSL \
  -A "dynadot4j-api-watch/1.0" \
  -H "Accept: application/json, text/html;q=0.9, text/plain;q=0.8, */*;q=0.1" \
  "$DOC_URL" \
  -o "$RAW_PATH"

if command -v jq >/dev/null 2>&1 && jq -e . "$RAW_PATH" >/dev/null 2>&1; then
  jq -S . "$RAW_PATH" > "$CURRENT_PATH"
elif [[ -n "$PYTHON_BIN" ]] && "$PYTHON_BIN" -c 'import json, sys; json.load(open(sys.argv[1], encoding="utf-8", errors="replace"))' "$RAW_PATH" >/dev/null 2>&1; then
  "$PYTHON_BIN" -c 'import json, sys; sys.stdout.reconfigure(encoding="utf-8"); data = json.load(open(sys.argv[1], encoding="utf-8", errors="replace")); json.dump(data, sys.stdout, ensure_ascii=False, indent=2, sort_keys=True); print()' "$RAW_PATH" > "$CURRENT_PATH"
else
  perl -0ne '
    s/\r\n?/\n/g;
    s/<script\b[^>]*>.*?<\/script>//gis;
    s/<style\b[^>]*>.*?<\/style>//gis;
    s/<!--.*?-->//gs;
    if (/(<div class="sidenav open-menu">.*?)(?:<div id="chat-widget"|<footer|<\/body>)/s) {
      $_ = $1;
    }
    s/data-cfemail="[^"]*"/data-cfemail="<cfemail>"/gi;
    s/[?&](utm_[^=]+|drefid|cache|cb|_)=([^"\s&<>]+)/?/gi;
    s/\b[0-9]{10,13}\b/<timestamp>/g;
    s/[[:space:]]+/ /g;
    s/>[[:space:]]+</>\n</g;
    print;
  ' "$RAW_PATH" | perl -ne '
    s/^\s+//;
    s/\s+$//;
    next if $_ eq "";
    print "$_\n";
  ' > "$CURRENT_PATH"
fi

perl -0pi -e 's/\b\d{13}\b/0/g' "$CURRENT_PATH"

if [[ "$UPDATE_BASELINE" == "true" ]]; then
  mkdir -p "$(dirname "$BASELINE_PATH")"
  cp "$CURRENT_PATH" "$BASELINE_PATH"
  echo "Updated Dynadot API baseline at $BASELINE_PATH"
  exit 0
fi

if [[ ! -f "$BASELINE_PATH" ]]; then
  echo "Dynadot API baseline not found: $BASELINE_PATH" >&2
  echo "Create it with: scripts/watch-dynadot-api.sh --update-baseline" >&2
  exit 2
fi

if diff -u "$BASELINE_PATH" "$CURRENT_PATH"; then
  echo "Dynadot API documentation matches the tracked baseline."
else
  echo "Dynadot API documentation changed." >&2
  echo "Review $CURRENT_PATH, update dynadot4j if needed, then run:" >&2
  echo "  scripts/watch-dynadot-api.sh --update-baseline" >&2
  exit 1
fi
