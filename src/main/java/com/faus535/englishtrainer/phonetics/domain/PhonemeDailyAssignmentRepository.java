package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PhonemeDailyAssignmentRepository {

    Optional<PhonemeDailyAssignment> findByUserAndDate(UserProfileId userId, LocalDate date);

    Optional<PhonemeDailyAssignment> findByUserAndPhoneme(UserProfileId userId, PhonemeId phonemeId);

    List<PhonemeDailyAssignment> findCompletedByUser(UserProfileId userId);

    PhonemeDailyAssignment save(PhonemeDailyAssignment assignment);
}
