package com.faus535.englishtrainer.home.application;

import java.util.List;

public record HomeData(
        long dueReviewCount,
        int streakDays,
        boolean[] weeklyActivity,
        SuggestedModule suggestedModule,
        long recentXpThisWeek,
        List<RecentAchievement> recentAchievements,
        String englishLevel
) {

    public enum SuggestedModule { REVIEW, ARTICLE, IMMERSE, TALK }

    public record RecentAchievement(String title, String icon, int xpReward) {}
}
