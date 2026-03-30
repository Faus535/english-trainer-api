package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaPhonemePracticePhraseRepository extends JpaRepository<PhonemePracticePhraseEntity, UUID> {
    List<PhonemePracticePhraseEntity> findByPhonemeId(UUID phonemeId);
}
