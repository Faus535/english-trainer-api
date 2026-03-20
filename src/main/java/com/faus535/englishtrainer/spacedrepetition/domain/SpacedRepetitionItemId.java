package com.faus535.englishtrainer.spacedrepetition.domain;

import java.util.UUID;

public record SpacedRepetitionItemId(UUID value) {

    public SpacedRepetitionItemId {
        if (value == null) {
            throw new IllegalArgumentException("SpacedRepetitionItemId cannot be null");
        }
    }

    public static SpacedRepetitionItemId generate() {
        return new SpacedRepetitionItemId(UUID.randomUUID());
    }

    public static SpacedRepetitionItemId fromString(String id) {
        return new SpacedRepetitionItemId(UUID.fromString(id));
    }
}
