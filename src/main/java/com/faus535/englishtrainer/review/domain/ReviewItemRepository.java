package com.faus535.englishtrainer.review.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewItemRepository {

    ReviewItem save(ReviewItem item);

    Optional<ReviewItem> findById(ReviewItemId id);

    List<ReviewItem> findDueByUserId(UUID userId, Instant now, int limit);

    Optional<ReviewItem> findByUserIdSourceTypeAndSourceId(UUID userId, ReviewSourceType sourceType, UUID sourceId);

    int countByUserId(UUID userId);

    int countDueByUserId(UUID userId, Instant now);
}
