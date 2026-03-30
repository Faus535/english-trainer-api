package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaUserPhonemeProgressRepository extends JpaRepository<UserPhonemeProgressEntity, UUID> {
    Optional<UserPhonemeProgressEntity> findByUserIdAndPhonemeIdAndPhraseId(UUID userId, UUID phonemeId, UUID phraseId);
    List<UserPhonemeProgressEntity> findByUserIdAndPhonemeId(UUID userId, UUID phonemeId);
}
