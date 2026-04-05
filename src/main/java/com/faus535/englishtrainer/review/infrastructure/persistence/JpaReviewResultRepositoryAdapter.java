package com.faus535.englishtrainer.review.infrastructure.persistence;

import com.faus535.englishtrainer.review.domain.ReviewResult;
import com.faus535.englishtrainer.review.domain.ReviewResultRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
class JpaReviewResultRepositoryAdapter implements ReviewResultRepository {

    private final JpaReviewResultRepository jpaRepository;

    JpaReviewResultRepositoryAdapter(JpaReviewResultRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ReviewResult save(ReviewResult result) {
        ReviewResultEntity entity = ReviewResultEntity.fromDomain(result);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public int countByUserIdAndReviewedAtAfter(UUID userId, Instant after) {
        return jpaRepository.countByUserIdAndReviewedAtAfter(userId, after);
    }
}
