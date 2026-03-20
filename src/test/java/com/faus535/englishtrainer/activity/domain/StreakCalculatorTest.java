package com.faus535.englishtrainer.activity.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class StreakCalculatorTest {

    @Test
    void shouldReturnZeroWhenNoActivities() {
        StreakInfo result = StreakCalculator.calculate(Collections.emptyList());

        assertEquals(0, result.currentStreak());
        assertEquals(0, result.bestStreak());
        assertNull(result.lastActiveDate());
    }

    @Test
    void shouldCalculateStreakForConsecutiveDays() {
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = List.of(
                today,
                today.minusDays(1),
                today.minusDays(2),
                today.minusDays(3)
        );

        StreakInfo result = StreakCalculator.calculate(dates);

        assertEquals(4, result.currentStreak());
        assertEquals(4, result.bestStreak());
        assertEquals(today, result.lastActiveDate());
    }

    @Test
    void shouldResetStreakOnGap() {
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = List.of(
                today,
                today.minusDays(3),
                today.minusDays(4),
                today.minusDays(5)
        );

        StreakInfo result = StreakCalculator.calculate(dates);

        assertEquals(1, result.currentStreak());
        assertEquals(3, result.bestStreak());
        assertEquals(today, result.lastActiveDate());
    }
}
