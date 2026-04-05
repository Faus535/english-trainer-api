package com.faus535.englishtrainer.immerse.domain;

import java.util.UUID;

public record ImmerseSubmissionId(UUID value) {

    public ImmerseSubmissionId {
        if (value == null) throw new IllegalArgumentException("ImmerseSubmissionId cannot be null");
    }

    public static ImmerseSubmissionId generate() {
        return new ImmerseSubmissionId(UUID.randomUUID());
    }
}
