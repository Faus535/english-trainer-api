package com.faus535.englishtrainer.immerse.domain;

import java.util.UUID;

public record ImmerseContentId(UUID value) {

    public ImmerseContentId {
        if (value == null) throw new IllegalArgumentException("ImmerseContentId cannot be null");
    }

    public static ImmerseContentId generate() {
        return new ImmerseContentId(UUID.randomUUID());
    }
}
