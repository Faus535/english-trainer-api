package com.faus535.englishtrainer.review.domain;

import com.faus535.englishtrainer.review.domain.event.ReviewCompletedEvent;
import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class ReviewItem extends AggregateRoot<ReviewItemId> {

    private final ReviewItemId id;
    private final UUID userId;
    private final ReviewSourceType sourceType;
    private final UUID sourceId;
    private final String frontContent;
    private final String backContent;
    private final LocalDate nextReviewAt;
    private final int intervalDays;
    private final double easeFactor;
    private final int repetitions;
    private final Instant createdAt;
    private final String contextSentence;
    private final String contextTranslation;
    private final String targetWord;
    private final String targetTranslation;

    private ReviewItem(ReviewItemId id, UUID userId, ReviewSourceType sourceType, UUID sourceId,
                       String frontContent, String backContent, LocalDate nextReviewAt,
                       int intervalDays, double easeFactor, int repetitions, Instant createdAt,
                       String contextSentence, String contextTranslation, String targetWord, String targetTranslation) {
        this.id = id;
        this.userId = userId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.frontContent = frontContent;
        this.backContent = backContent;
        this.nextReviewAt = nextReviewAt;
        this.intervalDays = intervalDays;
        this.easeFactor = easeFactor;
        this.repetitions = repetitions;
        this.createdAt = createdAt;
        this.contextSentence = contextSentence;
        this.contextTranslation = contextTranslation;
        this.targetWord = targetWord;
        this.targetTranslation = targetTranslation;
    }

    public static ReviewItem create(UUID userId, ReviewSourceType sourceType, UUID sourceId,
                                     String frontContent, String backContent) {
        return new ReviewItem(
                ReviewItemId.generate(), userId, sourceType, sourceId,
                frontContent, backContent, LocalDate.now(),
                1, 2.5, 0, Instant.now(), null, null, null, null);
    }

    public static ReviewItem create(UUID userId, ReviewSourceType sourceType, UUID sourceId,
                                     String frontContent, String backContent,
                                     String contextSentence, String contextTranslation,
                                     String targetWord, String targetTranslation) {
        return new ReviewItem(
                ReviewItemId.generate(), userId, sourceType, sourceId,
                frontContent, backContent, LocalDate.now(),
                1, 2.5, 0, Instant.now(),
                contextSentence, contextTranslation, targetWord, targetTranslation);
    }

    public static ReviewItem reconstitute(ReviewItemId id, UUID userId, ReviewSourceType sourceType,
                                           UUID sourceId, String frontContent, String backContent,
                                           LocalDate nextReviewAt, int intervalDays, double easeFactor,
                                           int repetitions, Instant createdAt,
                                           String contextSentence, String contextTranslation,
                                           String targetWord, String targetTranslation) {
        return new ReviewItem(id, userId, sourceType, sourceId, frontContent, backContent,
                nextReviewAt, intervalDays, easeFactor, repetitions, createdAt,
                contextSentence, contextTranslation, targetWord, targetTranslation);
    }

    public ReviewItem review(int quality) {
        SM2Result result = SM2Algorithm.calculate(easeFactor, intervalDays, repetitions, quality);

        boolean graduated = result.newRepetitions() >= 5 && result.newIntervalDays() >= 21;

        ReviewItem updated = new ReviewItem(id, userId, sourceType, sourceId,
                frontContent, backContent, result.nextReviewAt(), result.newIntervalDays(),
                result.newEaseFactor(), result.newRepetitions(), createdAt,
                contextSentence, contextTranslation, targetWord, targetTranslation);
        updated.registerEvent(new ReviewCompletedEvent(id, userId, graduated));
        return updated;
    }

    public boolean isDue(LocalDate today) {
        return !nextReviewAt.isAfter(today);
    }

    public ReviewItemId id() { return id; }
    public UUID userId() { return userId; }
    public ReviewSourceType sourceType() { return sourceType; }
    public UUID sourceId() { return sourceId; }
    public String frontContent() { return frontContent; }
    public String backContent() { return backContent; }
    public LocalDate nextReviewAt() { return nextReviewAt; }
    public int intervalDays() { return intervalDays; }
    public double easeFactor() { return easeFactor; }
    public int repetitions() { return repetitions; }
    public Instant createdAt() { return createdAt; }
    public String contextSentence() { return contextSentence; }
    public String contextTranslation() { return contextTranslation; }
    public String targetWord() { return targetWord; }
    public String targetTranslation() { return targetTranslation; }
}
