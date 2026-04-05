package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.review.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ReviewCompletedGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedGamificationListener.class);
    private static final int XP_PER_REVIEW = 5;
    private static final int XP_BONUS_GRADUATED = 20;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    ReviewCompletedGamificationListener(AuthUserRepository authUserRepository, AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ReviewCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        int xp = event.graduated() ? XP_PER_REVIEW + XP_BONUS_GRADUATED : XP_PER_REVIEW;
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), xp);
                            log.info("Granted {} XP for review item {}", xp, event.itemId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for review: {}", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling ReviewCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
