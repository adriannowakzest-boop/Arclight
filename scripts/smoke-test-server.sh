#!/usr/bin/env bash
set -euo pipefail

if [[ "$#" -ne 1 || ! -f "$1" ]]; then
  echo "Usage: $0 <server-installer.jar>" >&2
  exit 2
fi

readonly ARTIFACT="$(realpath "$1")"
readonly SERVER_DIR="$(mktemp -d)"
readonly LOG_FILE="$SERVER_DIR/server.log"
readonly CONTROL_PIPE="$SERVER_DIR/server.stdin"
readonly START_TIMEOUT_SECONDS="600"
readonly STOP_TIMEOUT_SECONDS="60"

server_pid=""
cleanup() {
  if [[ -n "$server_pid" ]] && kill -0 "$server_pid" 2>/dev/null; then
    printf 'stop\n' >&3 2>/dev/null || true
    for _ in $(seq 1 "$STOP_TIMEOUT_SECONDS"); do
      if ! kill -0 "$server_pid" 2>/dev/null; then
        break
      fi
      sleep 1
    done
    kill "$server_pid" 2>/dev/null || true
    wait "$server_pid" 2>/dev/null || true
  fi
  exec 3>&- 2>/dev/null || true
  rm -rf "$SERVER_DIR"
}
trap cleanup EXIT

cd "$SERVER_DIR"
printf 'eula=true\n' > eula.txt
mkfifo "$CONTROL_PIPE"
exec 3<>"$CONTROL_PIPE"

echo "Starting $ARTIFACT in $SERVER_DIR"
java -jar "$ARTIFACT" nogui < "$CONTROL_PIPE" > "$LOG_FILE" 2>&1 &
server_pid=$!

ready=0
for _ in $(seq 1 "$START_TIMEOUT_SECONDS"); do
  if grep -Eq 'Done \([0-9.]+s\)! For help, type "help"' "$LOG_FILE"; then
    ready=1
    break
  fi
  if ! kill -0 "$server_pid" 2>/dev/null; then
    wait "$server_pid" || true
    echo "Server exited before becoming ready. Log follows:" >&2
    cat "$LOG_FILE" >&2
    exit 1
  fi
  sleep 1
done

if [[ "$ready" -ne 1 ]]; then
  echo "Server did not become ready within ${START_TIMEOUT_SECONDS}s. Log tail follows:" >&2
  tail -n 200 "$LOG_FILE" >&2
  exit 1
fi

echo "Server reached the ready state; requesting a clean shutdown"
printf 'stop\n' >&3
for _ in $(seq 1 "$STOP_TIMEOUT_SECONDS"); do
  if ! kill -0 "$server_pid" 2>/dev/null; then
    break
  fi
  sleep 1
done

if kill -0 "$server_pid" 2>/dev/null; then
  echo "Server did not stop within ${STOP_TIMEOUT_SECONDS}s. Log tail follows:" >&2
  tail -n 200 "$LOG_FILE" >&2
  exit 1
fi

wait "$server_pid"
server_pid=""
echo "Server startup and clean shutdown passed"
