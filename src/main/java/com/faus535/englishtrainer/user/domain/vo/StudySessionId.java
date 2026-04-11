package com.faus535.englishtrainer.user.domain.vo;

import java.util.UUID;

public record StudySessionId(UUID value) {

    public StudySessionId {
        if (value == null) {
            throw new IllegalArgumentException("StudySessionId cannot be null");
        }
    }

    public static StudySessionId generate() {
        return new StudySessionId(UUID.randomUUID());
    }
}
