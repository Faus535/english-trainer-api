package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.review.domain.ReviewItemId;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetReviewQueueUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryReviewItemRepository repository;
    private GetReviewQueueUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryReviewItemRepository();
        useCase = new GetReviewQueueUseCase(repository);
    }

    @Test
    void returnsDueItemsSorted() {
        ReviewItem due1 = ReviewItemMother.dueToday();
        ReviewItem due2 = ReviewItemMother.dueToday();
        repository.save(due1);
        repository.save(due2);

        List<ReviewItem> result = useCase.execute(USER_ID, 10);

        assertEquals(2, result.size());
    }

    @Test
    void excludesNotDueItems() {
        ReviewItem due = ReviewItemMother.dueToday();
        ReviewItem notDue = ReviewItemMother.notDue();
        repository.save(due);
        repository.save(notDue);

        List<ReviewItem> result = useCase.execute(USER_ID, 10);

        assertEquals(1, result.size());
        assertEquals(due.id(), result.getFirst().id());
    }

    @Test
    void respectsLimit() {
        for (int i = 0; i < 5; i++) {
            repository.save(ReviewItemMother.dueToday());
        }

        List<ReviewItem> result = useCase.execute(USER_ID, 3);

        assertEquals(3, result.size());
    }

    @Test
    void emptyQueueReturnsEmptyList() {
        List<ReviewItem> result = useCase.execute(USER_ID, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void capsLimitAt50() {
        for (int i = 0; i < 55; i++) {
            repository.save(ReviewItemMother.dueToday());
        }

        List<ReviewItem> result = useCase.execute(USER_ID, 100);

        assertEquals(50, result.size());
    }

    @Test
    void shouldSortByMostOverdueFirst() {
        ReviewItem overdueBy5 = ReviewItem.reconstitute(
                ReviewItemId.generate(), USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "front", "back",
                LocalDate.now().minusDays(5), 1, 2.5, 0,
                Instant.now().minusSeconds(86400 * 5),
                null, null, null, null);
        ReviewItem overdueBy1 = ReviewItem.reconstitute(
                ReviewItemId.generate(), USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "front", "back",
                LocalDate.now().minusDays(1), 1, 2.5, 0,
                Instant.now().minusSeconds(86400),
                null, null, null, null);
        repository.save(overdueBy1);
        repository.save(overdueBy5);

        List<ReviewItem> result = useCase.execute(USER_ID, 10);

        assertEquals(2, result.size());
        assertEquals(overdueBy5.id(), result.get(0).id());
        assertEquals(overdueBy1.id(), result.get(1).id());
    }
}
