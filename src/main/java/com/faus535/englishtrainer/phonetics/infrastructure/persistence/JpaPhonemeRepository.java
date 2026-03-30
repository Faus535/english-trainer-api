package com.faus535.englishtrainer.phonetics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface JpaPhonemeRepository extends JpaRepository<PhonemeEntity, UUID> {
    List<PhonemeEntity> findAllByOrderByDifficultyOrderAsc();

    @Query(value = """
        SELECT p.* FROM phonemes p
        WHERE p.id NOT IN (
            SELECT da.phoneme_id FROM phoneme_daily_assignments da
            WHERE da.user_id = :userId AND da.completed = true
        )
        ORDER BY p.difficulty_order ASC
    """, nativeQuery = true)
    List<PhonemeEntity> findUncompletedByUserOrderedByDifficulty(@Param("userId") UUID userId);
}
