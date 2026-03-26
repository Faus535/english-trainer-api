package com.faus535.englishtrainer.learningpath.infrastructure.persistence;

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
@Table(name = "learning_paths")
class LearningPathEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "user_profile_id", nullable = false)
    private UUID userProfileId;

    @Column(name = "current_level", nullable = false)
    private String currentLevel;

    @Column(name = "current_unit_index", nullable = false)
    private int currentUnitIndex;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LearningPathEntity() {}

    static LearningPathEntity fromAggregate(UUID id, UUID userProfileId, String currentLevel,
                                             int currentUnitIndex, Instant createdAt,
                                             Instant updatedAt) {
        LearningPathEntity entity = new LearningPathEntity();
        entity.id = id;
        entity.isNew = true;
        entity.userProfileId = userProfileId;
        entity.currentLevel = currentLevel;
        entity.currentUnitIndex = currentUnitIndex;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        return entity;
    }

    void markAsExisting() {
        this.isNew = false;
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }

    UUID userProfileId() { return userProfileId; }
    String currentLevel() { return currentLevel; }
    int currentUnitIndex() { return currentUnitIndex; }
    Instant createdAt() { return createdAt; }
    Instant updatedAt() { return updatedAt; }
}
