package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.event.ArticleReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProcessArticleContentAsyncService {

    private static final Logger log = LoggerFactory.getLogger(ProcessArticleContentAsyncService.class);

    private final ArticleReadingRepository readingRepository;
    private final ArticleAiPort aiPort;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public ProcessArticleContentAsyncService(ArticleReadingRepository readingRepository,
                                       ArticleAiPort aiPort,
                                       TransactionTemplate transactionTemplate,
                                       ApplicationEventPublisher eventPublisher) {
        this.readingRepository = readingRepository;
        this.aiPort = aiPort;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Async("articleAsyncExecutor")
    public void process(UUID articleId, String topic, String level) {
        try {
            ArticleReading article = readingRepository.findById(new ArticleReadingId(articleId))
                    .orElse(null);

            if (article == null) {
                log.warn("Article not found for async processing: {}", articleId);
                return;
            }

            transactionTemplate.executeWithoutResult(status ->
                    readingRepository.save(article.markProcessing()));

            ArticleAiPort.ArticleGenerateResult result = aiPort.generateArticle(topic, level);

            AtomicInteger orderIndex = new AtomicInteger(0);
            List<ArticleParagraph> paragraphs = result.paragraphs().stream()
                    .map(p -> ArticleParagraph.create(
                            article.id(),
                            p.content(),
                            orderIndex.getAndIncrement(),
                            ArticleSpeaker.fromString(p.speaker())))
                    .toList();

            UUID userId = transactionTemplate.execute(status -> {
                ArticleReading current = readingRepository.findById(new ArticleReadingId(articleId))
                        .orElseThrow();
                readingRepository.save(current.markReady(result.title(), paragraphs));
                return current.userId();
            });

            eventPublisher.publishEvent(new ArticleReadyEvent(articleId, userId));

            log.info("Article content processed successfully: {}", articleId);

        } catch (Throwable t) {
            log.error("Failed to process article content: {}", articleId, t);
            try {
                ArticleReading article = readingRepository.findById(new ArticleReadingId(articleId))
                        .orElse(null);
                if (article != null) {
                    transactionTemplate.executeWithoutResult(status ->
                            readingRepository.save(article.markFailed()));
                }
            } catch (Exception e) {
                log.error("Failed to mark article as FAILED: {}", articleId, e);
            }
        }
    }
}
