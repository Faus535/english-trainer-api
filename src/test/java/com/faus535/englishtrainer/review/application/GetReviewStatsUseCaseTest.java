package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.review.domain.ReviewResult;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetReviewStatsUseCaseTest {

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
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var dueItem = ReviewItemMother.dueToday();
        var notDueItem = ReviewItemMother.notDue();
        itemRepository.save(dueItem);
        itemRepository.save(notDueItem);

        var result = ReviewResult.create(dueItem.id(), userId, 4);
        resultRepository.save(result);

        ReviewStats stats = useCase.execute(userId);

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
    }
}
