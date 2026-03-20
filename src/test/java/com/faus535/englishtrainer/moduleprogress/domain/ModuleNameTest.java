package com.faus535.englishtrainer.moduleprogress.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModuleNameTest {

    @Test
    void shouldCreateValidModuleName() {
        ModuleName name = new ModuleName("vocabulary");
        assertEquals("vocabulary", name.value());
    }

    @Test
    void shouldNormalizeTteLowerCase() {
        ModuleName name = new ModuleName("GRAMMAR");
        assertEquals("grammar", name.value());
    }

    @Test
    void shouldAcceptAllValidModuleNames() {
        new ModuleName("listening");
        new ModuleName("vocabulary");
        new ModuleName("grammar");
        new ModuleName("phrases");
        new ModuleName("pronunciation");
    }

    @Test
    void shouldThrowWhenInvalidModuleName() {
        assertThrows(IllegalArgumentException.class, () -> new ModuleName("invalid"));
    }

    @Test
    void shouldThrowWhenNullModuleName() {
        assertThrows(IllegalArgumentException.class, () -> new ModuleName(null));
    }

    @Test
    void shouldThrowWhenBlankModuleName() {
        assertThrows(IllegalArgumentException.class, () -> new ModuleName("  "));
    }
}
