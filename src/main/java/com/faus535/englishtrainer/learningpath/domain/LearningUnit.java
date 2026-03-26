package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.learningpath.domain.event.UnitMasteredEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class LearningUnit extends AggregateRoot<LearningUnitId> {

    private final LearningUnitId id;
    private final LearningPathId learningPathId;
    private final int unitIndex;
    private final String unitName;
    private final String targetLevel;
    private final UnitStatus status;
    private final MasteryScore masteryScore;
    private final List<UnitContent> contents;
    private final Instant completedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private LearningUnit(LearningUnitId id, LearningPathId learningPathId, int unitIndex,
                         String unitName, String targetLevel, UnitStatus status,
                         MasteryScore masteryScore, List<UnitContent> contents,
                         Instant completedAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.learningPathId = learningPathId;
        this.unitIndex = unitIndex;
        this.unitName = unitName;
        this.targetLevel = targetLevel;
        this.status = status;
        this.masteryScore = masteryScore;
        this.contents = List.copyOf(contents);
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LearningUnit create(LearningPathId learningPathId, int unitIndex,
                                       String unitName, String targetLevel,
                                       List<UnitContent> contents) {
        return new LearningUnit(
                LearningUnitId.generate(),
                learningPathId,
                unitIndex,
                unitName,
                targetLevel,
                UnitStatus.NOT_STARTED,
                new MasteryScore(0),
                contents,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    public static LearningUnit reconstitute(LearningUnitId id, LearningPathId learningPathId,
                                             int unitIndex, String unitName, String targetLevel,
                                             UnitStatus status, MasteryScore masteryScore,
                                             List<UnitContent> contents, Instant completedAt,
                                             Instant createdAt, Instant updatedAt) {
        return new LearningUnit(id, learningPathId, unitIndex, unitName, targetLevel,
                status, masteryScore, contents, completedAt, createdAt, updatedAt);
    }

    public LearningUnit startUnit() {
        return new LearningUnit(id, learningPathId, unitIndex, unitName, targetLevel,
                UnitStatus.IN_PROGRESS, masteryScore, contents, completedAt,
                createdAt, Instant.now());
    }

    public LearningUnit updateMastery(MasteryScore newScore, UserProfileId userId) {
        UnitStatus newStatus;
        Instant newCompletedAt = completedAt;

        if (newScore.isMastered()) {
            newStatus = UnitStatus.MASTERED;
            newCompletedAt = Instant.now();
        } else if (newScore.needsReview()) {
            newStatus = UnitStatus.NEEDS_REVIEW;
        } else {
            newStatus = UnitStatus.IN_PROGRESS;
        }

        LearningUnit updated = new LearningUnit(id, learningPathId, unitIndex, unitName,
                targetLevel, newStatus, newScore, contents, newCompletedAt,
                createdAt, Instant.now());

        if (newStatus == UnitStatus.MASTERED && status != UnitStatus.MASTERED) {
            updated.registerEvent(new UnitMasteredEvent(id, learningPathId, userId));
        }

        return updated;
    }

    public LearningUnit markContentPracticed(UUID contentId) {
        List<UnitContent> updatedContents = contents.stream()
                .map(c -> c.contentId().equals(contentId)
                        ? new UnitContent(c.contentType(), c.contentId(), true, Instant.now())
                        : c)
                .toList();

        return new LearningUnit(id, learningPathId, unitIndex, unitName, targetLevel,
                status, masteryScore, updatedContents, completedAt, createdAt, Instant.now());
    }

    public List<UnitContent> unpracticedContents() {
        return contents.stream()
                .filter(c -> !c.practiced())
                .toList();
    }

    public List<UnitContent> unpracticedContentsByType(ContentType contentType) {
        return contents.stream()
                .filter(c -> !c.practiced() && c.contentType() == contentType)
                .toList();
    }

    public LearningUnitId id() { return id; }
    public LearningPathId learningPathId() { return learningPathId; }
    public int unitIndex() { return unitIndex; }
    public String unitName() { return unitName; }
    public String targetLevel() { return targetLevel; }
    public UnitStatus status() { return status; }
    public MasteryScore masteryScore() { return masteryScore; }
    public List<UnitContent> contents() { return contents; }
    public Instant completedAt() { return completedAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
