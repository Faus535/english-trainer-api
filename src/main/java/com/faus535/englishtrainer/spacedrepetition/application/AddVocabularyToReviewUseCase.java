package com.faus535.englishtrainer.spacedrepetition.application;

import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionRepository;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class AddVocabularyToReviewUseCase {

    private final SpacedRepetitionRepository repository;

    public AddVocabularyToReviewUseCase(SpacedRepetitionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UserProfileId userId, String word, String level) {
        String unitReference = "vocab-" + word.toLowerCase().trim();
        if (repository.findByUserAndUnitReference(userId, unitReference).isEmpty()) {
            SpacedRepetitionItem item = SpacedRepetitionItem.createForVocabulary(userId, word, level);
            repository.save(item);
        }
    }
}
