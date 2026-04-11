package com.faus535.englishtrainer.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaReviewItemRepository extends JpaRepository<ReviewItemEntity, UUID> {

    @Query("SELECT r FROM ReviewItemEntity r WHERE r.userId = :userId AND r.nextReviewAt <= :now ORDER BY r.nextReviewAt ASC")
    List<ReviewItemEntity> findDueByUserId(UUID userId, Instant now);

    Optional<ReviewItemEntity> findByUserIdAndSourceTypeAndSourceId(UUID userId, String sourceType, UUID sourceId);

    int countByUserId(UUID userId);

    @Query("SELECT COUNT(r) FROM ReviewItemEntity r WHERE r.userId = :userId AND r.nextReviewAt <= :now")
    int countDueByUserId(UUID userId, Instant now);

    @Query("SELECT COUNT(r) FROM ReviewItemEntity r WHERE r.userId = :userId AND r.intervalDays >= 21")
    long countMasteredByUserId(UUID userId);
}
