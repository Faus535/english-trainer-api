package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemId;
import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.review.domain.ReviewResult;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetReviewStatsUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryReviewItemRepository itemRepository;
    private InMemoryReviewResultRepository resultRepository;
    private GetReviewStatsUseCase useCase;

    @BeforeEach
    void setUp() {
        itemRepository = new InMemoryReviewItemRepository();
        resultRepository = new InMemoryReviewResultRepository();
        useCase = new GetReviewStatsUseCase(itemRepository, resultRepository);
    }

    @Test
    void shouldReturnStatsForUser() {
        var dueItem = ReviewItemMother.dueToday();
        var notDueItem = ReviewItemMother.notDue();
        itemRepository.save(dueItem);
        itemRepository.save(notDueItem);

        var result = ReviewResult.create(dueItem.id(), USER_ID, 4);
        resultRepository.save(result);

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(2, stats.totalItems());
        assertEquals(1, stats.dueToday());
        assertEquals(1, stats.completedToday());
        assertEquals(1, stats.streak());
    }

    @Test
    void shouldReturnZerosForUserWithNoItems() {
        UUID unknownUser = UUID.randomUUID();

        ReviewStats stats = useCase.execute(unknownUser);

        assertEquals(0, stats.totalItems());
        assertEquals(0, stats.dueToday());
        assertEquals(0, stats.completedToday());
        assertEquals(0, stats.streak());
        assertEquals(0L, stats.totalMastered());
        assertEquals(0L, stats.weeklyReviewed());
        assertEquals(0.0, stats.retentionRate());
        assertEquals(0.0, stats.averageInterval());
    }

    @Test
    void execute_returnsMasteredCount() {
        ReviewItem masteredItem = ReviewItem.reconstitute(
                ReviewItemId.generate(), USER_ID, ReviewSourceType.TALK_ERROR,
                UUID.randomUUID(), "front", "back",
                LocalDate.now().plusDays(30), 21, 2.5, 5,
                Instant.now().minusSeconds(86400 * 30),
                null, null, null, null);
        itemRepository.save(masteredItem);
        itemRepository.save(ReviewItemMother.withUserId(USER_ID));

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(1L, stats.totalMastered());
    }

    @Test
    void execute_returnsWeeklyReviewedCount() {
        itemRepository.save(ReviewItemMother.withUserId(USER_ID));
        ReviewItemId itemId = itemRepository.findAll(USER_ID).get(0).id();
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 4));
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 3));

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(2L, stats.weeklyReviewed());
    }

    @Test
    void execute_returnsRetentionRate() {
        itemRepository.save(ReviewItemMother.withUserId(USER_ID));
        ReviewItemId itemId = itemRepository.findAll(USER_ID).get(0).id();
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 4)); // correct (>= 3)
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 2)); // incorrect

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(0.5, stats.retentionRate(), 0.001);
    }

    @Test
    void shouldReturnAverageIntervalWhenItemsExist() {
        itemRepository.save(ReviewItemMother.withIntervalDays(USER_ID, 1));
        itemRepository.save(ReviewItemMother.withIntervalDays(USER_ID, 6));

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(3.5, stats.averageInterval(), 0.001);
    }

    @Test
    void shouldReturnZeroAverageIntervalWhenNoItems() {
        ReviewStats stats = useCase.execute(UUID.randomUUID());

        assertEquals(0.0, stats.averageInterval(), 0.001);
    }

    @Test
    void shouldReturnRetentionRateAsRatioOfCorrectResults() {
        itemRepository.save(ReviewItemMother.withUserId(USER_ID));
        ReviewItemId itemId = itemRepository.findAll(USER_ID).get(0).id();
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 4)); // correct
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 3)); // correct
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 5)); // correct
        resultRepository.save(ReviewResult.create(itemId, USER_ID, 2)); // incorrect

        ReviewStats stats = useCase.execute(USER_ID);

        assertEquals(0.75, stats.retentionRate(), 0.001);
    }
}
