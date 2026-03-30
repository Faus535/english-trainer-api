package com.faus535.englishtrainer.phonetics.domain;

import java.util.UUID;

public record UserPhonemeProgressId(UUID value) {

    public UserPhonemeProgressId {
        if (value == null) {
            throw new IllegalArgumentException("UserPhonemeProgressId cannot be null");
        }
    }

    public static UserPhonemeProgressId generate() {
        return new UserPhonemeProgressId(UUID.randomUUID());
    }
}
