#!/usr/bin/env bash
set -euo pipefail

# Last Paper 1.20.1 source revision (Paper build 196).
readonly PAPER_REPOSITORY="https://github.com/PaperMC/Paper-archive.git"
readonly PAPER_COMMIT="773dd724469bae89d0c2075edc3d1ddc8d5b0b18"
readonly PAPER_DIR="${PAPER_UPSTREAM_DIR:-build/paper-upstream-1.20.1}"

if ! command -v git >/dev/null 2>&1; then
  echo "git is required to prepare the pinned Paper source tree." >&2
  exit 1
fi

if [[ -e "$PAPER_DIR/.git" ]]; then
  actual_origin="$(git -C "$PAPER_DIR" remote get-url origin)"
  if [[ "$actual_origin" != "$PAPER_REPOSITORY" ]]; then
    echo "Refusing to reuse $PAPER_DIR: origin is $actual_origin, expected $PAPER_REPOSITORY." >&2
    exit 1
  fi
else
  mkdir -p "$PAPER_DIR"
  git -C "$PAPER_DIR" init
  git -C "$PAPER_DIR" remote add origin "$PAPER_REPOSITORY"
fi

# Fetch only the pinned revision instead of cloning the archive's full history.
git -C "$PAPER_DIR" fetch --depth=1 --no-tags origin "$PAPER_COMMIT"

git -C "$PAPER_DIR" checkout --detach "$PAPER_COMMIT"
git -C "$PAPER_DIR" submodule sync --recursive
git -C "$PAPER_DIR" submodule update --init --recursive

actual_commit="$(git -C "$PAPER_DIR" rev-parse HEAD)"
if [[ "$actual_commit" != "$PAPER_COMMIT" ]]; then
  echo "Paper source revision mismatch: $actual_commit" >&2
  exit 1
fi

echo "Preparing Paper 1.20.1 source at $actual_commit"
(
  cd "$PAPER_DIR"
  chmod +x ./gradlew
  ./gradlew applyPatches --no-daemon --stacktrace
)
