package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class AddToReviewQueueUseCaseTest {

    private InMemorySpacedRepetitionRepository repository;
    private AddToReviewQueueUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemorySpacedRepetitionRepository();
        useCase = new AddToReviewQueueUseCase(repository);
    }

    @Test
    void shouldAddItemToReviewQueue() {
        UserProfileId userId = UserProfileId.generate();

        SpacedRepetitionItem item = useCase.execute(userId, "vocabulary", "A1", 0);

        assertNotNull(item);
        assertEquals(userId, item.userId());
        assertEquals("vocabulary-A1-0", item.unitReference());
        assertEquals(0, item.reviewCount());
        assertFalse(item.graduated());
    }

    @Test
    void shouldReturnExistingWhenAlreadyQueued() {
        UserProfileId userId = UserProfileId.generate();

        SpacedRepetitionItem first = useCase.execute(userId, "vocabulary", "A1", 0);
        SpacedRepetitionItem second = useCase.execute(userId, "vocabulary", "A1", 0);

        assertEquals(first.id(), second.id());
    }
}
