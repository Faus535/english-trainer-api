package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.Optional;

public interface PhonemeDailyAssignmentRepository {
    Optional<PhonemeDailyAssignment> findByUserAndDate(UserProfileId userId, LocalDate date);
    Optional<PhonemeDailyAssignment> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId);
    PhonemeDailyAssignment save(PhonemeDailyAssignment assignment);
}
