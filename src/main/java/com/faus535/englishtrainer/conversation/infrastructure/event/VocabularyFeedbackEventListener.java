package com.faus535.englishtrainer.conversation.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.conversation.application.VocabularyFeedbackEvent;
import com.faus535.englishtrainer.spacedrepetition.application.AddVocabularyToReviewUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class VocabularyFeedbackEventListener {

    private static final Logger log = LoggerFactory.getLogger(VocabularyFeedbackEventListener.class);

    private final AuthUserRepository authUserRepository;
    private final AddVocabularyToReviewUseCase addVocabularyToReviewUseCase;

    VocabularyFeedbackEventListener(AuthUserRepository authUserRepository,
                                     AddVocabularyToReviewUseCase addVocabularyToReviewUseCase) {
        this.authUserRepository = authUserRepository;
        this.addVocabularyToReviewUseCase = addVocabularyToReviewUseCase;
    }

    @EventListener
    void handleVocabularyFeedback(VocabularyFeedbackEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        for (String word : event.vocabularySuggestions()) {
                            try {
                                addVocabularyToReviewUseCase.execute(
                                        authUser.userProfileId(), word, event.level());
                                log.debug("Added vocabulary '{}' to SR queue for user {}",
                                        word, event.userId());
                            } catch (Exception e) {
                                log.error("Failed to add vocabulary '{}' to SR queue: {}",
                                        word, e.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling VocabularyFeedbackEvent: {}", e.getMessage(), e);
        }
    }
}
