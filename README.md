# Arclight

A Bukkit server implementation on common mod loaders.

![Downloads](https://img.shields.io/github/downloads/IzzelAliz/Arclight/total?style=flat-square)  ![GitHub](https://img.shields.io/github/license/IzzelAliz/Arclight?style=flat-square)

![](.github/arclightlogo.jpg)

## Download
Versions list and support status: [Discussions thread](https://wiki.izzel.io/s/arclight-docs/doc/versions-and-its-supporting-status-NCKYxXI3mt)

- [Stable releases](https://github.com/IzzelAliz/Arclight/releases)
- [Nightly build (GitHub login required)](https://github.com/IzzelAliz/Arclight/actions/workflows/gradle.yml?query=branch%3ATrials)

## Installing

* Download the jar.
* Launch with command `java -jar arclight.jar nogui`.
  * The `nogui` argument will disable the server control panel.

Read our document for more information.

## Support

Read the [document](https://wiki.izzel.io/s/arclight-docs).  
Something is not working? Report any problems [here](https://github.com/IzzelAliz/Arclight/issues/new/choose)!.  
Questions and discussions [here](https://github.com/IzzelAliz/Arclight/discussions).

Discord Server: https://discord.gg/ZvTY5SC  
QQ Group Chat: 3556966

## License

This project is licensed under [GPL v3](LICENSE).

## Sponsor

[![](.github/bisecthosting.webp)](https://bisecthosting.com/arclight)

Get 25% off hosting server with promocode **arclight** at [BisectHosting](https://bisecthosting.com/arclight).

[![](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET
applications. YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>,
<a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

[![](.github/jetbrains.png)](https://www.jetbrains.com/?from=Arclight)

[![](.github/nexusnode.gif)](https://nexusnode.com/minecraft.php)

Get 10% off hosting server with promocode **ARCLIGHT** at [NEXUSNODE](https://nexusnode.com/minecraft.php)


## PaperBridge development branch (Minecraft 1.20.1)

The `paperbridge-1.20.1` branch is an experimental Forge hybrid based on Arclight Trials.

- Minecraft: `1.20.1`
- Forge: `47.4.28`
- Java: `17`
- SpongePowered Mixin: `0.8.5` (official Sponge release line)
- Paper API: `1.20.1-R0.1-SNAPSHOT` is bundled as a first binary-linkage step for plugins compiled against Paper API.

**Compatibility status:** this does not yet implement the full Paper server internals or the separate `paper-plugin.yml` loader. Plugins that only use Bukkit/Spigot APIs remain the baseline; Paper-only methods and features must be implemented and tested before a plugin can be called compatible.

### Pinned Paper source reference

Paper 1.20.1 source is pinned to official upstream commit `773dd724469bae89d0c2075edc3d1ddc8d5b0b18` (build 196). See [PAPER_UPSTREAM.md](PAPER_UPSTREAM.md) for provenance and the reproducible source preparation script. This reconstructs the upstream reference tree; it does not mean Paper's server patches have already been ported into Forge.

### Build

```bash
./gradlew cleanBuild remapSpigotJar idea --no-daemon
./gradlew build collect --no-daemon
```


### Experimental Paper descriptor bridge

The `paperbridge-1.20.1` branch has an experimental compatibility adapter for jars containing only `paper-plugin.yml`. It translates the basic `JavaPlugin` metadata and supported server dependencies into the legacy Bukkit descriptor model.

The adapter deliberately rejects Paper bootstrapper classes, custom Paper plugin loaders, non-empty bootstrap dependencies, `join-classpath: false`, required dependencies declared with `load: BEFORE`, malformed dependency sections/flags, and unknown dependency scopes. Required `load: AFTER` maps to Bukkit `depend`; optional `AFTER`/`OMIT` maps to `softdepend`; optional `BEFORE` maps to `loadbefore`. Because Bukkit's legacy descriptor cannot express every Paper dependency rule, `load: OMIT` may gain legacy Bukkit ordering semantics. Maven GAV entries in `libraries` are forwarded to Paper 1.20.1's `PluginDescriptionFile`/`LibraryLoader` path. It does not implement Paper's isolated plugin class loaders, bootstrap lifecycle, or Paper-specific lifecycle/command APIs. Jars containing both descriptors use `plugin.yml`.

This adapter is not equivalent to Paper's native plugin provider and is not a claim of general Paper plugin compatibility.

## Development TODO

The PaperBridge 1.20.1 branch is experimental. The main outstanding tasks are:

- [ ] **Build and startup:** verify clean Java 17 builds, Forge 47.4.28 dependency resolution, packaging, Mixin/refmap configuration, and server start/stop in CI.
- [ ] **Paper runtime:** verify the pinned Paper source preparation workflow, port the required Paper patches, implement missing runtime behavior, and add API compatibility tests.
- [ ] **Plugin loading:** complete loader diagnostics, native `paper-plugin.yml` support, dependency/classloader handling, shutdown safety, and Forge runtime tests.
- [ ] **Forge integration:** validate lifecycle and event ordering, modded blocks/items/entities/dimensions, capabilities, chunk synchronization, ticking, and duplicate-event prevention.
- [ ] **Plugin compatibility:** automate plugin lifecycle tests; test Bukkit, Spigot, Paper, and NMS plugins, including EssentialsX, LuckPerms, WorldEdit, and WorldGuard; publish a compatibility matrix.
- [ ] **Release and documentation:** add regression and Mixin checks to CI, publish only verified artifacts, document installation and unsupported Paper features, and prepare an experimental release.

Completed implementation items and the detailed task checklist are tracked in [TODO.md](TODO.md). A checked item means the implementation or configuration work is present; it does not imply that the build or runtime behavior has been verified unless the item explicitly says so.
