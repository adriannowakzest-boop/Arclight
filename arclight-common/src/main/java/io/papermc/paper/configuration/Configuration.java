package io.papermc.paper.configuration;

/**
 * Shared version metadata for Paper configuration files.
 */
public final class Configuration {

    public static final String VERSION_FIELD = "_version";

    /**
     * Legacy version field retained for plugins and migration code that inspect it.
     */
    @Deprecated
    public static final String LEGACY_CONFIG_VERSION_FIELD = "config-version";

    @Deprecated
    public static final int FINAL_LEGACY_VERSION = 27;

    private Configuration() {
    }
}
