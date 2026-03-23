package com.faus535.englishtrainer.assessment.domain;

import java.util.UUID;

public record TestQuestionId(UUID value) {

    public TestQuestionId {
        if (value == null) {
            throw new IllegalArgumentException("TestQuestionId cannot be null");
        }
    }

    public static TestQuestionId generate() {
        return new TestQuestionId(UUID.randomUUID());
    }
}
