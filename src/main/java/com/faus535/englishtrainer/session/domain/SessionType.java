package com.faus535.englishtrainer.session.domain;

import java.util.Set;

public record SessionType(String value) {

    private static final Set<String> VALID = Set.of("normal", "integrator");

    public SessionType {
        if (value == null || !VALID.contains(value.toLowerCase())) {
            throw new IllegalArgumentException("Invalid session type: " + value);
        }
        value = value.toLowerCase();
    }
}
