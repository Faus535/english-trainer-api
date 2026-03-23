package com.faus535.englishtrainer.vocabularycontext.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaVocabularyContextRepository extends JpaRepository<VocabularyContextEntity, UUID> {

    Optional<VocabularyContextEntity> findByVocabularyIdAndLevel(UUID vocabularyId, String level);
}
