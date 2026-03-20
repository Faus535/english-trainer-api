package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.domain.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

@UseCase
public final class GetReviewStatsUseCase {

    private final SpacedRepetitionRepository repository;

    public GetReviewStatsUseCase(SpacedRepetitionRepository repository) {
        this.repository = repository;
    }

    public record ReviewStats(int total, int dueToday, int graduated) {}

    public ReviewStats execute(UserProfileId userId) {
        List<SpacedRepetitionItem> allItems = repository.findAllByUser(userId);
        int total = allItems.size();
        int dueToday = (int) allItems.stream().filter(SpacedRepetitionItem::isDueToday).count();
        int graduated = (int) allItems.stream().filter(SpacedRepetitionItem::graduated).count();
        return new ReviewStats(total, dueToday, graduated);
    }
}
