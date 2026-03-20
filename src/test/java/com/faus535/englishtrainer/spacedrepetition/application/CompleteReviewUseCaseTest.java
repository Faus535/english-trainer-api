package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemMother;
import com.faus535.englishtrainer.spacedrepetition.domain.error.SpacedRepetitionItemNotFoundException;
import com.faus535.englishtrainer.spacedrepetition.infrastructure.InMemorySpacedRepetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;

final class CompleteReviewUseCaseTest {

    private InMemorySpacedRepetitionRepository repository;
    private CompleteReviewUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemorySpacedRepetitionRepository();
        ApplicationEventPublisher publisher = event -> {};
        useCase = new CompleteReviewUseCase(repository, publisher);
    }

    @Test
    void shouldCompleteReviewAndAdvanceInterval() throws SpacedRepetitionItemNotFoundException {
        SpacedRepetitionItem item = SpacedRepetitionItemMother.create();
        repository.save(item);

        SpacedRepetitionItem updated = useCase.execute(item.id());

        assertEquals(1, updated.reviewCount());
        assertEquals(1, updated.intervalIndex());
        assertFalse(updated.graduated());
    }

    @Test
    void shouldThrowWhenItemNotFound() {
        SpacedRepetitionItemId unknownId = SpacedRepetitionItemId.generate();

        assertThrows(SpacedRepetitionItemNotFoundException.class,
                () -> useCase.execute(unknownId));
    }
}
