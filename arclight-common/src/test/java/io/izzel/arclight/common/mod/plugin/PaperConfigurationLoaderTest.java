package io.izzel.arclight.common.mod.plugin;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationLoaders;
import org.junit.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class PaperConfigurationLoaderTest {

    @Test
    public void exposesPaperConfigurationVersionMetadata() {
        assertEquals("_version", Configuration.VERSION_FIELD);
        assertEquals("config-version", Configuration.LEGACY_CONFIG_VERSION_FIELD);
        assertEquals(27, Configuration.FINAL_LEGACY_VERSION);
    }

    @Test
    public void writesNaturallySortedYamlWithoutHeader() throws Exception {
        Path file = Files.createTempFile("paperbridge-config-", ".yml");
        try {
            YamlConfigurationLoader loader = ConfigurationLoaders.naturallySortedWithoutHeader(file);
            ConfigurationNode node = loader.load();
            node.node("z-last").set("last");
            node.node("a-first").set("first");
            loader.save(node);

            String yaml = Files.readString(file, StandardCharsets.UTF_8);
            assertTrue(yaml.indexOf("a-first:") < yaml.indexOf("z-last:"));
            assertFalse(yaml.startsWith("#"));
        } finally {
            Files.deleteIfExists(file);
        }
    }
    @Test
    public void writesNestedKeysWithPaperIndentationAndNaturalOrdering() throws Exception {
        Path file = Files.createTempFile("paperbridge-config-nested-", ".yml");
        try {
            YamlConfigurationLoader loader = ConfigurationLoaders.naturallySortedWithoutHeader(file);
            ConfigurationNode node = loader.load();
            node.node("settings").node("z-last").set(true);
            node.node("settings").node("a-first").set(true);
            loader.save(node);

            String yaml = Files.readString(file, StandardCharsets.UTF_8);
            assertTrue(yaml.contains("settings:\n  a-first: true"));
            assertTrue(yaml.contains("  a-first: true\n  z-last: true"));
            assertFalse(yaml.startsWith("#"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

}
