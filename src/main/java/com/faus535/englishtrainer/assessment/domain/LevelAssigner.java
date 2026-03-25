package com.faus535.englishtrainer.assessment.domain;

public final class LevelAssigner {

    private LevelAssigner() {
    }

    public static String assignLevel(int scorePercentage) {
        if (scorePercentage >= 95) return "c2";
        if (scorePercentage >= 80) return "c1";
        if (scorePercentage >= 65) return "b2";
        if (scorePercentage >= 50) return "b1";
        if (scorePercentage >= 35) return "a2";
        return "a1";
    }
}
