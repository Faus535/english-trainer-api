package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;
import java.util.Optional;

public interface PhonemeRepository {

    List<Phoneme> findAll();

    Optional<Phoneme> findById(PhonemeId id);

    List<Phoneme> findUncompletedByUserOrderedByDifficulty(UserProfileId userId);
}
