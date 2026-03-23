package com.faus535.englishtrainer.tutorerror.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TutorErrorRepository {

    Optional<TutorError> findById(TutorErrorId id);

    List<TutorError> findByUserId(UserProfileId userId);

    List<TutorError> findByUserIdAndType(UserProfileId userId, String errorType);

    Optional<TutorError> findByUserIdAndOriginalAndCorrected(UserProfileId userId,
                                                              String originalText,
                                                              String correctedText);

    TutorError save(TutorError tutorError);

    List<Map<String, Object>> countByUserIdGroupedByWeek(UserProfileId userId, int weeks);
}
