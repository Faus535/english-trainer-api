package com.faus535.englishtrainer.reading.domain;

import java.util.UUID;

public record ReadingPassageId(UUID value) {

    public ReadingPassageId {
        if (value == null) {
            throw new IllegalArgumentException("ReadingPassageId cannot be null");
        }
    }

    public static ReadingPassageId generate() {
        return new ReadingPassageId(UUID.randomUUID());
    }

    public static ReadingPassageId fromString(String id) {
        return new ReadingPassageId(UUID.fromString(id));
    }
}
