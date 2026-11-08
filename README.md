# Arclight

Arclight is a hybrid Minecraft server project that combines Bukkit-compatible server functionality with mod-loader support.

[![Downloads](https://img.shields.io/github/downloads/IzzelAliz/Arclight/total?style=flat-square)](https://arclight.izzel.io/)
[![License](https://img.shields.io/github/license/IzzelAliz/Arclight?style=flat-square)](LICENSE)

![Arclight logo](.github/arclightlogo.jpg)

## Project status

This repository contains the Arclight project and an experimental development branch targeting a Forge + Bukkit/Paper hybrid for Minecraft 1.20.1.

The experimental work is maintained on:

- **Branch:** [`paperbridge-1.20.1`](https://github.com/adriannowakzest-boop/Arclight/tree/paperbridge-1.20.1)
- **Minecraft:** `1.20.1`
- **Forge:** `47.4.28`
- **Java:** `17`

The PaperBridge branch is under active development and must be treated as experimental. It is not a production-ready replacement for a stable Arclight release.

## Downloads

For existing Arclight releases and their support status, use the official project resources:

- [Arclight downloads](https://arclight.izzel.io/)
- [Releases on GitHub](https://github.com/IzzelAliz/Arclight/releases)
- [Versions and support status](https://github.com/IzzelAliz/Arclight/discussions/1575)

Builds from the experimental PaperBridge branch, when published by its workflow, are available from that branch's [GitHub Actions runs](https://github.com/adriannowakzest-boop/Arclight/actions/workflows/paperbridge-1.20.1.yml).

## Installing a released server

1. Download the server JAR for the Minecraft and mod-loader version you intend to use.
2. Make sure the installed Java version matches the requirements of that release.
3. Start the server from its working directory:

   ```bash
   java -jar arclight.jar nogui
   ```

   Replace `arclight.jar` with the actual name of the downloaded file.

4. Follow the first-start instructions and review the generated configuration files before opening the server to players.

The `nogui` argument disables the graphical server control panel.

For release-specific requirements and setup details, consult the [Arclight documentation](https://wiki.izzel.io/s/arclight-docs).

## PaperBridge 1.20.1

PaperBridge is an experimental effort to integrate Paper-related plugin compatibility into the Arclight Forge server for Minecraft 1.20.1. The work is isolated on the [`paperbridge-1.20.1` branch](https://github.com/adriannowakzest-boop/Arclight/tree/paperbridge-1.20.1).

### Target environment

| Component | Version |
|---|---|
| Minecraft | `1.20.1` |
| Forge | `47.4.28` |
| Java | `17` |
| SpongePowered Mixin | `0.8.5` |
| Paper API | `1.20.1-R0.1-SNAPSHOT` |

### Compatibility scope

The branch builds on Arclight's existing Forge and Bukkit/Spigot integration. It includes an experimental adapter that can translate a limited subset of basic `paper-plugin.yml` descriptors into the legacy Bukkit `JavaPlugin` descriptor model.

This adapter does **not** provide Paper's native plugin runtime. In particular, the following are not implemented by the descriptor adapter:

- Paper plugin bootstrapper classes and bootstrap lifecycle.
- Custom Paper plugin loaders.
- Paper's isolated plugin classpath and `join-classpath: false` behavior.
- Full Paper dependency ordering semantics.
- Paper-specific server runtime, lifecycle, command, and API implementations.

The presence of Paper API on the build classpath does not guarantee that every Paper API method is implemented by the running server. Bukkit/Spigot compatibility remains the baseline, and Paper-only plugins require individual testing. Do not assume compatibility with all Paper plugins.

### Build PaperBridge from source

Requirements: Git, JDK 17, and an internet connection.

```bash
git clone https://github.com/adriannowakzest-boop/Arclight.git
cd Arclight
git checkout paperbridge-1.20.1

./gradlew cleanBuild remapSpigotJar idea --no-daemon
./gradlew :arclight-common:test --no-daemon
./gradlew build collect --no-daemon
```

The resulting server artifacts are collected in `build/libs/`.

To reconstruct the pinned upstream Paper source reference, run:

```bash
bash scripts/prepare-paper-upstream.sh
```

See [PAPER_UPSTREAM.md](https://github.com/adriannowakzest-boop/Arclight/blob/paperbridge-1.20.1/PAPER_UPSTREAM.md) for the pinned source revision and details. Reconstructing the Paper source tree is a reference/preparation step; it does not mean Paper's server patches have already been ported into the Forge runtime.

**Do not use experimental PaperBridge builds for production servers.** Wait until clean CI builds, server startup/shutdown, Forge integration, and plugin compatibility tests have been completed.

## Development and contribution

When working on the PaperBridge target, use the `paperbridge-1.20.1` branch and keep changes scoped to Minecraft 1.20.1 / Forge 47.4.28 unless the target is intentionally changed.

Before submitting changes:

1. Build the project with JDK 17.
2. Run the available automated tests.
3. Include relevant server or plugin test results for runtime changes.
4. Document unsupported behavior rather than silently ignoring it.

Bug reports and feature requests for the upstream Arclight project should be submitted through [GitHub Issues](https://github.com/IzzelAliz/Arclight/issues/new/choose). Questions and project discussions are available through [GitHub Discussions](https://github.com/IzzelAliz/Arclight/discussions).

## Support

- [Arclight documentation](https://wiki.izzel.io/s/arclight-docs)
- [GitHub Issues](https://github.com/IzzelAliz/Arclight/issues)
- [GitHub Discussions](https://github.com/IzzelAliz/Arclight/discussions)
- [Discord](https://discord.gg/ZvTY5SC)

## License

Arclight is distributed under the [GNU General Public License v3.0](LICENSE).

When redistributing or modifying this project, retain the applicable license notices and comply with the license terms of Arclight and its upstream dependencies.

## Sponsors

[![Akira Cloud](.github/akiracloud.webp)](https://portal.akiracloud.net/aff.php?aff=128)

[![BisectHosting](.github/bisecthosting.webp)](https://bisecthosting.com/arclight)

Get 25% off hosting at [BisectHosting](https://bisecthosting.com/arclight) with the code **arclight**.

[![YourKit](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com)

YourKit supports open-source projects with tools for monitoring and profiling Java and .NET applications.
