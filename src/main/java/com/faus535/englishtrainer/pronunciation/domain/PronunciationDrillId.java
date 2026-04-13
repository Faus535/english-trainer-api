package com.faus535.englishtrainer.pronunciation.domain;

import java.util.UUID;

public record PronunciationDrillId(UUID value) {

    public PronunciationDrillId {
        if (value == null) {
            throw new IllegalArgumentException("PronunciationDrillId cannot be null");
        }
    }

    public static PronunciationDrillId generate() {
        return new PronunciationDrillId(UUID.randomUUID());
    }
}
