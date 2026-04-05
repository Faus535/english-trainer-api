package com.faus535.englishtrainer.review.infrastructure.persistence;

import com.faus535.englishtrainer.review.domain.*;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
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
    private Instant nextReviewAt;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays;

    @Column(name = "ease_factor", nullable = false)
    private double easeFactor;

    @Column(name = "consecutive_correct", nullable = false)
    private int consecutiveCorrect;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

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
        entity.easeFactor = item.easeFactor();
        entity.consecutiveCorrect = item.consecutiveCorrect();
        entity.createdAt = item.createdAt();
        return entity;
    }

    ReviewItem toDomain() {
        return ReviewItem.reconstitute(
                new ReviewItemId(id), userId, ReviewSourceType.fromString(sourceType),
                sourceId, frontContent, backContent, nextReviewAt,
                intervalDays, easeFactor, consecutiveCorrect, createdAt);
    }

    void updateFrom(ReviewItem item) {
        this.nextReviewAt = item.nextReviewAt();
        this.intervalDays = item.intervalDays();
        this.easeFactor = item.easeFactor();
        this.consecutiveCorrect = item.consecutiveCorrect();
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
