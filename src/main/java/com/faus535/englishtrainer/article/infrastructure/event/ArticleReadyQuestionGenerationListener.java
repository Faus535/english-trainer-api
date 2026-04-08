package com.faus535.englishtrainer.article.infrastructure.event;

import com.faus535.englishtrainer.article.application.GenerateArticleQuestionsAsyncService;
import com.faus535.englishtrainer.article.domain.event.ArticleReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ArticleReadyQuestionGenerationListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleReadyQuestionGenerationListener.class);

    private final GenerateArticleQuestionsAsyncService asyncService;

    ArticleReadyQuestionGenerationListener(GenerateArticleQuestionsAsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @TransactionalEventListener
    void handle(ArticleReadyEvent event) {
        log.debug("Triggering async question generation for article {}", event.articleReadingId());
        asyncService.generateQuestions(event.articleReadingId());
    }
}
