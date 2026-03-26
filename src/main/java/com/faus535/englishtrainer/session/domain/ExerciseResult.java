package com.faus535.englishtrainer.session.domain;

import java.time.Instant;

public record ExerciseResult(int correctCount, int totalCount, long averageResponseTimeMs, Instant completedAt) {

    public ExerciseResult {
        if (correctCount < 0) {
            throw new IllegalArgumentException("correctCount must be >= 0");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount must be >= 0");
        }
        if (averageResponseTimeMs < 0) {
            throw new IllegalArgumentException("averageResponseTimeMs must be >= 0");
        }
    }
}
