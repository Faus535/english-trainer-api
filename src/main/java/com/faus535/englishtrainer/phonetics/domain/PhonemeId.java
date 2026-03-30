package com.faus535.englishtrainer.phonetics.domain;

import java.util.UUID;

public record PhonemeId(UUID value) {
    public PhonemeId {
        if (value == null) throw new IllegalArgumentException("PhonemeId cannot be null");
    }

    public static PhonemeId fromString(String id) {
        return new PhonemeId(UUID.fromString(id));
    }
}
