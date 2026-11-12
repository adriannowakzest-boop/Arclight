package io.izzel.arclight.common.mod.plugin;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PaperCompatibilityMarkerTest {

    @Test
    public void exposesLegacyPaperIdentificationClasses() throws Exception {
        assertNotNull(Class.forName("com.destroystokyo.paper.PaperConfig"));
        assertNotNull(Class.forName("com.destroystokyo.paper.PaperWorldConfig"));
    }
}
