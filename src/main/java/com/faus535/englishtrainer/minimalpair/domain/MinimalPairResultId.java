package com.faus535.englishtrainer.minimalpair.domain;

import java.util.UUID;

public record MinimalPairResultId(UUID value) {

    public MinimalPairResultId {
        if (value == null) {
            throw new IllegalArgumentException("MinimalPairResultId cannot be null");
        }
    }

    public static MinimalPairResultId generate() {
        return new MinimalPairResultId(UUID.randomUUID());
    }
}
