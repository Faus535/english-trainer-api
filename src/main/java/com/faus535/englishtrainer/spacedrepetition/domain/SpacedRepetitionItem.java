package com.faus535.englishtrainer.spacedrepetition.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.spacedrepetition.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.time.LocalDate;

public final class SpacedRepetitionItem extends AggregateRoot<SpacedRepetitionItemId> {

    private static final int[] INTERVALS = {1, 3, 7, 14, 30};
    private static final int MAX_REVIEWS = 5;

    private final SpacedRepetitionItemId id;
    private final UserProfileId userId;
    private final String unitReference;
    private final String moduleName;
    private final String level;
    private final int unitIndex;
    private final String itemType;
    private final LocalDate nextReviewDate;
    private final int intervalIndex;
    private final int reviewCount;
    private final boolean graduated;
    private final Instant createdAt;

    private SpacedRepetitionItem(SpacedRepetitionItemId id, UserProfileId userId, String unitReference,
                                 String moduleName, String level, int unitIndex, String itemType,
                                 LocalDate nextReviewDate, int intervalIndex, int reviewCount,
                                 boolean graduated, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.unitReference = unitReference;
        this.moduleName = moduleName;
        this.level = level;
        this.unitIndex = unitIndex;
        this.itemType = itemType;
        this.nextReviewDate = nextReviewDate;
        this.intervalIndex = intervalIndex;
        this.reviewCount = reviewCount;
        this.graduated = graduated;
        this.createdAt = createdAt;
    }

    public static SpacedRepetitionItem create(UserProfileId userId, String moduleName, String level, int unitIndex) {
        String unitReference = moduleName + "-" + level + "-" + unitIndex;
        return new SpacedRepetitionItem(
                SpacedRepetitionItemId.generate(),
                userId,
                unitReference,
                moduleName,
                level,
                unitIndex,
                "module-unit",
                LocalDate.now().plusDays(1),
                0,
                0,
                false,
                Instant.now()
        );
    }

    public static SpacedRepetitionItem createForVocabulary(UserProfileId userId, String word, String level) {
        String normalizedWord = word.toLowerCase().trim();
        String unitReference = "vocab-" + normalizedWord;
        return new SpacedRepetitionItem(
                SpacedRepetitionItemId.generate(),
                userId,
                unitReference,
                "vocabulary-word",
                level,
                0,
                "vocabulary-word",
                LocalDate.now().plusDays(1),
                0,
                0,
                false,
                Instant.now()
        );
    }

    public static SpacedRepetitionItem reconstitute(SpacedRepetitionItemId id, UserProfileId userId,
                                                     String unitReference, String moduleName, String level,
                                                     int unitIndex, String itemType, LocalDate nextReviewDate,
                                                     int intervalIndex, int reviewCount, boolean graduated,
                                                     Instant createdAt) {
        return new SpacedRepetitionItem(id, userId, unitReference, moduleName, level, unitIndex,
                itemType != null ? itemType : "module-unit", nextReviewDate, intervalIndex, reviewCount, graduated, createdAt);
    }

    public SpacedRepetitionItem completeReview() {
        int newReviewCount = reviewCount + 1;
        int newIntervalIndex = Math.min(intervalIndex + 1, INTERVALS.length - 1);
        LocalDate newNextReviewDate = LocalDate.now().plusDays(INTERVALS[newIntervalIndex]);
        boolean newGraduated = newReviewCount >= MAX_REVIEWS;
        SpacedRepetitionItem updated = new SpacedRepetitionItem(id, userId, unitReference, moduleName, level, unitIndex,
                itemType, newNextReviewDate, newIntervalIndex, newReviewCount, newGraduated, createdAt);
        updated.registerEvent(new ReviewCompletedEvent(id, userId, newGraduated, itemType, unitReference));
        return updated;
    }

    public boolean isDueToday() {
        return !graduated && !nextReviewDate.isAfter(LocalDate.now());
    }

    public SpacedRepetitionItemId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String unitReference() { return unitReference; }
    public String moduleName() { return moduleName; }
    public String level() { return level; }
    public int unitIndex() { return unitIndex; }
    public String itemType() { return itemType; }
    public LocalDate nextReviewDate() { return nextReviewDate; }
    public int intervalIndex() { return intervalIndex; }
    public int reviewCount() { return reviewCount; }
    public boolean graduated() { return graduated; }
    public Instant createdAt() { return createdAt; }
}
