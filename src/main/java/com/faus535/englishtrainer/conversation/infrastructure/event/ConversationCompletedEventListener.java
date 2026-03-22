package com.faus535.englishtrainer.conversation.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.conversation.domain.event.ConversationCompletedEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class ConversationCompletedEventListener {

    private static final Logger log = LoggerFactory.getLogger(ConversationCompletedEventListener.class);
    private static final int XP_PER_CONVERSATION = 50;
    private static final int BONUS_XP_PER_TURN = 5;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    ConversationCompletedEventListener(AuthUserRepository authUserRepository, AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @EventListener
    void handleConversationCompleted(ConversationCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        int xp = XP_PER_CONVERSATION + (event.turnCount() * BONUS_XP_PER_TURN);
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), xp);
                            log.info("Granted {} XP to user {} for conversation {}",
                                    xp, event.userId(), event.conversationId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for conversation {}: {}",
                                    event.conversationId().value(), e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling ConversationCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
