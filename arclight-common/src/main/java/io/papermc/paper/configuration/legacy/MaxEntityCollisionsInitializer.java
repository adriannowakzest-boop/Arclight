package io.papermc.paper.configuration.legacy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.util.NamingSchemes;

import java.lang.reflect.Field;
import org.bukkit.configuration.file.YamlConfiguration;

public class MaxEntityCollisionsInitializer implements NodeResolver {

    private final String name;
    private final SpigotWorldConfig spigotConfig;

    public MaxEntityCollisionsInitializer(String name, SpigotWorldConfig spigotConfig) {
        this.name = name;
        this.spigotConfig = spigotConfig;
    }

    @Override
    public @Nullable ConfigurationNode resolve(ConfigurationNode parent) {
        final String key = NamingSchemes.LOWER_CASE_DASHED.coerce(this.name);
        final ConfigurationNode node = parent.node(key);
        final int old = getLegacyMaxEntityCollisions();
        if (node.virtual() && old > -1) {
            node.raw(old);
        }
        return node;
    }

    private int getLegacyMaxEntityCollisions() {
        try {
            Field field = SpigotWorldConfig.class.getDeclaredField("config");
            field.setAccessible(true);
            return ((YamlConfiguration) field.get(this.spigotConfig)).getInt("max-entity-collisions", -1);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to read legacy max-entity-collisions setting", exception);
        }
    }
}
