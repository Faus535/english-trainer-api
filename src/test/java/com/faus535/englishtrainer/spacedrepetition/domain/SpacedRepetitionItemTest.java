package com.faus535.englishtrainer.spacedrepetition.domain;

import com.faus535.englishtrainer.spacedrepetition.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

final class SpacedRepetitionItemTest {

    @Test
    void shouldCreateItem() {
        UserProfileId userId = UserProfileId.generate();

        SpacedRepetitionItem item = SpacedRepetitionItemMother.create(userId);

        assertNotNull(item.id());
        assertEquals(userId, item.userId());
        assertEquals("vocabulary", item.moduleName());
        assertEquals("A1", item.level());
        assertEquals(0, item.unitIndex());
        assertEquals("vocabulary-A1-0", item.unitReference());
        assertEquals(0, item.reviewCount());
        assertEquals(0, item.intervalIndex());
        assertFalse(item.graduated());
        assertNotNull(item.createdAt());
        assertEquals(LocalDate.now().plusDays(1), item.nextReviewDate());
    }

    @Test
    void shouldCompleteReview() {
        SpacedRepetitionItem item = SpacedRepetitionItemMother.create();

        SpacedRepetitionItem reviewed = item.completeReview();

        assertEquals(1, reviewed.reviewCount());
        assertEquals(1, reviewed.intervalIndex());
        assertFalse(reviewed.graduated());
    }

    @Test
    void shouldAdvanceInterval() {
        SpacedRepetitionItem item = SpacedRepetitionItemMother.create();

        // First review: intervalIndex -> 1, nextReviewDate -> today + 3 days
        SpacedRepetitionItem reviewed1 = item.completeReview();
        assertEquals(1, reviewed1.intervalIndex());
        assertEquals(LocalDate.now().plusDays(3), reviewed1.nextReviewDate());

        // Second review: intervalIndex -> 2, nextReviewDate -> today + 7 days
        SpacedRepetitionItem reviewed2 = reviewed1.completeReview();
        assertEquals(2, reviewed2.intervalIndex());
        assertEquals(LocalDate.now().plusDays(7), reviewed2.nextReviewDate());
    }

    @Test
    void shouldGraduateAfterFiveReviews() {
        SpacedRepetitionItem item = SpacedRepetitionItemMother.create();

        SpacedRepetitionItem current = item;
        for (int i = 0; i < 5; i++) {
            current = current.completeReview();
        }

        assertTrue(current.graduated());
        assertEquals(5, current.reviewCount());
    }

    @Test
    void shouldBeDueToday() {
        UserProfileId userId = UserProfileId.generate();
        SpacedRepetitionItem item = SpacedRepetitionItem.reconstitute(
                SpacedRepetitionItemId.generate(),
                userId,
                "vocabulary-A1-0",
                "vocabulary",
                "A1",
                0,
                LocalDate.now(),
                0,
                0,
                false,
                java.time.Instant.now()
        );

        assertTrue(item.isDueToday());
    }

    @Test
    void shouldNotBeDueWhenGraduated() {
        UserProfileId userId = UserProfileId.generate();
        SpacedRepetitionItem item = SpacedRepetitionItem.reconstitute(
                SpacedRepetitionItemId.generate(),
                userId,
                "vocabulary-A1-0",
                "vocabulary",
                "A1",
                0,
                LocalDate.now(),
                4,
                5,
                true,
                java.time.Instant.now()
        );

        assertFalse(item.isDueToday());
    }

    @Test
    void shouldRegisterReviewCompletedEvent() {
        SpacedRepetitionItem item = SpacedRepetitionItemMother.create();

        SpacedRepetitionItem reviewed = item.completeReview();

        var events = reviewed.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ReviewCompletedEvent.class, events.get(0));

        ReviewCompletedEvent event = (ReviewCompletedEvent) events.get(0);
        assertEquals(item.id(), event.itemId());
        assertEquals(item.userId(), event.userId());
    }
}
