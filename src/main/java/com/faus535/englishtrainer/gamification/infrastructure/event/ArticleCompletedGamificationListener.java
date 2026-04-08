package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ArticleCompletedGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleCompletedGamificationListener.class);
    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    ArticleCompletedGamificationListener(AuthUserRepository authUserRepository, AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ArticleReadingCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), event.xpEarned());
                            log.info("Granted {} XP for completing article {}", event.xpEarned(),
                                    event.articleReadingId());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for article completion: {}", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling ArticleReadingCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
