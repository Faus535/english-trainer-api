package com.faus535.englishtrainer.learningpath.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class MasteryScoreTest {

    @Test
    void shouldCreateValidScore() {
        MasteryScore score = new MasteryScore(75);

        assertEquals(75, score.value());
    }

    @Test
    void shouldRejectNegativeScore() {
        assertThrows(IllegalArgumentException.class, () -> new MasteryScore(-1));
    }

    @Test
    void shouldRejectScoreAbove100() {
        assertThrows(IllegalArgumentException.class, () -> new MasteryScore(101));
    }

    @Test
    void shouldAcceptBoundaryValues() {
        assertDoesNotThrow(() -> new MasteryScore(0));
        assertDoesNotThrow(() -> new MasteryScore(100));
    }

    @Test
    void shouldBeMasteredAt70() {
        assertTrue(new MasteryScore(70).isMastered());
        assertTrue(new MasteryScore(100).isMastered());
        assertFalse(new MasteryScore(69).isMastered());
    }

    @Test
    void shouldNeedReviewBelow50() {
        assertTrue(new MasteryScore(49).needsReview());
        assertTrue(new MasteryScore(0).needsReview());
        assertFalse(new MasteryScore(50).needsReview());
    }
}
