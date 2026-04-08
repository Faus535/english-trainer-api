package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ArticleReading extends AggregateRoot<ArticleReadingId> {

    private final ArticleReadingId id;
    private final UUID userId;
    private final ArticleTopic topic;
    private final ArticleLevel level;
    private final String title;
    private final ArticleStatus status;
    private final List<ArticleParagraph> paragraphs;
    private final Instant createdAt;

    private ArticleReading(ArticleReadingId id, UUID userId, ArticleTopic topic, ArticleLevel level,
                            String title, ArticleStatus status, List<ArticleParagraph> paragraphs,
                            Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.topic = topic;
        this.level = level;
        this.title = title;
        this.status = status;
        this.paragraphs = paragraphs != null ? List.copyOf(paragraphs) : List.of();
        this.createdAt = createdAt;
    }

    public static ArticleReading create(UUID userId, ArticleTopic topic, ArticleLevel level) {
        return new ArticleReading(ArticleReadingId.generate(), userId, topic, level,
                "", ArticleStatus.PENDING, List.of(), Instant.now());
    }

    public static ArticleReading reconstitute(ArticleReadingId id, UUID userId, ArticleTopic topic,
                                               ArticleLevel level, String title, ArticleStatus status,
                                               List<ArticleParagraph> paragraphs, Instant createdAt) {
        return new ArticleReading(id, userId, topic, level, title, status, paragraphs, createdAt);
    }

    public ArticleReading withTitleAndParagraphs(String title, List<ArticleParagraph> paragraphs) {
        return new ArticleReading(id, userId, topic, level, title, status, paragraphs, createdAt);
    }

    public ArticleReading markProcessing() {
        return new ArticleReading(id, userId, topic, level, title, ArticleStatus.PROCESSING, paragraphs, createdAt);
    }

    public ArticleReading markReady(String title, List<ArticleParagraph> paragraphs) {
        return new ArticleReading(id, userId, topic, level, title, ArticleStatus.READY, paragraphs, createdAt);
    }

    public ArticleReading markFailed() {
        return new ArticleReading(id, userId, topic, level, title, ArticleStatus.FAILED, paragraphs, createdAt);
    }

    public ArticleReading complete() throws ArticleAlreadyCompletedException {
        if (status == ArticleStatus.COMPLETED) {
            throw new ArticleAlreadyCompletedException(id);
        }
        ArticleReading completed = new ArticleReading(id, userId, topic, level, title,
                ArticleStatus.COMPLETED, paragraphs, createdAt);
        completed.registerEvent(new ArticleReadingCompletedEvent(id.value(), userId));
        return completed;
    }

    public ArticleReadingId id() { return id; }
    public UUID userId() { return userId; }
    public ArticleTopic topic() { return topic; }
    public ArticleLevel level() { return level; }
    public String title() { return title; }
    public ArticleStatus status() { return status; }
    public List<ArticleParagraph> paragraphs() { return paragraphs; }
    public Instant createdAt() { return createdAt; }
}
