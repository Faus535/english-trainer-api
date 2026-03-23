package com.faus535.englishtrainer.dailychallenge.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyChallengeRepository {

    Optional<DailyChallenge> findByDate(LocalDate date);

    DailyChallenge save(DailyChallenge dailyChallenge);
}
