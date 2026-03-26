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
@Table(name = "learning_units")
class LearningUnitEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    @Column(name = "learning_path_id", nullable = false)
    private UUID learningPathId;

    @Column(name = "unit_index", nullable = false)
    private int unitIndex;

    @Column(name = "unit_name", nullable = false)
    private String unitName;

    @Column(name = "target_level", nullable = false)
    private String targetLevel;

    @Column(nullable = false)
    private String status;

    @Column(name = "mastery_score", nullable = false)
    private int masteryScore;

    @Column(name = "contents_data")
    private String contentsData;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LearningUnitEntity() {}

    static LearningUnitEntity fromAggregate(UUID id, UUID learningPathId, int unitIndex,
                                             String unitName, String targetLevel, String status,
                                             int masteryScore, String contentsData,
                                             Instant completedAt, Instant createdAt,
                                             Instant updatedAt) {
        LearningUnitEntity entity = new LearningUnitEntity();
        entity.id = id;
        entity.isNew = true;
        entity.learningPathId = learningPathId;
        entity.unitIndex = unitIndex;
        entity.unitName = unitName;
        entity.targetLevel = targetLevel;
        entity.status = status;
        entity.masteryScore = masteryScore;
        entity.contentsData = contentsData;
        entity.completedAt = completedAt;
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

    UUID learningPathId() { return learningPathId; }
    int unitIndex() { return unitIndex; }
    String unitName() { return unitName; }
    String targetLevel() { return targetLevel; }
    String status() { return status; }
    int masteryScore() { return masteryScore; }
    String contentsData() { return contentsData; }
    Instant completedAt() { return completedAt; }
    Instant createdAt() { return createdAt; }
    Instant updatedAt() { return updatedAt; }
}
