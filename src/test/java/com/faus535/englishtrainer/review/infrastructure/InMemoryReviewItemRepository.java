package com.faus535.englishtrainer.review.infrastructure;

import com.faus535.englishtrainer.review.domain.*;

import java.time.Instant;
import java.util.*;

public class InMemoryReviewItemRepository implements ReviewItemRepository {

    private final Map<UUID, ReviewItem> store = new LinkedHashMap<>();

    @Override
    public ReviewItem save(ReviewItem item) {
        store.put(item.id().value(), item);
        return item;
    }

    @Override
    public Optional<ReviewItem> findById(ReviewItemId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public List<ReviewItem> findDueByUserId(UUID userId, Instant now, int limit) {
        return store.values().stream()
                .filter(item -> item.userId().equals(userId))
                .filter(item -> item.isDue(now))
                .sorted(Comparator.comparing(ReviewItem::nextReviewAt))
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<ReviewItem> findByUserIdSourceTypeAndSourceId(UUID userId, ReviewSourceType sourceType, UUID sourceId) {
        return store.values().stream()
                .filter(item -> item.userId().equals(userId))
                .filter(item -> item.sourceType() == sourceType)
                .filter(item -> item.sourceId().equals(sourceId))
                .findFirst();
    }

    @Override
    public int countByUserId(UUID userId) {
        return (int) store.values().stream()
                .filter(item -> item.userId().equals(userId))
                .count();
    }

    @Override
    public int countDueByUserId(UUID userId, Instant now) {
        return (int) store.values().stream()
                .filter(item -> item.userId().equals(userId))
                .filter(item -> item.isDue(now))
                .count();
    }

    public List<ReviewItem> findAll(UUID userId) {
        return store.values().stream()
                .filter(item -> item.userId().equals(userId))
                .toList();
    }
}
