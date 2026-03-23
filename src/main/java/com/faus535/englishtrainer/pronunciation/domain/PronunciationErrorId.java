package com.faus535.englishtrainer.pronunciation.domain;

import java.util.UUID;

public record PronunciationErrorId(UUID value) {

    public PronunciationErrorId {
        if (value == null) {
            throw new IllegalArgumentException("PronunciationErrorId cannot be null");
        }
    }

    public static PronunciationErrorId generate() {
        return new PronunciationErrorId(UUID.randomUUID());
    }

    public static PronunciationErrorId fromString(String id) {
        return new PronunciationErrorId(UUID.fromString(id));
    }
}
