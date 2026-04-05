package com.faus535.englishtrainer.review.infrastructure.persistence;

import com.faus535.englishtrainer.review.domain.ReviewItemId;
import com.faus535.englishtrainer.review.domain.ReviewResult;
import com.faus535.englishtrainer.review.domain.ReviewResultId;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "review_results")
class ReviewResultEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int quality;

    @Column(name = "reviewed_at", nullable = false)
    private Instant reviewedAt;

    @Transient
    private boolean isNew = true;

    protected ReviewResultEntity() {}

    static ReviewResultEntity fromDomain(ReviewResult result) {
        ReviewResultEntity entity = new ReviewResultEntity();
        entity.id = result.id().value();
        entity.itemId = result.itemId().value();
        entity.userId = result.userId();
        entity.quality = result.quality();
        entity.reviewedAt = result.reviewedAt();
        return entity;
    }

    ReviewResult toDomain() {
        return new ReviewResult(
                new ReviewResultId(id), new ReviewItemId(itemId), userId, quality, reviewedAt);
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
