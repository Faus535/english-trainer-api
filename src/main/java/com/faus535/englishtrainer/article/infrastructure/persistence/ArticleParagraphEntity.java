package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.ArticleParagraph;
import com.faus535.englishtrainer.article.domain.ArticleParagraphId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleSpeaker;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "article_paragraphs")
class ArticleParagraphEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    private boolean isNew;

    @Column(name = "article_reading_id", nullable = false)
    private UUID articleReadingId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(length = 5, nullable = false)
    private String speaker;

    protected ArticleParagraphEntity() {}

    static ArticleParagraphEntity fromDomain(ArticleParagraph paragraph) {
        ArticleParagraphEntity entity = new ArticleParagraphEntity();
        entity.id = paragraph.id().value();
        entity.isNew = true;
        entity.articleReadingId = paragraph.articleReadingId().value();
        entity.content = paragraph.content();
        entity.orderIndex = paragraph.orderIndex();
        entity.speaker = paragraph.speaker().name();
        return entity;
    }

    ArticleParagraph toDomain() {
        return ArticleParagraph.reconstitute(
                new ArticleParagraphId(id),
                new ArticleReadingId(articleReadingId),
                content,
                orderIndex,
                ArticleSpeaker.fromString(speaker));
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
