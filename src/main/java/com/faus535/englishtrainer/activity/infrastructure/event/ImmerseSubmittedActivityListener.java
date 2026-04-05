package com.faus535.englishtrainer.activity.infrastructure.event;

import com.faus535.englishtrainer.activity.application.RecordActivityUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
class ImmerseSubmittedActivityListener {

    private static final Logger log = LoggerFactory.getLogger(ImmerseSubmittedActivityListener.class);

    private final AuthUserRepository authUserRepository;
    private final RecordActivityUseCase recordActivityUseCase;

    ImmerseSubmittedActivityListener(AuthUserRepository authUserRepository,
                                      RecordActivityUseCase recordActivityUseCase) {
        this.authUserRepository = authUserRepository;
        this.recordActivityUseCase = recordActivityUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ImmerseExerciseAnsweredEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        recordActivityUseCase.execute(authUser.userProfileId(), LocalDate.now());
                        log.debug("Recorded activity for immerse exercise {}", event.exerciseId().value());
                    });
        } catch (Exception e) {
            log.error("Error recording activity for immerse: {}", e.getMessage(), e);
        }
    }
}
