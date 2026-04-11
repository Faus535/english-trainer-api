package com.faus535.englishtrainer.article.infrastructure.event;

import com.faus535.englishtrainer.article.application.EnrichMarkedWordAsyncService;
import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class ArticleWordMarkedEnrichmentListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleWordMarkedEnrichmentListener.class);

    private final EnrichMarkedWordAsyncService enrichmentService;

    ArticleWordMarkedEnrichmentListener(EnrichMarkedWordAsyncService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @TransactionalEventListener
    void handle(ArticleWordMarkedEvent event) {
        log.debug("Triggering async enrichment for marked word '{}' (id={})",
                event.wordOrPhrase(), event.markedWordId());
        enrichmentService.enrich(event.markedWordId(), event.articleReadingId(),
                event.wordOrPhrase(), event.contextSentence());
    }
}
