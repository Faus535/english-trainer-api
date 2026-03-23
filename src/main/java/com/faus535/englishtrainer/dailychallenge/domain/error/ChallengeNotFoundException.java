package com.faus535.englishtrainer.dailychallenge.domain.error;

import java.time.LocalDate;

public final class ChallengeNotFoundException extends DailyChallengeException {

    public ChallengeNotFoundException(LocalDate date) {
        super("Challenge not found for date: " + date);
    }
}
