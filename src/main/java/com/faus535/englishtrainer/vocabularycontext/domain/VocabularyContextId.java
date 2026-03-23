package com.faus535.englishtrainer.vocabularycontext.domain;

import java.util.UUID;

public record VocabularyContextId(UUID value) {

    public VocabularyContextId {
        if (value == null) {
            throw new IllegalArgumentException("VocabularyContextId cannot be null");
        }
    }

    public static VocabularyContextId generate() {
        return new VocabularyContextId(UUID.randomUUID());
    }

    public static VocabularyContextId fromString(String id) {
        return new VocabularyContextId(UUID.fromString(id));
    }
}
