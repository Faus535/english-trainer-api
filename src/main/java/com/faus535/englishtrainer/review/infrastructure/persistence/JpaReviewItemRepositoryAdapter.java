package com.faus535.englishtrainer.review.infrastructure.persistence;

import com.faus535.englishtrainer.review.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaReviewItemRepositoryAdapter implements ReviewItemRepository {

    private final JpaReviewItemRepository jpaRepository;

    JpaReviewItemRepositoryAdapter(JpaReviewItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ReviewItem save(ReviewItem item) {
        Optional<ReviewItemEntity> existing = jpaRepository.findById(item.id().value());

        if (existing.isPresent()) {
            ReviewItemEntity entity = existing.get();
            entity.updateFrom(item);
            return jpaRepository.save(entity).toDomain();
        }

        ReviewItemEntity entity = ReviewItemEntity.fromDomain(item);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<ReviewItem> findById(ReviewItemId id) {
        return jpaRepository.findById(id.value())
                .map(ReviewItemEntity::toDomain);
    }

    @Override
    public List<ReviewItem> findDueByUserId(UUID userId, LocalDate today, int limit) {
        return jpaRepository.findDueByUserId(userId, today).stream()
                .limit(limit)
                .map(ReviewItemEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ReviewItem> findByUserIdSourceTypeAndSourceId(UUID userId, ReviewSourceType sourceType, UUID sourceId) {
        return jpaRepository.findByUserIdAndSourceTypeAndSourceId(userId, sourceType.value(), sourceId)
                .map(ReviewItemEntity::toDomain);
    }

    @Override
    public int countByUserId(UUID userId) {
        return jpaRepository.countByUserId(userId);
    }

    @Override
    public int countDueByUserId(UUID userId, LocalDate today) {
        return jpaRepository.countDueByUserId(userId, today);
    }

    @Override
    public long countMasteredByUserId(UUID userId) {
        return jpaRepository.countMasteredByUserId(userId);
    }

    @Override
    public double averageIntervalByUserId(UUID userId) {
        return jpaRepository.averageIntervalByUserId(userId);
    }
}
