package com.faus535.englishtrainer.phonetics.domain;

import java.util.UUID;

public record PhonemePracticePhraseId(UUID value) {

    public PhonemePracticePhraseId {
        if (value == null) {
            throw new IllegalArgumentException("PhonemePracticePhraseId cannot be null");
        }
    }

    public static PhonemePracticePhraseId generate() {
        return new PhonemePracticePhraseId(UUID.randomUUID());
    }
}
