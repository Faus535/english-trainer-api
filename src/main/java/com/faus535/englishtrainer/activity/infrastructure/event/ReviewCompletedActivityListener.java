package com.faus535.englishtrainer.activity.infrastructure.event;

import com.faus535.englishtrainer.activity.application.RecordActivityUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.auth.domain.AuthUserRepository;
import com.faus535.englishtrainer.review.domain.event.ReviewCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
class ReviewCompletedActivityListener {

    private static final Logger log = LoggerFactory.getLogger(ReviewCompletedActivityListener.class);

    private final AuthUserRepository authUserRepository;
    private final RecordActivityUseCase recordActivityUseCase;

    ReviewCompletedActivityListener(AuthUserRepository authUserRepository,
                                     RecordActivityUseCase recordActivityUseCase) {
        this.authUserRepository = authUserRepository;
        this.recordActivityUseCase = recordActivityUseCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ReviewCompletedEvent event) {
        try {
            authUserRepository.findById(new AuthUserId(event.userId()))
                    .ifPresent(authUser -> {
                        recordActivityUseCase.execute(authUser.userProfileId(), LocalDate.now());
                        log.debug("Recorded activity for review item {}", event.itemId().value());
                    });
        } catch (Exception e) {
            log.error("Error recording activity for review: {}", e.getMessage(), e);
        }
    }
}
