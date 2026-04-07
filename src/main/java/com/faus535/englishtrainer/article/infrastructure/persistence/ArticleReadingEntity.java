package com.faus535.englishtrainer.article.infrastructure.persistence;

import com.faus535.englishtrainer.article.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "article_readings")
class ArticleReadingEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(length = 100, nullable = false)
    private String topic;

    @Column(length = 5, nullable = false)
    private String level;

    @Column(length = 300, nullable = false)
    private String title;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "article_reading_id")
    @OrderBy("orderIndex ASC")
    private List<ArticleParagraphEntity> paragraphs = new ArrayList<>();

    protected ArticleReadingEntity() {}

    static ArticleReadingEntity fromDomain(ArticleReading reading) {
        ArticleReadingEntity entity = new ArticleReadingEntity();
        entity.id = reading.id().value();
        entity.isNew = true;
        entity.userId = reading.userId();
        entity.topic = reading.topic().value();
        entity.level = reading.level().value();
        entity.title = reading.title();
        entity.status = reading.status().value();
        entity.createdAt = reading.createdAt();
        entity.paragraphs = reading.paragraphs().stream()
                .map(ArticleParagraphEntity::fromDomain)
                .toList();
        return entity;
    }

    ArticleReading toDomain() {
        List<ArticleParagraph> domainParagraphs = paragraphs.stream()
                .map(ArticleParagraphEntity::toDomain)
                .toList();
        return ArticleReading.reconstitute(
                new ArticleReadingId(id),
                userId,
                new ArticleTopic(topic),
                ArticleLevel.fromString(level),
                title,
                ArticleStatus.fromString(status),
                domainParagraphs,
                createdAt);
    }

    void updateFrom(ArticleReading reading) {
        this.title = reading.title();
        this.status = reading.status().value();
        this.paragraphs.clear();
        this.paragraphs.addAll(reading.paragraphs().stream()
                .map(ArticleParagraphEntity::fromDomain)
                .toList());
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
