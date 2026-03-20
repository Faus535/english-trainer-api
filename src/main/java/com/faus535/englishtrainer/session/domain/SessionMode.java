package com.faus535.englishtrainer.session.domain;

import java.util.Set;

public record SessionMode(String value) {

    private static final Set<String> VALID = Set.of("short", "full", "extended");

    public SessionMode {
        if (value == null || !VALID.contains(value.toLowerCase())) {
            throw new IllegalArgumentException("Invalid session mode: " + value);
        }
        value = value.toLowerCase();
    }
}
