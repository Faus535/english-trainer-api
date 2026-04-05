package com.faus535.englishtrainer.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

interface JpaReviewResultRepository extends JpaRepository<ReviewResultEntity, UUID> {

    int countByUserIdAndReviewedAtAfter(UUID userId, Instant after);
}
