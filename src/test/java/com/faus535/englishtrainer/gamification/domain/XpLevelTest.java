package com.faus535.englishtrainer.gamification.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XpLevelTest {

    @Test
    void shouldAssignLevel0ForXpBelow200() {
        XpLevel xpLevel = XpLevel.fromXp(0);

        assertEquals(0, xpLevel.level());
        assertEquals("Beginner", xpLevel.name());
        assertEquals(0, xpLevel.currentXp());
        assertEquals(0, xpLevel.xpForCurrentLevel());
        assertEquals(200, xpLevel.xpForNextLevel());
    }

    @Test
    void shouldAssignLevel1ForXp200() {
        XpLevel xpLevel = XpLevel.fromXp(200);

        assertEquals(1, xpLevel.level());
        assertEquals("Elementary", xpLevel.name());
        assertEquals(200, xpLevel.xpForCurrentLevel());
        assertEquals(600, xpLevel.xpForNextLevel());
    }

    @Test
    void shouldAssignLevel2ForXp600() {
        XpLevel xpLevel = XpLevel.fromXp(600);

        assertEquals(2, xpLevel.level());
        assertEquals("Pre-Intermediate", xpLevel.name());
    }

    @Test
    void shouldAssignLevel3ForXp1200() {
        XpLevel xpLevel = XpLevel.fromXp(1200);

        assertEquals(3, xpLevel.level());
        assertEquals("Intermediate", xpLevel.name());
    }

    @Test
    void shouldAssignLevel4ForXp2200() {
        XpLevel xpLevel = XpLevel.fromXp(2200);

        assertEquals(4, xpLevel.level());
        assertEquals("Upper-Intermediate", xpLevel.name());
    }

    @Test
    void shouldAssignLevel5ForXp3500() {
        XpLevel xpLevel = XpLevel.fromXp(3500);

        assertEquals(5, xpLevel.level());
        assertEquals("Advanced", xpLevel.name());
    }

    @Test
    void shouldAssignLevel6ForXp5000() {
        XpLevel xpLevel = XpLevel.fromXp(5000);

        assertEquals(6, xpLevel.level());
        assertEquals("Proficient", xpLevel.name());
    }

    @Test
    void shouldAssignLevel7ForXp7000() {
        XpLevel xpLevel = XpLevel.fromXp(7000);

        assertEquals(7, xpLevel.level());
        assertEquals("Master", xpLevel.name());
        assertEquals(1.0, xpLevel.progress());
    }

    @Test
    void shouldCalculateProgressWithinLevel() {
        XpLevel xpLevel = XpLevel.fromXp(100);

        assertEquals(0, xpLevel.level());
        assertEquals(0.5, xpLevel.progress(), 0.01);
    }

    @Test
    void shouldHandleXpBetweenLevels() {
        XpLevel xpLevel = XpLevel.fromXp(400);

        assertEquals(1, xpLevel.level());
        assertEquals(0.5, xpLevel.progress(), 0.01);
    }
}
