package com.faus535.englishtrainer.vocabulary.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

final class VocabLevelTest {

    @ParameterizedTest
    @ValueSource(strings = {"a1", "a2", "b1", "b2", "c1", "c2"})
    void shouldCreateValidLevel(String level) {
        VocabLevel vocabLevel = new VocabLevel(level);

        assertEquals(level, vocabLevel.value());
    }

    @Test
    void shouldThrowWhenInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> new VocabLevel("z9"));
    }

    @Test
    void shouldNormalizeToLowerCase() {
        VocabLevel level = new VocabLevel("A2");

        assertEquals("a2", level.value());
    }
}
