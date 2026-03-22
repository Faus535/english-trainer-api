package com.faus535.englishtrainer.conversation.domain;

import java.util.Set;

public record ConversationLevel(String value) {

    private static final Set<String> VALID_LEVELS = Set.of("a1", "a2", "b1", "b2", "c1", "c2");

    public ConversationLevel {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ConversationLevel cannot be null or blank");
        }
        value = value.toLowerCase();
        if (!VALID_LEVELS.contains(value)) {
            throw new IllegalArgumentException("Invalid CEFR level: " + value);
        }
    }
}
