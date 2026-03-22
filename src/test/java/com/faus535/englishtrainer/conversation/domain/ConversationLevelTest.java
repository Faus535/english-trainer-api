package com.faus535.englishtrainer.conversation.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversationLevelTest {

    @Test
    void shouldAcceptValidLevels() {
        assertDoesNotThrow(() -> new ConversationLevel("a1"));
        assertDoesNotThrow(() -> new ConversationLevel("A2"));
        assertDoesNotThrow(() -> new ConversationLevel("B1"));
        assertDoesNotThrow(() -> new ConversationLevel("c2"));
    }

    @Test
    void shouldRejectInvalidLevel() {
        assertThrows(IllegalArgumentException.class, () -> new ConversationLevel("d1"));
    }

    @Test
    void shouldRejectNullLevel() {
        assertThrows(IllegalArgumentException.class, () -> new ConversationLevel(null));
    }

    @Test
    void shouldNormalizeLevelToLowercase() {
        assertEquals("b2", new ConversationLevel("B2").value());
    }
}
