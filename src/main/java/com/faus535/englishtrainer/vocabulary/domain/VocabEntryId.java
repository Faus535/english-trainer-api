package com.faus535.englishtrainer.vocabulary.domain;

import java.util.UUID;

public record VocabEntryId(UUID value) {

    public VocabEntryId {
        if (value == null) {
            throw new IllegalArgumentException("VocabEntryId cannot be null");
        }
    }

    public static VocabEntryId generate() {
        return new VocabEntryId(UUID.randomUUID());
    }

    public static VocabEntryId fromString(String id) {
        return new VocabEntryId(UUID.fromString(id));
    }
}
