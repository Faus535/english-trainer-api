package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class TalkCompletedGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(TalkCompletedGamificationListener.class);
    private static final int XP_PER_TALK_CONVERSATION = 50;
    private static final int BONUS_XP_PER_TURN = 5;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    TalkCompletedGamificationListener(AuthUserRepository authUserRepository, AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(TalkConversationCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        int xp = XP_PER_TALK_CONVERSATION + (event.turnCount() * BONUS_XP_PER_TURN);
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), xp);
                            log.info("Granted {} XP for talk conversation {}", xp, event.conversationId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for talk conversation: {}", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling TalkConversationCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
