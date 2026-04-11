package com.faus535.englishtrainer.review.infrastructure.persistence;

import com.faus535.englishtrainer.review.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "review_items")
class ReviewItemEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "source_type", nullable = false, length = 30)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "front_content", nullable = false, columnDefinition = "TEXT")
    private String frontContent;

    @Column(name = "back_content", nullable = false, columnDefinition = "TEXT")
    private String backContent;

    @Column(name = "next_review_at", nullable = false)
    private LocalDate nextReviewAt;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    @Column(name = "ease_factor", nullable = false, precision = 4, scale = 2)
    private BigDecimal easeFactor;

    @Column(name = "repetitions", nullable = false)
    private int repetitions;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "context_sentence", columnDefinition = "TEXT")
    private String contextSentence;

    @Column(name = "context_translation", columnDefinition = "TEXT")
    private String contextTranslation;

    @Column(name = "target_word", columnDefinition = "TEXT")
    private String targetWord;

    @Column(name = "target_translation", columnDefinition = "TEXT")
    private String targetTranslation;

    protected ReviewItemEntity() {}

    static ReviewItemEntity fromDomain(ReviewItem item) {
        ReviewItemEntity entity = new ReviewItemEntity();
        entity.id = item.id().value();
        entity.isNew = true;
        entity.userId = item.userId();
        entity.sourceType = item.sourceType().value();
        entity.sourceId = item.sourceId();
        entity.frontContent = item.frontContent();
        entity.backContent = item.backContent();
        entity.nextReviewAt = item.nextReviewAt();
        entity.intervalDays = item.intervalDays();
        entity.easeFactor = BigDecimal.valueOf(item.easeFactor());
        entity.repetitions = item.repetitions();
        entity.createdAt = item.createdAt();
        entity.contextSentence = item.contextSentence();
        entity.contextTranslation = item.contextTranslation();
        entity.targetWord = item.targetWord();
        entity.targetTranslation = item.targetTranslation();
        return entity;
    }

    ReviewItem toDomain() {
        return ReviewItem.reconstitute(
                new ReviewItemId(id), userId, ReviewSourceType.fromString(sourceType),
                sourceId, frontContent, backContent, nextReviewAt,
                intervalDays, easeFactor.doubleValue(), repetitions, createdAt,
                contextSentence, contextTranslation, targetWord, targetTranslation);
    }

    void updateFrom(ReviewItem item) {
        this.nextReviewAt = item.nextReviewAt();
        this.intervalDays = item.intervalDays();
        this.easeFactor = BigDecimal.valueOf(item.easeFactor());
        this.repetitions = item.repetitions();
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
