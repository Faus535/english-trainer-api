package com.faus535.englishtrainer.article.domain;

import java.time.Instant;
import java.util.UUID;

public final class ArticleMarkedWord {

    private final ArticleMarkedWordId id;
    private final ArticleReadingId articleReadingId;
    private final UUID userId;
    private final String wordOrPhrase;
    private final String translation;
    private final String contextSentence;
    private final Instant createdAt;

    private ArticleMarkedWord(ArticleMarkedWordId id, ArticleReadingId articleReadingId,
                               UUID userId, String wordOrPhrase, String translation,
                               String contextSentence, Instant createdAt) {
        this.id = id;
        this.articleReadingId = articleReadingId;
        this.userId = userId;
        this.wordOrPhrase = wordOrPhrase;
        this.translation = translation;
        this.contextSentence = contextSentence;
        this.createdAt = createdAt;
    }

    public static ArticleMarkedWord create(ArticleReadingId articleReadingId, UUID userId,
                                            String wordOrPhrase, String translation, String contextSentence) {
        return new ArticleMarkedWord(ArticleMarkedWordId.generate(), articleReadingId, userId,
                wordOrPhrase, translation, contextSentence, Instant.now());
    }

    public static ArticleMarkedWord reconstitute(ArticleMarkedWordId id, ArticleReadingId articleReadingId,
                                                  UUID userId, String wordOrPhrase, String translation,
                                                  String contextSentence, Instant createdAt) {
        return new ArticleMarkedWord(id, articleReadingId, userId, wordOrPhrase, translation, contextSentence, createdAt);
    }

    public ArticleMarkedWordId id() { return id; }
    public ArticleReadingId articleReadingId() { return articleReadingId; }
    public UUID userId() { return userId; }
    public String wordOrPhrase() { return wordOrPhrase; }
    public String translation() { return translation; }
    public String contextSentence() { return contextSentence; }
    public Instant createdAt() { return createdAt; }
}
