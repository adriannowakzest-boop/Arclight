#!/usr/bin/env bash
set -euo pipefail

readonly BUILD_DIR="${1:-build/libs}"
readonly TEMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TEMP_DIR"' EXIT

fail() {
  echo "PaperBridge artifact verification failed: $*" >&2
  exit 1
}

command -v jar >/dev/null 2>&1 || fail "JDK jar command is unavailable"
command -v unzip >/dev/null 2>&1 || fail "unzip is unavailable"
command -v python3 >/dev/null 2>&1 || fail "python3 is unavailable"

mapfile -t artifacts < <(find "$BUILD_DIR" -maxdepth 1 -type f -name '*.jar' -print | sort)
[[ "${#artifacts[@]}" -eq 1 ]] || fail "expected exactly one distributable JAR in $BUILD_DIR, found ${#artifacts[@]}"

readonly ARTIFACT="${artifacts[0]}"
echo "Verifying $ARTIFACT"

jar tf "$ARTIFACT" > "$TEMP_DIR/installer.entries"
grep -Fxq 'io/izzel/arclight/server/Launcher.class' "$TEMP_DIR/installer.entries" || fail "installer launcher class is missing"
grep -Fxq 'common.jar' "$TEMP_DIR/installer.entries" || fail "nested common.jar is missing"
grep -Fxq 'gson.jar' "$TEMP_DIR/installer.entries" || fail "nested gson.jar is missing"

unzip -p "$ARTIFACT" META-INF/MANIFEST.MF > "$TEMP_DIR/installer.mf"
grep -Fq 'Main-Class: io.izzel.arclight.server.Launcher' "$TEMP_DIR/installer.mf" || fail "installer Main-Class manifest entry is missing"

unzip -p "$ARTIFACT" common.jar > "$TEMP_DIR/common.jar"
jar tf "$TEMP_DIR/common.jar" > "$TEMP_DIR/common.entries"
grep -Fxq 'io/izzel/arclight/common/mod/ArclightConnector.class' "$TEMP_DIR/common.entries" || fail "Mixin connector class is missing from common.jar"
grep -Fxq 'io/izzel/arclight/common/mod/plugin/PaperPluginDescriptor.class' "$TEMP_DIR/common.entries" || fail "Paper descriptor adapter is missing from common.jar"
grep -Fxq 'org/bukkit/plugin/PluginDescriptionFile.class' "$TEMP_DIR/common.entries" || fail "Bukkit/Paper API classes are missing from common.jar"
grep -Fxq 'mixins.arclight.refmap.json' "$TEMP_DIR/common.entries" || fail "generated Mixin refmap is missing from common.jar"

unzip -p "$TEMP_DIR/common.jar" META-INF/MANIFEST.MF > "$TEMP_DIR/common.mf"
grep -Fq 'MixinConnector: io.izzel.arclight.common.mod.ArclightConnector' "$TEMP_DIR/common.mf" || fail "MixinConnector manifest entry is missing"

mkdir -p "$TEMP_DIR/common"
unzip -q "$TEMP_DIR/common.jar" -d "$TEMP_DIR/common"
python3 - "$TEMP_DIR/common" <<'PY'
import json
import pathlib
import sys

root = pathlib.Path(sys.argv[1])
configs = sorted(root.glob("mixins.arclight*.json"))
if not configs:
    raise SystemExit("No Arclight Mixin configuration files were packaged")

for config_path in configs:
    try:
        config = json.loads(config_path.read_text(encoding="utf-8"))
    except Exception as exc:
        raise SystemExit(f"Invalid Mixin config {config_path.name}: {exc}")

    refmap = config.get("refmap")
    if refmap and not (root / refmap).is_file():
        raise SystemExit(f"{config_path.name} references missing refmap {refmap}")

    package = config.get("package", "").replace(".", "/")
    for mixin in config.get("mixins", []):
        class_file = root / package / (mixin.replace(".", "/") + ".class")
        if not class_file.is_file():
            raise SystemExit(f"{config_path.name} references missing mixin class {package}/{mixin}.class")

refmap_path = root / "mixins.arclight.refmap.json"
if not refmap_path.is_file() or refmap_path.stat().st_size == 0:
    raise SystemExit("Generated Mixin refmap is empty or missing")

print(f"Validated {len(configs)} Mixin configs and their referenced classes")
PY

echo "PaperBridge artifact verification passed"
