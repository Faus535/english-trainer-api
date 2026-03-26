package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.util.List;

public final class LearningPath extends AggregateRoot<LearningPathId> {

    private final LearningPathId id;
    private final UserProfileId userId;
    private final String currentLevel;
    private final int currentUnitIndex;
    private final List<LearningUnitId> unitIds;
    private final Instant createdAt;
    private final Instant updatedAt;

    private LearningPath(LearningPathId id, UserProfileId userId, String currentLevel,
                         int currentUnitIndex, List<LearningUnitId> unitIds,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.currentLevel = currentLevel;
        this.currentUnitIndex = currentUnitIndex;
        this.unitIds = List.copyOf(unitIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LearningPath create(UserProfileId userId, String currentLevel,
                                       List<LearningUnitId> unitIds) {
        return new LearningPath(
                LearningPathId.generate(),
                userId,
                currentLevel,
                0,
                unitIds,
                Instant.now(),
                Instant.now()
        );
    }

    public static LearningPath reconstitute(LearningPathId id, UserProfileId userId,
                                             String currentLevel, int currentUnitIndex,
                                             List<LearningUnitId> unitIds,
                                             Instant createdAt, Instant updatedAt) {
        return new LearningPath(id, userId, currentLevel, currentUnitIndex, unitIds,
                createdAt, updatedAt);
    }

    public LearningUnitId currentUnitId() {
        if (isCompleted()) {
            return null;
        }
        return unitIds.get(currentUnitIndex);
    }

    public LearningPath advanceToNextUnit() {
        return new LearningPath(id, userId, currentLevel, currentUnitIndex + 1, unitIds,
                createdAt, Instant.now());
    }

    public boolean isCompleted() {
        return currentUnitIndex >= unitIds.size();
    }

    public LearningPathId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String currentLevel() { return currentLevel; }
    public int currentUnitIndex() { return currentUnitIndex; }
    public List<LearningUnitId> unitIds() { return unitIds; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
