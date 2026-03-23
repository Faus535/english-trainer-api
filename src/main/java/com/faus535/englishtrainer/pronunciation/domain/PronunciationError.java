package com.faus535.englishtrainer.pronunciation.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class PronunciationError extends AggregateRoot<PronunciationErrorId> {

    private final PronunciationErrorId id;
    private final UserProfileId userId;
    private final String word;
    private final String expectedPhoneme;
    private final String spokenPhoneme;
    private final int occurrenceCount;
    private final Instant lastOccurred;
    private final Instant createdAt;

    private PronunciationError(PronunciationErrorId id, UserProfileId userId, String word,
                                String expectedPhoneme, String spokenPhoneme,
                                int occurrenceCount, Instant lastOccurred, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.word = word;
        this.expectedPhoneme = expectedPhoneme;
        this.spokenPhoneme = spokenPhoneme;
        this.occurrenceCount = occurrenceCount;
        this.lastOccurred = lastOccurred;
        this.createdAt = createdAt;
    }

    public static PronunciationError create(UserProfileId userId, String word,
                                             String expectedPhoneme, String spokenPhoneme) {
        Instant now = Instant.now();
        return new PronunciationError(
                PronunciationErrorId.generate(), userId, word,
                expectedPhoneme, spokenPhoneme, 1, now, now);
    }

    public static PronunciationError reconstitute(PronunciationErrorId id, UserProfileId userId,
                                                    String word, String expectedPhoneme,
                                                    String spokenPhoneme, int occurrenceCount,
                                                    Instant lastOccurred, Instant createdAt) {
        return new PronunciationError(id, userId, word, expectedPhoneme, spokenPhoneme,
                occurrenceCount, lastOccurred, createdAt);
    }

    public PronunciationError incrementCount() {
        return new PronunciationError(id, userId, word, expectedPhoneme, spokenPhoneme,
                occurrenceCount + 1, Instant.now(), createdAt);
    }

    public PronunciationErrorId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String word() { return word; }
    public String expectedPhoneme() { return expectedPhoneme; }
    public String spokenPhoneme() { return spokenPhoneme; }
    public int occurrenceCount() { return occurrenceCount; }
    public Instant lastOccurred() { return lastOccurred; }
    public Instant createdAt() { return createdAt; }
}
