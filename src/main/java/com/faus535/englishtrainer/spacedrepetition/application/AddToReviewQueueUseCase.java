package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.Optional;

@UseCase
public final class AddToReviewQueueUseCase {

    private final SpacedRepetitionRepository repository;

    public AddToReviewQueueUseCase(SpacedRepetitionRepository repository) {
        this.repository = repository;
    }

    public SpacedRepetitionItem execute(UserProfileId userId, String moduleName, String level, int unitIndex) {
        String unitReference = moduleName + "-" + level + "-" + unitIndex;
        Optional<SpacedRepetitionItem> existing = repository.findByUserAndUnitReference(userId, unitReference);
        if (existing.isPresent()) {
            return existing.get();
        }
        SpacedRepetitionItem item = SpacedRepetitionItem.create(userId, moduleName, level, unitIndex);
        return repository.save(item);
    }
}
