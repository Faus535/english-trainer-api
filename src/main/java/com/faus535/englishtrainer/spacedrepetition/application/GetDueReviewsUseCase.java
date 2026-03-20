package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.List;

@UseCase
public final class GetDueReviewsUseCase {

    private final SpacedRepetitionRepository repository;

    public GetDueReviewsUseCase(SpacedRepetitionRepository repository) {
        this.repository = repository;
    }

    public List<SpacedRepetitionItem> execute(UserProfileId userId) {
        return repository.findDueByUser(userId, LocalDate.now());
    }
}
