package com.faus535.englishtrainer.tutorerror.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

interface JpaTutorErrorRepository extends JpaRepository<TutorErrorEntity, UUID> {

    List<TutorErrorEntity> findByUserIdOrderByOccurrenceCountDesc(UUID userId);

    List<TutorErrorEntity> findByUserIdAndErrorTypeOrderByOccurrenceCountDesc(UUID userId, String errorType);

    Optional<TutorErrorEntity> findByUserIdAndOriginalTextAndCorrectedText(UUID userId,
                                                                            String originalText,
                                                                            String correctedText);

    @Query(value = """
            SELECT DATE_TRUNC('week', last_seen) AS week_start,
                   COUNT(*) AS error_count
            FROM tutor_errors
            WHERE user_id = :userId
              AND last_seen >= NOW() - CAST(:weeks || ' weeks' AS INTERVAL)
            GROUP BY DATE_TRUNC('week', last_seen)
            ORDER BY week_start ASC
            """, nativeQuery = true)
    List<Map<String, Object>> countByUserIdGroupedByWeek(UUID userId, int weeks);
}
