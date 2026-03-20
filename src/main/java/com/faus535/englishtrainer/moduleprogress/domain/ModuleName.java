package com.faus535.englishtrainer.moduleprogress.domain;

import java.util.Set;

public record ModuleName(String value) {

    private static final Set<String> VALID = Set.of("listening", "vocabulary", "grammar", "phrases", "pronunciation");

    public ModuleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ModuleName cannot be null or blank");
        }
        value = value.toLowerCase();
        if (!VALID.contains(value)) {
            throw new IllegalArgumentException("Invalid module: " + value + ". Valid: " + VALID);
        }
    }
}
