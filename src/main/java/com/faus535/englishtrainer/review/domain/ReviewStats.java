package com.faus535.englishtrainer.review.domain;

public record ReviewStats(int totalItems, int dueToday, int completedToday, int streak,
                           long totalMastered, long weeklyReviewed, double accuracyRate) {}
