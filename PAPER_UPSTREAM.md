# Paper upstream for Minecraft 1.20.1

PaperBridge uses a pinned historical Paper source revision as its compatibility reference.

- Upstream: https://github.com/PaperMC/Paper
- Commit: `773dd724469bae89d0c2075edc3d1ddc8d5b0b18`
- Upstream commit date: 2023-09-21
- Minecraft: `1.20.1`
- Paper API version: `1.20.1-R0.1-SNAPSHOT`
- Java toolchain: `17`
- Upstream release line: Paper build `196`

The upstream commit is pinned rather than following a moving branch. This prevents newer Paper patches or API changes from silently entering the 1.20.1 target.

## Prepare the reference source tree

Run from the Arclight repository root:

```bash
bash scripts/prepare-paper-upstream.sh
```

The script clones the official Paper repository into `build/paper-upstream-1.20.1`, checks out the exact commit above, initializes its declared submodules, verifies the checked-out SHA, and runs Paper's `applyPatches` task. Set `PAPER_UPSTREAM_DIR` to use a different disposable working directory.

The Paper upstream tree is a reference/build input. It is **not yet merged into Arclight's Forge server implementation**. Paper's source layout is based on Paperweight and patched Bukkit/CraftBukkit/Minecraft trees, while Arclight uses ForgeGradle and its own Arclight patches. A successful upstream patch application only proves that the pinned Paper source tree can be reconstructed; it does not prove that Paper runtime patches have been ported to Forge.

## Licensing and attribution

Paper is an upstream project with its own licensing and contribution requirements. Keep upstream license and attribution files intact when porting source or patches. Do not copy source files without their applicable notices. Review each patch for compatibility with Arclight's existing changes before porting it.
