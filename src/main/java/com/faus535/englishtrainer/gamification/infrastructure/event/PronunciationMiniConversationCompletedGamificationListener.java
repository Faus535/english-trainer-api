package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationMiniConversationCompletedEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class PronunciationMiniConversationCompletedGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(
            PronunciationMiniConversationCompletedGamificationListener.class);
    private static final int XP_PER_MINI_CONVERSATION = 30;
    private static final int BONUS_XP_THRESHOLD = 70;
    private static final int BONUS_XP = 10;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    PronunciationMiniConversationCompletedGamificationListener(AuthUserRepository authUserRepository,
            AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(PronunciationMiniConversationCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        int xp = XP_PER_MINI_CONVERSATION +
                                (event.finalScore() >= BONUS_XP_THRESHOLD ? BONUS_XP : 0);
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), xp);
                            log.info("Granted {} XP for pronunciation mini-conversation {}",
                                    xp, event.conversationId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for pronunciation mini-conversation: {}",
                                    e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling PronunciationMiniConversationCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
