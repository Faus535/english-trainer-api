package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_marked_words")
class ArticleMarkedWordEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "article_reading_id", nullable = false)
    private UUID articleReadingId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "word_or_phrase", length = 200, nullable = false)
    private String wordOrPhrase;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String translation;

    @Column(name = "context_sentence", columnDefinition = "TEXT")
    private String contextSentence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ArticleMarkedWordEntity() {}

    static ArticleMarkedWordEntity fromDomain(ArticleMarkedWord word) {
        ArticleMarkedWordEntity entity = new ArticleMarkedWordEntity();
        entity.id = word.id().value();
        entity.isNew = true;
        entity.articleReadingId = word.articleReadingId().value();
        entity.userId = word.userId();
        entity.wordOrPhrase = word.wordOrPhrase();
        entity.translation = word.translation();
        entity.contextSentence = word.contextSentence();
        entity.createdAt = word.createdAt();
        return entity;
    }

    ArticleMarkedWord toDomain() {
        return ArticleMarkedWord.reconstitute(
                new ArticleMarkedWordId(id),
                new ArticleReadingId(articleReadingId),
                userId, wordOrPhrase, translation, contextSentence, createdAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
