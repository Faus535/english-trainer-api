package com.faus535.englishtrainer.tutorerror.domain;

import java.util.UUID;

public record TutorErrorId(UUID value) {

    public TutorErrorId {
        if (value == null) {
            throw new IllegalArgumentException("TutorErrorId cannot be null");
        }
    }

    public static TutorErrorId generate() {
        return new TutorErrorId(UUID.randomUUID());
    }

    public static TutorErrorId fromString(String id) {
        return new TutorErrorId(UUID.fromString(id));
    }
}
