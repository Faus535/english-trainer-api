package com.faus535.englishtrainer.vocabulary.domain;

import java.util.UUID;

public record VocabMasteryId(UUID value) {

    public VocabMasteryId {
        if (value == null) {
            throw new IllegalArgumentException("VocabMasteryId cannot be null");
        }
    }

    public static VocabMasteryId generate() {
        return new VocabMasteryId(UUID.randomUUID());
    }

    public static VocabMasteryId fromString(String id) {
        return new VocabMasteryId(UUID.fromString(id));
    }
}
