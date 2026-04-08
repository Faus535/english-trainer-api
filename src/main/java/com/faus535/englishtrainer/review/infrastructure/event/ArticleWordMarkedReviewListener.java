package com.faus535.englishtrainer.review.infrastructure.event;

import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import com.faus535.englishtrainer.review.application.CreateReviewItemFromArticleUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ArticleWordMarkedReviewListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleWordMarkedReviewListener.class);

    private final CreateReviewItemFromArticleUseCase useCase;

    ArticleWordMarkedReviewListener(CreateReviewItemFromArticleUseCase useCase) {
        this.useCase = useCase;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handle(ArticleWordMarkedEvent event) {
        log.debug("Creating review item from article marked word {}", event.markedWordId());
        useCase.execute(event);
    }
}
