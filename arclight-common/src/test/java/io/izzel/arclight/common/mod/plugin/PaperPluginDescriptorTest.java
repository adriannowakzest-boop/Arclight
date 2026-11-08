package io.izzel.arclight.common.mod.plugin;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;

public class PaperPluginDescriptorTest {

    @Test
    public void translatesSupportedServerDependencies() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: SamplePaperPlugin\n" +
            "version: '1.0.0'\n" +
            "main: example.SamplePlugin\n" +
            "api-version: '1.20.1'\n" +
            "libraries:\n" +
            "  - org.example:sample-library:1.2.3\n" +
            "dependencies:\n" +
            "  server:\n" +
            "    CoreLib:\n" +
            "      required: true\n" +
            "      load: OMIT\n" +
            "      join-classpath: true\n" +
            "    PlaceholderAPI:\n" +
            "      required: false\n" +
            "      load: OMIT\n" +
            "      join-classpath: true\n" +
            "    LatePlugin:\n" +
            "      required: false\n" +
            "      load: AFTER\n" +
            "      join-classpath: true\n" +
            "    EarlyPlugin:\n" +
            "      required: false\n" +
            "      load: BEFORE\n" +
            "      join-classpath: true\n"
        );

        PluginDescriptionFile description = PaperPluginDescriptor.read(jar);
        assertNotNull(description);
        assertEquals("SamplePaperPlugin", description.getName());
        assertEquals("1.0.0", description.getVersion());
        assertEquals("example.SamplePlugin", description.getMain());
        assertEquals("1.20", description.getAPIVersion());
        assertTrue(description.getLibraries().contains("org.example:sample-library:1.2.3"));
        assertTrue(description.getDepend().contains("CoreLib"));
        assertTrue(description.getSoftDepend().contains("PlaceholderAPI"));
        assertTrue(description.getSoftDepend().contains("LatePlugin"));
        assertTrue(description.getLoadBefore().contains("EarlyPlugin"));
    }

    @Test
    public void prefersPluginYmlWhenBothDescriptorsExist() throws Exception {
        File jar = createJar(
            "plugin.yml", "name: LegacyPlugin\nversion: '2.0'\nmain: example.LegacyPlugin\n",
            "paper-plugin.yml", "name: PaperPlugin\nversion: '1.0'\nmain: example.PaperPlugin\n"
        );

        assertNull(PaperPluginDescriptor.read(jar));
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsBootstrapperUntilPaperBootstrapRuntimeExists() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: BootstrapPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "bootstrapper: example.PluginBootstrap\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test
    public void translatesRequiredAfterDependency() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: OrderedDependencyPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  server:\n" +
            "    CoreLib:\n" +
            "      required: true\n" +
            "      load: AFTER\n" +
            "      join-classpath: true\n"
        );

        PluginDescriptionFile description = PaperPluginDescriptor.read(jar);
        assertNotNull(description);
        assertTrue(description.getDepend().contains("CoreLib"));
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsRequiredBeforeDependencyThatLegacyLoaderCannotExpress() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: EarlyDependencyPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  server:\n" +
            "    CoreLib:\n" +
            "      required: true\n" +
            "      load: BEFORE\n" +
            "      join-classpath: true\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsDisabledDependencyClasspath() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: IsolatedPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  server:\n" +
            "    CoreLib:\n" +
            "      required: true\n" +
            "      join-classpath: false\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsMalformedServerDependencySection() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: MalformedPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  server: not-a-map\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsNonBooleanDependencyFlags() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: MalformedPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  server:\n" +
            "    CoreLib:\n" +
            "      required: 'yes'\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsUnknownDependencyScopes() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: UnknownScopePlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "dependencies:\n" +
            "  client:\n" +
            "    CoreLib: {}\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsNonStringRequiredFields() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: 123\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsInvalidJavaMainClassName() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: InvalidMainPlugin\n" +
            "version: '1.0'\n" +
            "main: example.not-valid\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsNonStringApiVersion() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: InvalidApiPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "api-version: 120\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsInvalidLoadPhase() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: InvalidLoadPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "load: LATE\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    @Test(expected = InvalidDescriptionException.class)
    public void rejectsNonStringLibraryEntries() throws Exception {
        File jar = createJar("paper-plugin.yml",
            "name: InvalidLibraryPlugin\n" +
            "version: '1.0'\n" +
            "main: example.Plugin\n" +
            "libraries:\n" +
            "  - 123\n"
        );
        PaperPluginDescriptor.read(jar);
    }

    private static File createJar(String... entries) throws Exception {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Entries must be name/content pairs");
        }
        File jar = Files.createTempFile("paperbridge-plugin-", ".jar").toFile();
        jar.deleteOnExit();
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            for (int i = 0; i < entries.length; i += 2) {
                output.putNextEntry(new JarEntry(entries[i]));
                output.write(entries[i + 1].getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
        return jar;
    }
}
