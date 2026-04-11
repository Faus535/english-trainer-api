package com.faus535.englishtrainer.review.infrastructure;

import com.faus535.englishtrainer.review.domain.ReviewResult;
import com.faus535.englishtrainer.review.domain.ReviewResultRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryReviewResultRepository implements ReviewResultRepository {

    private final List<ReviewResult> store = new ArrayList<>();

    @Override
    public ReviewResult save(ReviewResult result) {
        store.add(result);
        return result;
    }

    @Override
    public int countByUserIdAndReviewedAtAfter(UUID userId, Instant after) {
        return (int) store.stream()
                .filter(r -> r.userId().equals(userId))
                .filter(r -> r.reviewedAt().isAfter(after))
                .count();
    }

    @Override
    public long countCorrectByUserIdSince(UUID userId, Instant since) {
        return store.stream()
                .filter(r -> r.userId().equals(userId))
                .filter(r -> !r.reviewedAt().isBefore(since))
                .filter(r -> r.quality() >= 3)
                .count();
    }

    public List<ReviewResult> findAll() {
        return List.copyOf(store);
    }
}
