package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UseCase
public class CreateReviewItemFromTalkUseCase {

    private final ReviewItemRepository repository;

    public CreateReviewItemFromTalkUseCase(ReviewItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(TalkConversationCompletedEvent event) {
        UUID userId = event.userId();

        for (TalkCorrection correction : event.corrections()) {
            String contextSentence = correction.originalUserMessage();
            for (String grammarFix : correction.grammarFixes()) {
                UUID sourceId = UUID.nameUUIDFromBytes(grammarFix.getBytes(StandardCharsets.UTF_8));
                createIfNotExists(userId, sourceId, grammarFix,
                        "Grammar: " + correction.encouragement(), contextSentence);
            }
            for (String vocabSuggestion : correction.vocabularySuggestions()) {
                UUID sourceId = UUID.nameUUIDFromBytes(vocabSuggestion.getBytes(StandardCharsets.UTF_8));
                createIfNotExists(userId, sourceId, vocabSuggestion,
                        "Vocabulary suggestion", contextSentence);
            }
        }
    }

    private void createIfNotExists(UUID userId, UUID sourceId, String frontContent, String backContent,
                                    String contextSentence) {
        repository.findByUserIdSourceTypeAndSourceId(userId, ReviewSourceType.TALK_ERROR, sourceId)
                .ifPresentOrElse(
                        existing -> {},
                        () -> repository.save(ReviewItem.create(
                                userId, ReviewSourceType.TALK_ERROR, sourceId, frontContent, backContent,
                                contextSentence, null, frontContent, null))
                );
    }
}
