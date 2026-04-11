package com.faus535.englishtrainer.review.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SM2AlgorithmTest {

    @Test
    void shouldReturnInterval1WhenFirstRepetition() {
        SM2Result result = SM2Algorithm.calculate(2.5, 1, 0, 4);

        assertEquals(1, result.newIntervalDays());
        assertEquals(1, result.newRepetitions());
    }

    @Test
    void shouldReturnInterval6WhenSecondRepetition() {
        SM2Result result = SM2Algorithm.calculate(2.5, 1, 1, 4);

        assertEquals(6, result.newIntervalDays());
        assertEquals(2, result.newRepetitions());
    }

    @Test
    void shouldMultiplyIntervalByEaseFactorWhenThirdRepetition() {
        SM2Result result = SM2Algorithm.calculate(2.5, 6, 2, 4);

        assertEquals(15, result.newIntervalDays()); // 6 * 2.5 = 15
        assertEquals(3, result.newRepetitions());
    }

    @Test
    void shouldResetRepetitionsWhenQualityBelow3() {
        SM2Result result = SM2Algorithm.calculate(2.5, 6, 3, 2);

        assertEquals(0, result.newRepetitions());
        assertEquals(1, result.newIntervalDays());
    }

    @Test
    void shouldNotDropEaseFactorBelow1Point3() {
        SM2Result result = SM2Algorithm.calculate(1.3, 1, 0, 0);

        assertTrue(result.newEaseFactor() >= 1.3);
    }

    @Test
    void shouldIncreaseEaseFactorWhenQuality5() {
        SM2Result result = SM2Algorithm.calculate(2.5, 1, 0, 5);

        assertEquals(2.6, result.newEaseFactor(), 0.001);
    }

    @Test
    void shouldDecreaseEaseFactorWhenQuality3() {
        SM2Result result = SM2Algorithm.calculate(2.5, 1, 0, 3);

        assertEquals(2.36, result.newEaseFactor(), 0.001);
    }

    @Test
    void shouldSetNextReviewAtToTodayPlusInterval() {
        SM2Result result = SM2Algorithm.calculate(2.5, 1, 0, 4);

        assertEquals(LocalDate.now().plusDays(result.newIntervalDays()), result.nextReviewAt());
    }

    @Test
    void shouldReturnRepetitions0AndInterval1WhenQuality0() {
        SM2Result result = SM2Algorithm.calculate(2.5, 6, 3, 0);

        assertEquals(0, result.newRepetitions());
        assertEquals(1, result.newIntervalDays());
    }

    @Test
    void shouldReturnRepetitions0AndInterval1WhenQuality1() {
        SM2Result result = SM2Algorithm.calculate(2.5, 6, 3, 1);

        assertEquals(0, result.newRepetitions());
        assertEquals(1, result.newIntervalDays());
    }

    @Test
    void shouldReturnRepetitions0AndInterval1WhenQuality2() {
        SM2Result result = SM2Algorithm.calculate(2.5, 6, 3, 2);

        assertEquals(0, result.newRepetitions());
        assertEquals(1, result.newIntervalDays());
    }
}
