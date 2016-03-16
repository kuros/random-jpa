package com.github.kuros.random.jpa.types;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    @Test
    public void shouldReturnSupportedFalseForVersionMisMatch() throws Exception {
        assertFalse(Version.V1.isSupported(Version.V2));
        assertFalse(Version.V2.isSupported(Version.V1));
    }

    @Test
    public void shouldReturnSupportedTrueForSameVersion() throws Exception {
        assertTrue(Version.V2.isSupported(Version.V2));
    }

    @Test
    public void shouldReturnSupportedTrueForMultipleVersionInput() throws Exception {
        assertTrue(Version.V2.isSupported(Version.V1, Version.V2));
    }
}
