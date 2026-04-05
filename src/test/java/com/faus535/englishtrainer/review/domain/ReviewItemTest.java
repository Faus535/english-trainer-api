package com.faus535.englishtrainer.review.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewItemTest {

    @Test
    void createSetsDefaults() {
        ReviewItem item = ReviewItem.create(UUID.randomUUID(), ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "front", "back");

        assertEquals(1, item.intervalDays());
        assertEquals(2.5, item.easeFactor());
        assertEquals(0, item.consecutiveCorrect());
        assertNotNull(item.id());
        assertNotNull(item.nextReviewAt());
        assertNotNull(item.createdAt());
    }

    @Test
    void reviewWithQuality3IncreasesConsecutiveCorrect() {
        ReviewItem item = ReviewItemMother.dueToday();

        ReviewItem reviewed = item.review(3);

        assertEquals(1, reviewed.consecutiveCorrect());
        assertEquals(1, reviewed.intervalDays());
    }

    @Test
    void reviewWithQuality5IncreasesEaseFactor() {
        ReviewItem item = ReviewItemMother.dueToday();

        ReviewItem reviewed = item.review(5);

        assertTrue(reviewed.easeFactor() > 2.5);
    }

    @Test
    void reviewWithQuality0ResetsToOne() {
        ReviewItem item = ReviewItemMother.withConsecutiveCorrect(3);

        ReviewItem reviewed = item.review(0);

        assertEquals(0, reviewed.consecutiveCorrect());
        assertEquals(1, reviewed.intervalDays());
    }

    @Test
    void reviewWithQuality2ResetsConsecutiveCorrect() {
        ReviewItem item = ReviewItemMother.withConsecutiveCorrect(5);

        ReviewItem reviewed = item.review(2);

        assertEquals(0, reviewed.consecutiveCorrect());
        assertEquals(1, reviewed.intervalDays());
    }

    @Test
    void secondCorrectReviewSetsIntervalTo6() {
        ReviewItem item = ReviewItemMother.withConsecutiveCorrect(1);

        ReviewItem reviewed = item.review(4);

        assertEquals(6, reviewed.intervalDays());
        assertEquals(2, reviewed.consecutiveCorrect());
    }

    @Test
    void thirdCorrectReviewMultipliesByEaseFactor() {
        ReviewItem item = ReviewItemMother.withEaseFactor(2.5);

        ReviewItem reviewed = item.review(4);

        assertEquals(15, reviewed.intervalDays()); // 6 * 2.5 = 15
        assertEquals(3, reviewed.consecutiveCorrect());
    }

    @Test
    void easeFactorNeverDropsBelow1Point3() {
        ReviewItem item = ReviewItemMother.withEaseFactor(1.3);

        ReviewItem reviewed = item.review(0);

        assertTrue(reviewed.easeFactor() >= 1.3);
    }

    @Test
    void easeFactorFormula() {
        ReviewItem item = ReviewItemMother.withEaseFactor(2.5);

        // Quality 4: EF' = 2.5 + (0.1 - (5-4)*(0.08 + (5-4)*0.02)) = 2.5 + (0.1 - 0.1) = 2.5
        ReviewItem reviewed = item.review(4);
        assertEquals(2.5, reviewed.easeFactor(), 0.001);

        // Quality 5: EF' = 2.5 + (0.1 - 0) = 2.6
        ReviewItem reviewed5 = item.review(5);
        assertEquals(2.6, reviewed5.easeFactor(), 0.001);

        // Quality 3: EF' = 2.5 + (0.1 - 2*(0.08 + 2*0.02)) = 2.5 + (0.1 - 0.24) = 2.36
        ReviewItem reviewed3 = item.review(3);
        assertEquals(2.36, reviewed3.easeFactor(), 0.001);
    }

    @Test
    void reviewRegistersEvent() {
        ReviewItem item = ReviewItemMother.dueToday();

        ReviewItem reviewed = item.review(4);

        assertFalse(reviewed.pullDomainEvents().isEmpty());
    }

    @Test
    void isDueReturnsTrueWhenPastDue() {
        ReviewItem item = ReviewItemMother.dueToday();

        assertTrue(item.isDue(java.time.Instant.now()));
    }

    @Test
    void isDueReturnsFalseWhenNotDue() {
        ReviewItem item = ReviewItemMother.notDue();

        assertFalse(item.isDue(java.time.Instant.now()));
    }
}
