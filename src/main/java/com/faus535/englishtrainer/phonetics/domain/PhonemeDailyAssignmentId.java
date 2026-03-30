package com.faus535.englishtrainer.phonetics.domain;

import java.util.UUID;

public record PhonemeDailyAssignmentId(UUID value) {
    public PhonemeDailyAssignmentId {
        if (value == null) throw new IllegalArgumentException("PhonemeDailyAssignmentId cannot be null");
    }

    public static PhonemeDailyAssignmentId generate() {
        return new PhonemeDailyAssignmentId(UUID.randomUUID());
    }
}
