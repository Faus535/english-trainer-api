package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.Instant;
import java.time.LocalDate;

public final class PhonemeDailyAssignmentMother {
    private PhonemeDailyAssignmentMother() {}

    public static PhonemeDailyAssignment create(UserProfileId userId, PhonemeId phonemeId) {
        return PhonemeDailyAssignment.create(userId, phonemeId, LocalDate.now());
    }

    public static PhonemeDailyAssignment completed(UserProfileId userId, PhonemeId phonemeId) {
        return PhonemeDailyAssignment.reconstitute(
            PhonemeDailyAssignmentId.generate(), userId, phonemeId,
            LocalDate.now(), true, Instant.now()
        );
    }
}
