package com.faus535.englishtrainer.phrase.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaPhraseRepository extends JpaRepository<PhraseEntity, UUID> {

    List<PhraseEntity> findByLevel(String level);
}
