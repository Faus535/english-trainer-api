package com.faus535.englishtrainer.review.infrastructure.event;

import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.review.application.CreateReviewItemFromImmerseUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ImmerseAnsweredReviewListener {

    private static final Logger log = LoggerFactory.getLogger(ImmerseAnsweredReviewListener.class);

    private final CreateReviewItemFromImmerseUseCase useCase;

    ImmerseAnsweredReviewListener(CreateReviewItemFromImmerseUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ImmerseExerciseAnsweredEvent event) {
        log.debug("Creating review item from immerse exercise {}", event.exerciseId().value());
        useCase.execute(event);
    }
}
