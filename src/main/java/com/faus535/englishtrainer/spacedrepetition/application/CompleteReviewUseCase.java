package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.spacedrepetition.domain.error.SpacedRepetitionItemNotFoundException;

@UseCase
public final class CompleteReviewUseCase {

    private final SpacedRepetitionRepository repository;

    public CompleteReviewUseCase(SpacedRepetitionRepository repository) {
        this.repository = repository;
    }

    public SpacedRepetitionItem execute(SpacedRepetitionItemId itemId) throws SpacedRepetitionItemNotFoundException {
        SpacedRepetitionItem item = repository.findById(itemId)
                .orElseThrow(() -> new SpacedRepetitionItemNotFoundException(itemId));
        SpacedRepetitionItem updated = item.completeReview();
        return repository.save(updated);
    }
}
