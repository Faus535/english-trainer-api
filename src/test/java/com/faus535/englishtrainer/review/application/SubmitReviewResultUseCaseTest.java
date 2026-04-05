package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemMother;
import com.faus535.englishtrainer.review.domain.error.ReviewItemNotFoundException;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmitReviewResultUseCaseTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private InMemoryReviewItemRepository itemRepository;
    private InMemoryReviewResultRepository resultRepository;
    private SubmitReviewResultUseCase useCase;

    @BeforeEach
    void setUp() {
        itemRepository = new InMemoryReviewItemRepository();
        resultRepository = new InMemoryReviewResultRepository();
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new SubmitReviewResultUseCase(itemRepository, resultRepository, eventPublisher);
    }

    @Test
    void updatesItemSchedule() throws ReviewItemNotFoundException {
        ReviewItem item = ReviewItemMother.dueToday();
        itemRepository.save(item);

        ReviewItem updated = useCase.execute(USER_ID, item.id().value(), 4);

        assertEquals(1, updated.consecutiveCorrect());
        assertNotEquals(item.nextReviewAt(), updated.nextReviewAt());
    }

    @Test
    void savesResultRecord() throws ReviewItemNotFoundException {
        ReviewItem item = ReviewItemMother.dueToday();
        itemRepository.save(item);

        useCase.execute(USER_ID, item.id().value(), 3);

        assertEquals(1, resultRepository.findAll().size());
        assertEquals(3, resultRepository.findAll().getFirst().quality());
    }

    @Test
    void throwsOnItemNotFound() {
        assertThrows(ReviewItemNotFoundException.class,
                () -> useCase.execute(USER_ID, UUID.randomUUID(), 4));
    }
}
