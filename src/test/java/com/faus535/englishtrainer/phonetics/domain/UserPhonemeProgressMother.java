package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;

public final class UserPhonemeProgressMother {
    private UserPhonemeProgressMother() {}

    public static UserPhonemeProgress create(UserProfileId userId, PhonemeId phonemeId,
                                               PhonemePracticePhraseId phraseId) {
        return UserPhonemeProgress.create(userId, phonemeId, phraseId);
    }

    public static UserPhonemeProgress completed(UserProfileId userId, PhonemeId phonemeId,
                                                  PhonemePracticePhraseId phraseId) {
        return UserPhonemeProgress.reconstitute(
            UserPhonemeProgressId.generate(), userId, phonemeId, phraseId,
            3, 2, 85, true, Instant.now()
        );
    }
}
