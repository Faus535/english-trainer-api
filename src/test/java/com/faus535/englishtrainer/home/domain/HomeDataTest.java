package com.faus535.englishtrainer.home.domain;

import com.faus535.englishtrainer.home.application.HomeData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HomeDataTest {

    @Test
    void homeData_holdsAllFields() {
        boolean[] weeklyActivity = {true, false, true, false, false, false, false};
        List<HomeData.RecentAchievement> achievements = List.of(
                new HomeData.RecentAchievement("First Article", "📖", 50)
        );

        HomeData data = new HomeData(5L, 3, weeklyActivity, HomeData.SuggestedModule.ARTICLE,
                150L, achievements, "B1");

        assertEquals(5L, data.dueReviewCount());
        assertEquals(3, data.streakDays());
        assertArrayEquals(weeklyActivity, data.weeklyActivity());
        assertEquals(HomeData.SuggestedModule.ARTICLE, data.suggestedModule());
        assertEquals(150L, data.recentXpThisWeek());
        assertEquals(1, data.recentAchievements().size());
        assertEquals("B1", data.englishLevel());
        assertEquals("First Article", data.recentAchievements().getFirst().title());
        assertEquals("📖", data.recentAchievements().getFirst().icon());
        assertEquals(50, data.recentAchievements().getFirst().xpReward());
    }
}
