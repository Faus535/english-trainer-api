package com.faus535.englishtrainer.minimalpair.domain;

import java.util.UUID;

public record MinimalPairId(UUID value) {

    public MinimalPairId {
        if (value == null) {
            throw new IllegalArgumentException("MinimalPairId cannot be null");
        }
    }

    public static MinimalPairId fromString(String id) {
        return new MinimalPairId(UUID.fromString(id));
    }
}
