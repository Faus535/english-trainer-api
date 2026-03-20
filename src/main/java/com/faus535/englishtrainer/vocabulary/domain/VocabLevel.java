package com.faus535.englishtrainer.vocabulary.domain;

import java.util.Set;

public record VocabLevel(String value) {

    private static final Set<String> VALID_LEVELS = Set.of("a1", "a2", "b1", "b2", "c1", "c2");

    public VocabLevel {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("VocabLevel cannot be null or blank");
        }
        value = value.toLowerCase();
        if (!VALID_LEVELS.contains(value)) {
            throw new IllegalArgumentException("Invalid level: " + value + ". Valid levels: " + VALID_LEVELS);
        }
    }
}
