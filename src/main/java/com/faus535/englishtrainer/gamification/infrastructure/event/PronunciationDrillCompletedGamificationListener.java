package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.pronunciation.domain.event.PronunciationDrillCompletedEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class PronunciationDrillCompletedGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(PronunciationDrillCompletedGamificationListener.class);
    private static final int XP_PER_DRILL = 15;
    private static final int BONUS_XP_PER_PERFECT_STREAK = 5;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    PronunciationDrillCompletedGamificationListener(AuthUserRepository authUserRepository,
            AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(PronunciationDrillCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        int xp = XP_PER_DRILL + (event.perfectStreak() * BONUS_XP_PER_PERFECT_STREAK);
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), xp);
                            log.info("Granted {} XP for pronunciation drill {}", xp, event.drillId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for pronunciation drill: {}", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling PronunciationDrillCompletedEvent: {}", e.getMessage(), e);
        }
    }
}
