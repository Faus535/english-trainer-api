package com.faus535.englishtrainer.review.infrastructure.event;

import com.faus535.englishtrainer.review.application.CreateReviewItemFromTalkUseCase;
import com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class TalkCompletedReviewListener {

    private static final Logger log = LoggerFactory.getLogger(TalkCompletedReviewListener.class);

    private final CreateReviewItemFromTalkUseCase useCase;

    TalkCompletedReviewListener(CreateReviewItemFromTalkUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(TalkConversationCompletedEvent event) {
        log.debug("Creating review items from talk conversation {}", event.conversationId().value());
        useCase.execute(event);
    }
}
