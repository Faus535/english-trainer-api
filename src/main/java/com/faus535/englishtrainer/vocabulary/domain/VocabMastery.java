package com.faus535.englishtrainer.vocabulary.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.vocabulary.domain.event.WordLearnedEvent;
import com.faus535.englishtrainer.vocabulary.domain.event.WordMasteredEvent;

import java.time.Instant;

public final class VocabMastery extends AggregateRoot<VocabMasteryId> {

    private final VocabMasteryId id;
    private final UserProfileId userId;
    private final VocabEntryId vocabEntryId;
    private final String word;
    private final int correctCount;
    private final int incorrectCount;
    private final int totalAttempts;
    private final VocabMasteryStatus status;
    private final MasterySource source;
    private final Instant lastPracticedAt;
    private final Instant learnedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    private VocabMastery(VocabMasteryId id, UserProfileId userId, VocabEntryId vocabEntryId, String word,
                         int correctCount, int incorrectCount, int totalAttempts, VocabMasteryStatus status,
                         MasterySource source, Instant lastPracticedAt, Instant learnedAt,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.vocabEntryId = vocabEntryId;
        this.word = word;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.totalAttempts = totalAttempts;
        this.status = status;
        this.source = source;
        this.lastPracticedAt = lastPracticedAt;
        this.learnedAt = learnedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static VocabMastery create(UserProfileId userId, VocabEntryId vocabEntryId, String word,
                                       MasterySource source) {
        Instant now = Instant.now();
        return new VocabMastery(
                VocabMasteryId.generate(),
                userId,
                vocabEntryId,
                word,
                0,
                0,
                0,
                VocabMasteryStatus.LEARNING,
                source,
                now,
                null,
                now,
                now
        );
    }

    public static VocabMastery reconstitute(VocabMasteryId id, UserProfileId userId, VocabEntryId vocabEntryId,
                                             String word, int correctCount, int incorrectCount, int totalAttempts,
                                             VocabMasteryStatus status, MasterySource source,
                                             Instant lastPracticedAt, Instant learnedAt,
                                             Instant createdAt, Instant updatedAt) {
        return new VocabMastery(id, userId, vocabEntryId, word, correctCount, incorrectCount, totalAttempts,
                status, source, lastPracticedAt, learnedAt, createdAt, updatedAt);
    }

    public VocabMastery recordCorrectAnswer() {
        int newCorrectCount = correctCount + 1;
        int newTotalAttempts = totalAttempts + 1;
        Instant now = Instant.now();

        VocabMasteryStatus newStatus = recalculateStatus(newCorrectCount, incorrectCount, newTotalAttempts, status);
        Instant newLearnedAt = learnedAt;

        VocabMastery updated = new VocabMastery(id, userId, vocabEntryId, word, newCorrectCount, incorrectCount,
                newTotalAttempts, newStatus, source, now, newLearnedAt, createdAt, now);

        if (newStatus == VocabMasteryStatus.LEARNED && status != VocabMasteryStatus.LEARNED
                && status != VocabMasteryStatus.MASTERED) {
            updated = new VocabMastery(id, userId, vocabEntryId, word, newCorrectCount, incorrectCount,
                    newTotalAttempts, newStatus, source, now, now, createdAt, now);
            updated.registerEvent(new WordLearnedEvent(id, userId, vocabEntryId, word));
        }

        return updated;
    }

    public VocabMastery recordIncorrectAnswer() {
        int newIncorrectCount = incorrectCount + 1;
        int newTotalAttempts = totalAttempts + 1;
        Instant now = Instant.now();

        VocabMasteryStatus newStatus = recalculateStatus(correctCount, newIncorrectCount, newTotalAttempts, status);

        return new VocabMastery(id, userId, vocabEntryId, word, correctCount, newIncorrectCount,
                newTotalAttempts, newStatus, source, now, learnedAt, createdAt, now);
    }

    public double accuracy() {
        if (totalAttempts == 0) {
            return 0.0;
        }
        return (double) correctCount / totalAttempts * 100.0;
    }

    public VocabMastery graduate() {
        Instant now = Instant.now();
        Instant newLearnedAt = learnedAt != null ? learnedAt : now;

        VocabMastery mastered = new VocabMastery(id, userId, vocabEntryId, word, correctCount, incorrectCount,
                totalAttempts, VocabMasteryStatus.MASTERED, source, now, newLearnedAt, createdAt, now);
        mastered.registerEvent(new WordMasteredEvent(id, userId, word));
        return mastered;
    }

    private static VocabMasteryStatus recalculateStatus(int correctCount, int incorrectCount, int totalAttempts,
                                                         VocabMasteryStatus currentStatus) {
        if (currentStatus == VocabMasteryStatus.MASTERED) {
            return VocabMasteryStatus.MASTERED;
        }

        double accuracy = totalAttempts > 0 ? (double) correctCount / totalAttempts * 100.0 : 0.0;

        if (currentStatus == VocabMasteryStatus.LEARNED && accuracy < 60.0) {
            return VocabMasteryStatus.LEARNING;
        }

        if (correctCount >= 3 && accuracy >= 70.0) {
            return VocabMasteryStatus.LEARNED;
        }

        return VocabMasteryStatus.LEARNING;
    }

    public VocabMasteryId id() { return id; }
    public UserProfileId userId() { return userId; }
    public VocabEntryId vocabEntryId() { return vocabEntryId; }
    public String word() { return word; }
    public int correctCount() { return correctCount; }
    public int incorrectCount() { return incorrectCount; }
    public int totalAttempts() { return totalAttempts; }
    public VocabMasteryStatus status() { return status; }
    public MasterySource source() { return source; }
    public Instant lastPracticedAt() { return lastPracticedAt; }
    public Instant learnedAt() { return learnedAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}
