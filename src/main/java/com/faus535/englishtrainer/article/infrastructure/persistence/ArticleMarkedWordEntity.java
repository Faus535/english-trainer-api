package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWordId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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

    @Column(name = "english_definition", columnDefinition = "TEXT")
    private String englishDefinition;

    @Column(name = "context_sentence", columnDefinition = "TEXT")
    private String contextSentence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(columnDefinition = "TEXT")
    private String definition;

    @Column(length = 100)
    private String phonetics;

    @Column(columnDefinition = "TEXT[]")
    private String[] synonyms;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    @Column(name = "part_of_speech", length = 50)
    private String partOfSpeech;

    protected ArticleMarkedWordEntity() {}

    static ArticleMarkedWordEntity fromDomain(ArticleMarkedWord word) {
        ArticleMarkedWordEntity entity = new ArticleMarkedWordEntity();
        entity.id = word.id().value();
        entity.isNew = true;
        entity.articleReadingId = word.articleReadingId().value();
        entity.userId = word.userId();
        entity.wordOrPhrase = word.wordOrPhrase();
        entity.translation = word.translation();
        entity.englishDefinition = word.englishDefinition();
        entity.contextSentence = word.contextSentence();
        entity.createdAt = word.createdAt();
        entity.definition = word.definition();
        entity.phonetics = word.phonetics();
        entity.synonyms = word.synonyms() != null ? word.synonyms().toArray(String[]::new) : null;
        entity.exampleSentence = word.exampleSentence();
        entity.partOfSpeech = word.partOfSpeech();
        return entity;
    }

    static ArticleMarkedWordEntity fromDomainForUpdate(ArticleMarkedWord word) {
        ArticleMarkedWordEntity entity = fromDomain(word);
        entity.isNew = false;
        return entity;
    }

    ArticleMarkedWord toDomain() {
        return ArticleMarkedWord.reconstitute(
                new ArticleMarkedWordId(id),
                new ArticleReadingId(articleReadingId),
                userId, wordOrPhrase, translation, englishDefinition, contextSentence, createdAt,
                definition, phonetics,
                synonyms != null ? Arrays.asList(synonyms) : null,
                exampleSentence, partOfSpeech);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
