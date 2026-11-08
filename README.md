# Arclight

A Bukkit server implementation on common mod loaders.

[![Downloads count](https://img.shields.io/github/downloads/IzzelAliz/Arclight/total?style=flat-square)](https://arclight.izzel.io/)  ![License](https://img.shields.io/github/license/IzzelAliz/Arclight?style=flat-square)

![Logo](.github/arclightlogo.jpg)

## Download

Downloads are available at [https://arclight.izzel.io](https://arclight.izzel.io).

Versions list and support status: [Discussions thread](https://github.com/IzzelAliz/Arclight/discussions/1575)

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

[![Akira Cloud — Minecraft server hosting](.github/akiracloud.webp)](https://portal.akiracloud.net/aff.php?aff=128)

[![](.github/bisecthosting.webp)](https://bisecthosting.com/arclight)

Get 25% off hosting server with promocode **arclight** at [BisectHosting](https://bisecthosting.com/arclight).

[![](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com)

YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET
applications. YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>,
<a href="https://www.yourkit.com/.net/profiler/">YourKit .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.
## PaperBridge — experimental Minecraft 1.20.1 branch

Development of the Forge + Bukkit/Paper hybrid is taking place on the [paperbridge-1.20.1](https://github.com/adriannowakzest-boop/Arclight/tree/paperbridge-1.20.1) branch.

### Target versions

- Minecraft: `1.20.1`
- Forge: `47.4.28`
- Java: `17`
- SpongePowered Mixin: `0.8.5`
- Paper API: `1.20.1-R0.1-SNAPSHOT`

### Current compatibility status

This branch is experimental. It builds on Arclight's existing Forge/Bukkit integration and includes an initial bridge for basic Paper plugin descriptors. It is **not yet a complete Paper server implementation**.

- Bukkit/Spigot plugin support remains the compatibility baseline.
- Basic `paper-plugin.yml` metadata can be adapted to the legacy Bukkit JavaPlugin loader.
- Paper bootstrapper classes, custom plugin loaders, isolated Paper classpaths, and Paper-specific runtime/lifecycle APIs are not implemented by this adapter.
- Paper API being available at compile time does not mean every Paper API method is implemented at runtime.
- Paper-only plugins must be tested individually; general Paper plugin compatibility is not guaranteed.

### Paper 1.20.1 source reference

The branch pins the official Paper source revision `773dd724469bae89d0c2075edc3d1ddc8d5b0b18` (Paper 1.20.1 build 196). See [PAPER_UPSTREAM.md](https://github.com/adriannowakzest-boop/Arclight/blob/paperbridge-1.20.1/PAPER_UPSTREAM.md) for the source provenance and preparation instructions.

### Build the experimental branch

```bash
git clone https://github.com/adriannowakzest-boop/Arclight.git
cd Arclight
git checkout paperbridge-1.20.1
./gradlew cleanBuild remapSpigotJar idea --no-daemon
./gradlew :arclight-common:test --no-daemon
./gradlew build collect --no-daemon
```

Do not use this branch as a production server until its CI build, startup, plugin compatibility, and Forge integration tests have passed.
