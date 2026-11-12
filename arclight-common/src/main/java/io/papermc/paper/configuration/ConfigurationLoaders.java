package io.papermc.paper.configuration;

import java.nio.file.Path;

import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.util.MapFactories;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * Factory methods for Paper-compatible YAML configuration loaders.
 */
public final class ConfigurationLoaders {

    private ConfigurationLoaders() {
    }

    /**
     * Creates a loader builder using Paper's standard indentation, block style,
     * preset header mode, and naturally sorted map keys.
     */
    public static YamlConfigurationLoader.Builder naturallySorted() {
        return YamlConfigurationLoader.builder()
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .headerMode(HeaderMode.PRESET)
            .defaultOptions(options -> options.mapFactory(MapFactories.sortedNatural()));
    }

    /**
     * Creates a naturally sorted YAML loader for a file without a generated header.
     */
    public static YamlConfigurationLoader naturallySortedWithoutHeader(final Path path) {
        return naturallySorted()
            .headerMode(HeaderMode.NONE)
            .path(path)
            .build();
    }
}
