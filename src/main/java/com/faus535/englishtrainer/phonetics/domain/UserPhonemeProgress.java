package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class UserPhonemeProgress extends AggregateRoot<UserPhonemeProgressId> {

    private final UserPhonemeProgressId id;
    private final UserProfileId userId;
    private final PhonemeId phonemeId;
    private final PhonemePracticePhraseId phraseId;
    private final boolean correct;
    private final Instant attemptedAt;

    private UserPhonemeProgress(UserPhonemeProgressId id, UserProfileId userId, PhonemeId phonemeId,
                                 PhonemePracticePhraseId phraseId, boolean correct, Instant attemptedAt) {
        this.id = id;
        this.userId = userId;
        this.phonemeId = phonemeId;
        this.phraseId = phraseId;
        this.correct = correct;
        this.attemptedAt = attemptedAt;
    }

    public static UserPhonemeProgress create(UserProfileId userId, PhonemeId phonemeId,
                                              PhonemePracticePhraseId phraseId, boolean correct) {
        return new UserPhonemeProgress(UserPhonemeProgressId.generate(), userId, phonemeId,
                phraseId, correct, Instant.now());
    }

    public static UserPhonemeProgress reconstitute(UserPhonemeProgressId id, UserProfileId userId,
                                                    PhonemeId phonemeId, PhonemePracticePhraseId phraseId,
                                                    boolean correct, Instant attemptedAt) {
        return new UserPhonemeProgress(id, userId, phonemeId, phraseId, correct, attemptedAt);
    }

    public UserPhonemeProgressId id() { return id; }
    public UserProfileId userId() { return userId; }
    public PhonemeId phonemeId() { return phonemeId; }
    public PhonemePracticePhraseId phraseId() { return phraseId; }
    public boolean correct() { return correct; }
    public Instant attemptedAt() { return attemptedAt; }
}
