package com.faus535.englishtrainer.spacedrepetition.infrastructure.persistence;

import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItem;
import com.faus535.englishtrainer.spacedrepetition.domain.SpacedRepetitionItemId;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "spaced_repetition_items")
class SpacedRepetitionItemEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "unit_reference", nullable = false)
    private String unitReference;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @Column(nullable = false)
    private String level;

    @Column(name = "unit_index", nullable = false)
    private int unitIndex;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate;

    @Column(name = "interval_index", nullable = false)
    private int intervalIndex;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private boolean graduated;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SpacedRepetitionItemEntity() {}

    static SpacedRepetitionItemEntity fromAggregate(SpacedRepetitionItem aggregate) {
        SpacedRepetitionItemEntity entity = new SpacedRepetitionItemEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true;
        entity.userId = aggregate.userId().value();
        entity.unitReference = aggregate.unitReference();
        entity.moduleName = aggregate.moduleName();
        entity.level = aggregate.level();
        entity.unitIndex = aggregate.unitIndex();
        entity.itemType = aggregate.itemType();
        entity.nextReviewDate = aggregate.nextReviewDate();
        entity.intervalIndex = aggregate.intervalIndex();
        entity.reviewCount = aggregate.reviewCount();
        entity.graduated = aggregate.graduated();
        entity.createdAt = aggregate.createdAt();
        return entity;
    }

    SpacedRepetitionItem toAggregate() {
        return SpacedRepetitionItem.reconstitute(
                new SpacedRepetitionItemId(id),
                new UserProfileId(userId),
                unitReference,
                moduleName,
                level,
                unitIndex,
                itemType,
                nextReviewDate,
                intervalIndex,
                reviewCount,
                graduated,
                createdAt
        );
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
