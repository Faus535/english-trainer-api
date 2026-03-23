package com.faus535.englishtrainer.pronunciation.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface PronunciationErrorRepository {

    PronunciationError save(PronunciationError error);

    List<PronunciationError> findByUserId(UserProfileId userId);

    Optional<PronunciationError> findByUserIdAndWordAndPhoneme(UserProfileId userId, String word, String expectedPhoneme);
}
