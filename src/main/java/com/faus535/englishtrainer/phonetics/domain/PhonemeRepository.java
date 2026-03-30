package com.faus535.englishtrainer.phonetics.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhonemeRepository {
    List<Phoneme> findAll();
    Optional<Phoneme> findById(PhonemeId id);
    List<Phoneme> findUncompletedByUserOrderedByDifficulty(UUID userId);
}
