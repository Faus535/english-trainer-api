package com.faus535.englishtrainer.talk.application;

import com.faus535.englishtrainer.talk.domain.QuickChallenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListQuickChallengesUseCaseTest {

    private ListQuickChallengesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListQuickChallengesUseCase();
    }

    @Test
    void execute_returnsThreeChallenges() {
        List<QuickChallenge> result = useCase.execute(LocalDate.now());

        assertEquals(3, result.size());
    }

    @Test
    void execute_returnsSameChallengesForSameDay() {
        LocalDate date = LocalDate.of(2026, 4, 11);

        List<QuickChallenge> first = useCase.execute(date);
        List<QuickChallenge> second = useCase.execute(date);

        assertEquals(first, second);
    }

    @Test
    void execute_returnsDifferentChallengesForDifferentDays() {
        LocalDate day1 = LocalDate.of(2026, 1, 1);   // dayOfYear=1, offset=1%13=1
        LocalDate day2 = LocalDate.of(2026, 1, 3);   // dayOfYear=3, offset=3%13=3

        List<QuickChallenge> result1 = useCase.execute(day1);
        List<QuickChallenge> result2 = useCase.execute(day2);

        assertNotEquals(result1, result2);
    }
}
