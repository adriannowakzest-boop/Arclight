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

        ConfigurationSection dependencies = null;
        if (paper.contains("dependencies")) {
            Object value = paper.get("dependencies");
            if (!(value instanceof ConfigurationSection)) {
                throw new InvalidDescriptionException("'dependencies' must be a YAML section.");
            }
            dependencies = (ConfigurationSection) value;
            for (String scope : dependencies.getKeys(false)) {
                if (!"server".equals(scope) && !"bootstrap".equals(scope)) {
                    throw new InvalidDescriptionException("Unsupported Paper dependency scope: " + scope);
                }
            }
        }

        if (dependencies != null && dependencies.contains("bootstrap")) {
            ConfigurationSection bootstrapDependencies = dependencies.getConfigurationSection("bootstrap");
            if (bootstrapDependencies == null) {
                throw new InvalidDescriptionException("'dependencies.bootstrap' must be a YAML section.");
            }
            if (!bootstrapDependencies.getKeys(false).isEmpty()) {
                throw new InvalidDescriptionException(
                    "PaperBridge 1.20.1 does not implement Paper bootstrap dependencies."
                );
            }
        }

        String name = requiredString(paper, "name");
        String version = requiredString(paper, "version");
        String main = requiredString(paper, "main");
        if (!main.matches("[\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*(\\.[\\p{javaJavaIdentifierStart}][\\p{javaJavaIdentifierPart}]*)*")) {
            throw new InvalidDescriptionException("'main' must be a valid Java binary class name: " + main);
        }
        validateStringList(paper, "authors");
        validateStringList(paper, "provides");
        validateStringList(paper, "libraries");
        if (paper.contains("author") && !(paper.get("author") instanceof String)) {
            throw new InvalidDescriptionException("'author' must be a string.");
        }
        if (paper.contains("description") && !(paper.get("description") instanceof String)) {
            throw new InvalidDescriptionException("'description' must be a string.");
        }
        if (paper.contains("website") && !(paper.get("website") instanceof String)) {
            throw new InvalidDescriptionException("'website' must be a string.");
        }
        if (paper.contains("prefix") && !(paper.get("prefix") instanceof String)) {
            throw new InvalidDescriptionException("'prefix' must be a string.");
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
        copy(paper, bukkit, "commands");
        copy(paper, bukkit, "provides");
        // Paper 1.20.1 PluginDescriptionFile and LibraryLoader support Maven GAV libraries.
        copy(paper, bukkit, "libraries");

        if (paper.contains("api-version") && !(paper.get("api-version") instanceof String)) {
            throw new InvalidDescriptionException("'api-version' must be a string.");
        }
        String apiVersion = paper.getString("api-version");
        if (apiVersion != null) {
            // Bukkit's 1.20.1 API version is "1.20"; patch-level API versions
            // were only introduced by Paper for later Minecraft releases.
            if (apiVersion.matches("1\\.20\\.\\d+")) {
                apiVersion = "1.20";
            }
            bukkit.set("api-version", apiVersion);
        }

        if (paper.contains("load") && !(paper.get("load") instanceof String)) {
            throw new InvalidDescriptionException("'load' must be STARTUP or POSTWORLD.");
        }
        String load = paper.getString("load", "POSTWORLD");
        if (!"STARTUP".equalsIgnoreCase(load) && !"POSTWORLD".equalsIgnoreCase(load)) {
            throw new InvalidDescriptionException("Unsupported Paper plugin load value: " + load);
        }
        bukkit.set("load", load.toUpperCase(Locale.ROOT));

        List<String> required = new ArrayList<>();
        List<String> optional = new ArrayList<>();
        List<String> loadBefore = new ArrayList<>();
        ConfigurationSection serverDependencies = null;
        if (dependencies != null && dependencies.contains("server")) {
            serverDependencies = dependencies.getConfigurationSection("server");
            if (serverDependencies == null) {
                throw new InvalidDescriptionException("'dependencies.server' must be a YAML section.");
            }
        }
        if (serverDependencies != null) {
            for (String dependencyName : serverDependencies.getKeys(false)) {
                ConfigurationSection dependency = serverDependencies.getConfigurationSection(dependencyName);
                if (dependency == null) {
                    throw new InvalidDescriptionException(
                        "Invalid server dependency entry for '" + dependencyName + "'. Expected a YAML section."
                    );
                }

                for (String dependencyOption : dependency.getKeys(false)) {
                    if (!"required".equals(dependencyOption) &&
                        !"load".equals(dependencyOption) &&
                        !"join-classpath".equals(dependencyOption)) {
                        throw new InvalidDescriptionException(
                            "Unsupported option '" + dependencyOption + "' for Paper dependency '" + dependencyName + "'."
                        );
                    }
                }

                boolean joinClasspath = readBoolean(dependency, "join-classpath", true, dependencyName);
                if (!joinClasspath) {
                    throw new InvalidDescriptionException(
                        "PaperBridge 1.20.1 uses Arclight's legacy plugin class loader and cannot enforce " +
                        "join-classpath: false for dependency '" + dependencyName + "'."
                    );
                }

                boolean isRequired = readBoolean(dependency, "required", true, dependencyName);
                Object loadValue = dependency.get("load");
                if (loadValue != null && !(loadValue instanceof String)) {
                    throw new InvalidDescriptionException(
                        "Invalid load value for Paper dependency '" + dependencyName + "'. Expected BEFORE, AFTER, or OMIT."
                    );
                }
                String relativeOrder = (loadValue == null ? "OMIT" : ((String) loadValue)).toUpperCase(Locale.ROOT);
                if (!"OMIT".equals(relativeOrder) && !"BEFORE".equals(relativeOrder) && !"AFTER".equals(relativeOrder)) {
                    throw new InvalidDescriptionException(
                        "Unsupported load order '" + relativeOrder + "' for Paper dependency '" + dependencyName + "'."
                    );
                }

                if ("BEFORE".equals(relativeOrder)) {
                    if (isRequired) {
                        throw new InvalidDescriptionException(
                            "Required Paper dependency '" + dependencyName + "' uses load: BEFORE. " +
                            "The legacy Bukkit loader cannot enforce both required presence and loading before that dependency."
                        );
                    }
                    loadBefore.add(dependencyName);
                } else if (isRequired) {
                    // Bukkit depend loads this plugin after the dependency, matching Paper AFTER.
                    required.add(dependencyName);
                } else {
                    // softdepend preserves optional availability and AFTER ordering where possible.
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

    private static String requiredString(ConfigurationSection section, String key) throws InvalidDescriptionException {
        Object value = section.get(key);
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new InvalidDescriptionException("'"+ key + "' must be a non-empty string.");
        }
        return ((String) value).trim();
    }

    private static void validateStringList(ConfigurationSection section, String key) throws InvalidDescriptionException {
        if (!section.contains(key)) {
            return;
        }
        Object value = section.get(key);
        if (!(value instanceof List)) {
            throw new InvalidDescriptionException("'" + key + "' must be a list of strings.");
        }
        for (Object item : (List<?>) value) {
            if (!(item instanceof String) || ((String) item).trim().isEmpty()) {
                throw new InvalidDescriptionException("'" + key + "' must contain only non-empty strings.");
            }
        }
    }

    private static boolean readBoolean(ConfigurationSection section, String key, boolean defaultValue,
                                        String dependencyName) throws InvalidDescriptionException {
        Object value = section.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof Boolean)) {
            throw new InvalidDescriptionException(
                "Invalid '" + key + "' value for Paper dependency '" + dependencyName + "'. Expected true or false."
            );
        }
        return (Boolean) value;
    }

    private static void copy(YamlConfiguration source, YamlConfiguration target, String key) {
        if (source.contains(key)) {
            target.set(key, source.get(key));
        }
    }
}
