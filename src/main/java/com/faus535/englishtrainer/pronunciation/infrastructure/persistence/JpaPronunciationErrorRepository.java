package com.faus535.englishtrainer.pronunciation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaPronunciationErrorRepository extends JpaRepository<PronunciationErrorEntity, UUID> {

    List<PronunciationErrorEntity> findByUserId(UUID userId);

    Optional<PronunciationErrorEntity> findByUserIdAndWordAndExpectedPhoneme(UUID userId, String word, String expectedPhoneme);
}
