package com.faus535.englishtrainer.assessment.domain;

public final class LevelAssigner {

    private LevelAssigner() {
    }

    public static String assignLevel(int scorePercentage) {
        if (scorePercentage >= 85) return "c1";
        if (scorePercentage >= 70) return "b2";
        if (scorePercentage >= 55) return "b1";
        if (scorePercentage >= 40) return "a2";
        return "a1";
    }
}
