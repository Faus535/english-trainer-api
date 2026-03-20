package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.spacedrepetition.domain.error.SpacedRepetitionItemNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CompleteReviewUseCase {

    private final SpacedRepetitionRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CompleteReviewUseCase(SpacedRepetitionRepository repository,
                                 ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SpacedRepetitionItem execute(SpacedRepetitionItemId itemId) throws SpacedRepetitionItemNotFoundException {
        SpacedRepetitionItem item = repository.findById(itemId)
                .orElseThrow(() -> new SpacedRepetitionItemNotFoundException(itemId));
        SpacedRepetitionItem updated = item.completeReview();
        SpacedRepetitionItem saved = repository.save(updated);
        updated.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
