package com.faus535.englishtrainer.analytics.domain;

import java.util.Map;

public record AnalyticsSummary(
        int totalSessions,
        int totalConversations,
        int totalReadingSubmissions,
        int totalWritingSubmissions,
        int currentStreak,
        int bestStreak,
        int totalXp,
        Map<String, String> currentLevels
) {}
