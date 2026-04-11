package com.faus535.englishtrainer.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

interface JpaReviewResultRepository extends JpaRepository<ReviewResultEntity, UUID> {

    int countByUserIdAndReviewedAtAfter(UUID userId, Instant after);

    @Query("SELECT COUNT(r) FROM ReviewResultEntity r WHERE r.userId = :userId AND r.reviewedAt >= :since AND r.quality >= 3")
    long countCorrectByUserIdSince(UUID userId, Instant since);
}
