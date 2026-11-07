package io.izzel.arclight.common.mod.plugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Adapts the basic Paper plugin descriptor to Bukkit's legacy JavaPlugin loader.
 *
 * <p>This intentionally does not pretend to implement Paper's separate bootstrap,
 * loader, or isolated classpath systems. Those require Paper's server-side
 * plugin-provider runtime and are rejected with an explicit diagnostic.</p>
 */
public final class PaperPluginDescriptor {

    private PaperPluginDescriptor() {
    }

    /**
     * Reads a Paper-only descriptor and converts the subset supported by the
     * legacy Bukkit loader. Returns {@code null} when the jar has a plugin.yml
     * or does not contain paper-plugin.yml.
     */
    public static PluginDescriptionFile read(File pluginJar) throws InvalidDescriptionException {
        if (pluginJar == null || !pluginJar.isFile()) {
            return null;
        }

        try (JarFile jar = new JarFile(pluginJar)) {
            // Prefer the standard Bukkit descriptor for dual-format plugins.
            if (jar.getJarEntry("plugin.yml") != null) {
                return null;
            }

            JarEntry paperDescriptor = jar.getJarEntry("paper-plugin.yml");
            if (paperDescriptor == null) {
                return null;
            }

            try (InputStream input = jar.getInputStream(paperDescriptor);
                 InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                YamlConfiguration paper = new YamlConfiguration();
                paper.load(reader);
                return convert(paper);
            }
        } catch (InvalidDescriptionException exception) {
            throw exception;
        } catch (IOException | InvalidConfigurationException | RuntimeException exception) {
            throw new InvalidDescriptionException(exception);
        }
    }

    private static PluginDescriptionFile convert(YamlConfiguration paper) throws InvalidDescriptionException {
        if (paper.contains("bootstrapper")) {
            throw new InvalidDescriptionException(
                "PaperBridge 1.20.1 cannot run paper-plugin.yml bootstrapper classes yet. " +
                "Use a JavaPlugin entry point without 'bootstrapper'."
            );
        }
        if (paper.contains("loader")) {
            throw new InvalidDescriptionException(
                "PaperBridge 1.20.1 cannot run custom Paper plugin loaders yet. " +
                "Use the legacy JavaPlugin class loader without 'loader'."
            );
        }

        ConfigurationSection bootstrapDependencies = paper.getConfigurationSection("dependencies.bootstrap");
        if (bootstrapDependencies != null && !bootstrapDependencies.getKeys(false).isEmpty()) {
            throw new InvalidDescriptionException(
                "PaperBridge 1.20.1 does not implement Paper bootstrap dependencies."
            );
        }

        String name = paper.getString("name");
        String version = paper.getString("version");
        String main = paper.getString("main");
        if (name == null || version == null || main == null) {
            throw new InvalidDescriptionException(
                "paper-plugin.yml must define non-empty name, version, and main fields."
            );
        }

        YamlConfiguration bukkit = new YamlConfiguration();
        copy(paper, bukkit, "name");
        copy(paper, bukkit, "version");
        copy(paper, bukkit, "main");
        copy(paper, bukkit, "description");
        copy(paper, bukkit, "website");
        copy(paper, bukkit, "authors");
        copy(paper, bukkit, "author");
        copy(paper, bukkit, "prefix");
        copy(paper, bukkit, "permissions");
        copy(paper, bukkit, "provides");

        String apiVersion = paper.getString("api-version");
        if (apiVersion != null) {
            // Bukkit's 1.20.1 API version is "1.20"; patch-level API versions
            // were only introduced by Paper for later Minecraft releases.
            if (apiVersion.matches("1\\.20\\.\\d+")) {
                apiVersion = "1.20";
            }
            bukkit.set("api-version", apiVersion);
        }

        String load = paper.getString("load", "POSTWORLD");
        if (!"STARTUP".equalsIgnoreCase(load) && !"POSTWORLD".equalsIgnoreCase(load)) {
            throw new InvalidDescriptionException("Unsupported Paper plugin load value: " + load);
        }
        bukkit.set("load", load.toUpperCase(Locale.ROOT));

        List<String> required = new ArrayList<>();
        List<String> optional = new ArrayList<>();
        List<String> loadBefore = new ArrayList<>();
        ConfigurationSection dependencies = paper.getConfigurationSection("dependencies.server");
        if (dependencies != null) {
            for (String dependencyName : dependencies.getKeys(false)) {
                Object dependencyValue = dependencies.getValues(false).get(dependencyName);
                if (!(dependencyValue instanceof ConfigurationSection)) {
                    throw new InvalidDescriptionException(
                        "Invalid server dependency entry for '" + dependencyName + "'."
                    );
                }

                ConfigurationSection dependency = (ConfigurationSection) dependencyValue;
                if (!dependency.getBoolean("join-classpath", true)) {
                    throw new InvalidDescriptionException(
                        "PaperBridge 1.20.1 uses Arclight's legacy plugin class loader and cannot enforce " +
                        "join-classpath: false for dependency '" + dependencyName + "'."
                    );
                }

                boolean isRequired = dependency.getBoolean("required", true);
                String relativeOrder = dependency.getString("load", "OMIT").toUpperCase(Locale.ROOT);
                if (!"OMIT".equals(relativeOrder) && !"BEFORE".equals(relativeOrder) && !"AFTER".equals(relativeOrder)) {
                    throw new InvalidDescriptionException(
                        "Unsupported load order '" + relativeOrder + "' for Paper dependency '" + dependencyName + "'."
                    );
                }

                if ("AFTER".equals(relativeOrder)) {
                    if (isRequired) {
                        throw new InvalidDescriptionException(
                            "Required Paper dependency '" + dependencyName + "' uses load: AFTER. " +
                            "The legacy Bukkit loader cannot preserve this ordering and required-dependency semantics."
                        );
                    }
                    loadBefore.add(dependencyName);
                } else if (isRequired) {
                    required.add(dependencyName);
                } else {
                    optional.add(dependencyName);
                }
            }
        }

        if (!required.isEmpty()) {
            bukkit.set("depend", required);
        }
        if (!optional.isEmpty()) {
            bukkit.set("softdepend", optional);
        }
        if (!loadBefore.isEmpty()) {
            bukkit.set("loadbefore", loadBefore);
        }

        return new PluginDescriptionFile(new StringReader(bukkit.saveToString()));
    }

    private static void copy(YamlConfiguration source, YamlConfiguration target, String key) {
        if (source.contains(key)) {
            target.set(key, source.get(key));
        }
    }
}
