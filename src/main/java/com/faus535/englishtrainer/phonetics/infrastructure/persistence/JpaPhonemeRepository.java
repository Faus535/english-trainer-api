package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface JpaPhonemeRepository extends JpaRepository<PhonemeEntity, UUID> {

    @Query("""
        SELECT p FROM PhonemeEntity p
        WHERE p.id NOT IN (
            SELECT a.phonemeId FROM PhonemeDailyAssignmentEntity a
            WHERE a.userId = :userId AND a.completed = true
        )
        ORDER BY p.difficultyOrder
    """)
    List<PhonemeEntity> findUncompletedByUserOrderedByDifficulty(UUID userId);
}
