package com.faus535.englishtrainer.review.domain;

import java.time.LocalDate;

public final class SM2Algorithm {

    private SM2Algorithm() {}

    public static SM2Result calculate(double easeFactor, int intervalDays, int repetitions, int quality) {
        double newEaseFactor = Math.max(1.3,
                easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)));
        int newIntervalDays;
        int newRepetitions;

        if (quality >= 3) {
            newRepetitions = repetitions + 1;
            if (repetitions == 0) {
                newIntervalDays = 1;
            } else if (repetitions == 1) {
                newIntervalDays = 6;
            } else {
                newIntervalDays = (int) Math.round(intervalDays * newEaseFactor);
            }
        } else {
            newRepetitions = 0;
            newIntervalDays = 1;
        }

        LocalDate nextReviewAt = LocalDate.now().plusDays(newIntervalDays);
        return new SM2Result(newEaseFactor, newIntervalDays, newRepetitions, nextReviewAt);
    }
}
