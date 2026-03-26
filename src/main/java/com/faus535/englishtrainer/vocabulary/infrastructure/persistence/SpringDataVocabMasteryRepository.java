package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SpringDataVocabMasteryRepository extends JpaRepository<VocabMasteryEntity, UUID> {

    Optional<VocabMasteryEntity> findByUserIdAndVocabEntryId(UUID userId, UUID vocabEntryId);

    Optional<VocabMasteryEntity> findByUserIdAndWord(UUID userId, String word);

    List<VocabMasteryEntity> findByUserId(UUID userId);

    List<VocabMasteryEntity> findByUserIdAndStatus(UUID userId, String status);
}
