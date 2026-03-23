package com.faus535.englishtrainer.tutorerror.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class TutorError extends AggregateRoot<TutorErrorId> {

    private final TutorErrorId id;
    private final UserProfileId userId;
    private final String errorType;
    private final String originalText;
    private final String correctedText;
    private final String rule;
    private final int occurrenceCount;
    private final Instant firstSeen;
    private final Instant lastSeen;

    private TutorError(TutorErrorId id, UserProfileId userId, String errorType,
                       String originalText, String correctedText, String rule,
                       int occurrenceCount, Instant firstSeen, Instant lastSeen) {
        this.id = id;
        this.userId = userId;
        this.errorType = errorType;
        this.originalText = originalText;
        this.correctedText = correctedText;
        this.rule = rule;
        this.occurrenceCount = occurrenceCount;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
    }

    public static TutorError create(UserProfileId userId, String errorType,
                                     String originalText, String correctedText, String rule) {
        Instant now = Instant.now();
        return new TutorError(TutorErrorId.generate(), userId, errorType,
                originalText, correctedText, rule, 1, now, now);
    }

    public static TutorError reconstitute(TutorErrorId id, UserProfileId userId, String errorType,
                                            String originalText, String correctedText, String rule,
                                            int occurrenceCount, Instant firstSeen, Instant lastSeen) {
        return new TutorError(id, userId, errorType, originalText, correctedText, rule,
                occurrenceCount, firstSeen, lastSeen);
    }

    public TutorError incrementCount() {
        return new TutorError(id, userId, errorType, originalText, correctedText, rule,
                occurrenceCount + 1, firstSeen, Instant.now());
    }

    public TutorErrorId id() { return id; }
    public UserProfileId userId() { return userId; }
    public String errorType() { return errorType; }
    public String originalText() { return originalText; }
    public String correctedText() { return correctedText; }
    public String rule() { return rule; }
    public int occurrenceCount() { return occurrenceCount; }
    public Instant firstSeen() { return firstSeen; }
    public Instant lastSeen() { return lastSeen; }
}
