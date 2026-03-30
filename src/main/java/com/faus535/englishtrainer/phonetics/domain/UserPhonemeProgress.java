package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class UserPhonemeProgress extends AggregateRoot<UserPhonemeProgressId> {
    private final UserPhonemeProgressId id;
    private final UserProfileId userId;
    private final PhonemeId phonemeId;
    private final PhonemePracticePhraseId phraseId;
    private final int attemptsCount;
    private final int correctAttemptsCount;
    private final int bestScore;
    private final boolean phraseCompleted;
    private final Instant lastAttemptAt;

    private UserPhonemeProgress(UserPhonemeProgressId id, UserProfileId userId,
                                 PhonemeId phonemeId, PhonemePracticePhraseId phraseId,
                                 int attemptsCount, int correctAttemptsCount, int bestScore,
                                 boolean phraseCompleted, Instant lastAttemptAt) {
        this.id = id;
        this.userId = userId;
        this.phonemeId = phonemeId;
        this.phraseId = phraseId;
        this.attemptsCount = attemptsCount;
        this.correctAttemptsCount = correctAttemptsCount;
        this.bestScore = bestScore;
        this.phraseCompleted = phraseCompleted;
        this.lastAttemptAt = lastAttemptAt;
    }

    public static UserPhonemeProgress create(UserProfileId userId, PhonemeId phonemeId,
                                               PhonemePracticePhraseId phraseId) {
        return new UserPhonemeProgress(
            UserPhonemeProgressId.generate(), userId, phonemeId, phraseId,
            0, 0, 0, false, null
        );
    }

    public static UserPhonemeProgress reconstitute(UserPhonemeProgressId id, UserProfileId userId,
                                                     PhonemeId phonemeId, PhonemePracticePhraseId phraseId,
                                                     int attemptsCount, int correctAttemptsCount,
                                                     int bestScore, boolean phraseCompleted,
                                                     Instant lastAttemptAt) {
        return new UserPhonemeProgress(id, userId, phonemeId, phraseId,
            attemptsCount, correctAttemptsCount, bestScore, phraseCompleted, lastAttemptAt);
    }

    public UserPhonemeProgress recordAttempt(int score) {
        boolean isCorrect = score >= 60;
        int newAttempts = attemptsCount + 1;
        int newCorrect = correctAttemptsCount + (isCorrect ? 1 : 0);
        int newBest = Math.max(bestScore, score);
        boolean newCompleted = phraseCompleted || isCorrect;
        return new UserPhonemeProgress(id, userId, phonemeId, phraseId,
            newAttempts, newCorrect, newBest, newCompleted, Instant.now());
    }

    public UserPhonemeProgressId id() { return id; }
    public UserProfileId userId() { return userId; }
    public PhonemeId phonemeId() { return phonemeId; }
    public PhonemePracticePhraseId phraseId() { return phraseId; }
    public int attemptsCount() { return attemptsCount; }
    public int correctAttemptsCount() { return correctAttemptsCount; }
    public int bestScore() { return bestScore; }
    public boolean phraseCompleted() { return phraseCompleted; }
    public Instant lastAttemptAt() { return lastAttemptAt; }
}
