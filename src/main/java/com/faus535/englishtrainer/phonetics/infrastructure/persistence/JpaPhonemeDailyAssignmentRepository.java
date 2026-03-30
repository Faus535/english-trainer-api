package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

interface JpaPhonemeDailyAssignmentRepository extends JpaRepository<PhonemeDailyAssignmentEntity, UUID> {
    Optional<PhonemeDailyAssignmentEntity> findByUserIdAndAssignedDate(UUID userId, LocalDate date);
    Optional<PhonemeDailyAssignmentEntity> findByUserIdAndPhonemeId(UUID userId, UUID phonemeId);
}
