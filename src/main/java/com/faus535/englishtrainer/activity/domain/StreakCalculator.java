package com.faus535.englishtrainer.activity.domain;

import java.time.LocalDate;
import java.util.List;

public final class StreakCalculator {

    private StreakCalculator() {}

    public static StreakInfo calculate(List<LocalDate> sortedDatesDescending) {
        if (sortedDatesDescending.isEmpty()) {
            return new StreakInfo(0, 0, null);
        }

        LocalDate today = LocalDate.now();
        LocalDate lastActive = sortedDatesDescending.get(0);

        // Calculate current streak
        int currentStreak = 0;
        LocalDate expected = today;
        // If today has no activity, start from yesterday
        if (!lastActive.equals(today)) {
            expected = today.minusDays(1);
            if (!lastActive.equals(expected)) {
                // Last activity was more than 1 day ago, current streak is 0
                currentStreak = 0;
            }
        }
        if (currentStreak == 0 && (lastActive.equals(today) || lastActive.equals(today.minusDays(1)))) {
            for (LocalDate date : sortedDatesDescending) {
                if (date.equals(expected)) {
                    currentStreak++;
                    expected = expected.minusDays(1);
                } else if (date.isBefore(expected)) {
                    break;
                }
            }
        }

        // Calculate best streak
        int bestStreak = 0;
        int tempStreak = 1;
        // Sort ascending for best streak calculation
        List<LocalDate> ascending = sortedDatesDescending.stream()
                .sorted()
                .toList();
        for (int i = 1; i < ascending.size(); i++) {
            if (ascending.get(i).equals(ascending.get(i - 1).plusDays(1))) {
                tempStreak++;
            } else {
                bestStreak = Math.max(bestStreak, tempStreak);
                tempStreak = 1;
            }
        }
        bestStreak = Math.max(bestStreak, tempStreak);
        bestStreak = Math.max(bestStreak, currentStreak);

        return new StreakInfo(currentStreak, bestStreak, lastActive);
    }
}
