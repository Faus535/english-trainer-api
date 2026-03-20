package com.faus535.englishtrainer.vocabulary.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class VocabEntryTest {

    @Test
    void shouldCreateEntry() {
        VocabEntry entry = VocabEntryMother.create();

        assertNotNull(entry);
        assertNotNull(entry.id());
    }

    @Test
    void shouldHaveCorrectFields() {
        VocabEntry entry = VocabEntryMother.create();

        assertEquals("hello", entry.en());
        assertEquals("/h\u0259\u02c8lo\u028a/", entry.ipa());
        assertEquals("hola", entry.es());
        assertEquals("noun", entry.type());
        assertEquals("Hello there", entry.example());
        assertEquals(new VocabLevel("a1"), entry.level());
    }
}
