package com.faus535.englishtrainer.moduleprogress.domain;

import java.util.Set;

public record ModuleLevel(String value) {

    private static final Set<String> VALID = Set.of("a1", "a2", "b1", "b2", "c1", "c2");

    public ModuleLevel {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ModuleLevel cannot be null or blank");
        }
        value = value.toLowerCase();
        if (!VALID.contains(value)) {
            throw new IllegalArgumentException("Invalid level: " + value);
        }
    }

    public static ModuleLevel defaultLevel() {
        return new ModuleLevel("a1");
    }
}
