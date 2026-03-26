package com.faus535.englishtrainer.learningpath.domain;

public record MasteryScore(int value) {

    public MasteryScore {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("MasteryScore must be between 0 and 100, got: " + value);
        }
    }

    public boolean isMastered() {
        return value >= 70;
    }

    public boolean needsReview() {
        return value < 50;
    }
}
