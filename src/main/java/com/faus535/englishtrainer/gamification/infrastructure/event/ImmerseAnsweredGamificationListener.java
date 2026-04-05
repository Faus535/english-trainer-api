package com.faus535.englishtrainer.gamification.infrastructure.event;

import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.user.application.AddXpUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ImmerseAnsweredGamificationListener {

    private static final Logger log = LoggerFactory.getLogger(ImmerseAnsweredGamificationListener.class);
    private static final int XP_PER_CORRECT_ANSWER = 10;

    private final AuthUserRepository authUserRepository;
    private final AddXpUseCase addXpUseCase;

    ImmerseAnsweredGamificationListener(AuthUserRepository authUserRepository, AddXpUseCase addXpUseCase) {
        this.authUserRepository = authUserRepository;
        this.addXpUseCase = addXpUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ImmerseExerciseAnsweredEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        try {
                            addXpUseCase.execute(authUser.userProfileId(), XP_PER_CORRECT_ANSWER);
                            log.info("Granted {} XP for immerse exercise {}", XP_PER_CORRECT_ANSWER,
                                    event.exerciseId().value());
                        } catch (Exception e) {
                            log.error("Failed to grant XP for immerse exercise: {}", e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Error handling ImmerseExerciseAnsweredEvent: {}", e.getMessage(), e);
        }
    }
}
