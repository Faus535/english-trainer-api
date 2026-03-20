package com.faus535.englishtrainer.gamification.domain;

public record XpLevel(int level, String name, int currentXp, int xpForCurrentLevel, int xpForNextLevel, double progress) {

    private static final int[] THRESHOLDS = {0, 200, 600, 1200, 2200, 3500, 5000, 7000};
    private static final String[] NAMES = {"Beginner", "Elementary", "Pre-Intermediate", "Intermediate",
            "Upper-Intermediate", "Advanced", "Proficient", "Master"};

    public static XpLevel fromXp(int xp) {
        int level = 0;
        for (int i = THRESHOLDS.length - 1; i >= 0; i--) {
            if (xp >= THRESHOLDS[i]) {
                level = i;
                break;
            }
        }
        String name = NAMES[level];
        int currentLevelXp = THRESHOLDS[level];
        int nextLevelXp = level < THRESHOLDS.length - 1 ? THRESHOLDS[level + 1] : THRESHOLDS[level];
        double progress = level < THRESHOLDS.length - 1
                ? (double) (xp - currentLevelXp) / (nextLevelXp - currentLevelXp)
                : 1.0;
        return new XpLevel(level, name, xp, currentLevelXp, nextLevelXp, Math.min(progress, 1.0));
    }
}
