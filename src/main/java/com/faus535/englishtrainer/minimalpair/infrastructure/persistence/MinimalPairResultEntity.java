package com.faus535.englishtrainer.minimalpair.infrastructure.persistence;

import com.faus535.englishtrainer.minimalpair.domain.MinimalPairId;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResult;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPairResultId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "minimal_pair_results")
class MinimalPairResultEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pair_id", nullable = false)
    private UUID pairId;

    @Column(nullable = false)
    private boolean correct;

    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    protected MinimalPairResultEntity() {}

    static MinimalPairResultEntity fromAggregate(MinimalPairResult aggregate) {
        MinimalPairResultEntity entity = new MinimalPairResultEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.pairId = aggregate.pairId().value();
        entity.correct = aggregate.correct();
        entity.answeredAt = aggregate.answeredAt();
        return entity;
    }

    MinimalPairResult toAggregate() {
        return MinimalPairResult.reconstitute(
                new MinimalPairResultId(id),
                new UserProfileId(userId),
                new MinimalPairId(pairId),
                correct,
                answeredAt
        );
    }

    void markAsExisting() { this.isNew = false; }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
