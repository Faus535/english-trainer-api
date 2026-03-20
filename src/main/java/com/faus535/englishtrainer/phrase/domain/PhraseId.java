package com.faus535.englishtrainer.phrase.domain;

import java.util.UUID;

public record PhraseId(UUID value) {

    public PhraseId {
        if (value == null) {
            throw new IllegalArgumentException("PhraseId cannot be null");
        }
    }

    public static PhraseId generate() {
        return new PhraseId(UUID.randomUUID());
    }

    public static PhraseId fromString(String id) {
        return new PhraseId(UUID.fromString(id));
    }
}
