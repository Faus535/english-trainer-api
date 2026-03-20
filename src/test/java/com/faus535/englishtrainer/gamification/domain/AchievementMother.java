package com.faus535.englishtrainer.gamification.domain;

public final class AchievementMother {

    private AchievementMother() {
    }

    public static Achievement create() {
        return Achievement.reconstitute(
                new AchievementId("first-lesson"),
                "First Lesson",
                "Complete your first lesson",
                "star",
                50
        );
    }

    public static Achievement withId(String id) {
        return Achievement.reconstitute(
                new AchievementId(id),
                "Achievement " + id,
                "Description for " + id,
                "trophy",
                100
        );
    }
}
