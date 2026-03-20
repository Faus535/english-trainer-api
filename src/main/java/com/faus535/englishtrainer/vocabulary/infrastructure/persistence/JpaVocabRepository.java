package com.faus535.englishtrainer.vocabulary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaVocabRepository extends JpaRepository<VocabEntryEntity, UUID> {

    List<VocabEntryEntity> findByLevel(String level);

    List<VocabEntryEntity> findByEnContainingIgnoreCaseOrEsContainingIgnoreCase(String en, String es);
}
