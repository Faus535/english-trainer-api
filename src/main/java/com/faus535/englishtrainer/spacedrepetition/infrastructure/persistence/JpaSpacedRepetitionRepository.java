package com.faus535.englishtrainer.spacedrepetition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaSpacedRepetitionRepository extends JpaRepository<SpacedRepetitionItemEntity, UUID> {

    Optional<SpacedRepetitionItemEntity> findByUserIdAndUnitReference(UUID userId, String unitReference);

    List<SpacedRepetitionItemEntity> findByUserIdAndNextReviewDateLessThanEqualAndGraduatedFalse(UUID userId, LocalDate today);

    List<SpacedRepetitionItemEntity> findByUserId(UUID userId);
}
