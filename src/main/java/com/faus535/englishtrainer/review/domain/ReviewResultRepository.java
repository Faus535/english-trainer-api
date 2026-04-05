package com.faus535.englishtrainer.review.domain;

import java.time.Instant;
import java.util.UUID;

public interface ReviewResultRepository {

    ReviewResult save(ReviewResult result);

    int countByUserIdAndReviewedAtAfter(UUID userId, Instant after);
}
