package com.faus535.englishtrainer.vocabulary.infrastructure.event;

import com.faus535.englishtrainer.spacedrepetition.application.AddVocabularyToReviewUseCase;
import com.faus535.englishtrainer.vocabulary.domain.event.WordLearnedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class WordLearnedListener {

    private static final Logger log = LoggerFactory.getLogger(WordLearnedListener.class);

    private final AddVocabularyToReviewUseCase addVocabularyToReviewUseCase;

    WordLearnedListener(AddVocabularyToReviewUseCase addVocabularyToReviewUseCase) {
        this.addVocabularyToReviewUseCase = addVocabularyToReviewUseCase;
    }

    @EventListener
    void handleWordLearned(WordLearnedEvent event) {
        try {
            addVocabularyToReviewUseCase.execute(event.userId(), event.word(), "A1");
            log.info("Scheduled SRS review for learned word '{}' for user {}", event.word(), event.userId());
        } catch (Exception e) {
            log.error("Failed to schedule SRS review for word '{}': {}", event.word(), e.getMessage(), e);
        }
    }
}
