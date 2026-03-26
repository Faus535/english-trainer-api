package com.faus535.englishtrainer.learningpath.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ContentSelector {

    private static final double NEW_CONTENT_RATIO = 0.7;

    public record ContentSelection(List<UUID> newContentIds, List<UUID> reviewContentIds) {

        public ContentSelection {
            newContentIds = List.copyOf(newContentIds);
            reviewContentIds = List.copyOf(reviewContentIds);
        }

        public List<UUID> allIds() {
            List<UUID> all = new ArrayList<>(newContentIds.size() + reviewContentIds.size());
            all.addAll(newContentIds);
            all.addAll(reviewContentIds);
            return List.copyOf(all);
        }

        public boolean isEmpty() {
            return newContentIds.isEmpty() && reviewContentIds.isEmpty();
        }
    }

    public ContentSelection select(List<UnitContent> unpracticedContents,
                                    List<UUID> dueReviewContentIds,
                                    Set<UUID> recentlyPracticedIds,
                                    int requestedCount) {
        if (requestedCount <= 0) {
            return new ContentSelection(List.of(), List.of());
        }

        List<UUID> availableNewIds = unpracticedContents.stream()
                .map(UnitContent::contentId)
                .filter(id -> !recentlyPracticedIds.contains(id))
                .collect(Collectors.toCollection(ArrayList::new));

        List<UUID> availableReviewIds = dueReviewContentIds.stream()
                .filter(id -> !recentlyPracticedIds.contains(id))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(availableNewIds);
        Collections.shuffle(availableReviewIds);

        if (availableNewIds.isEmpty() && availableReviewIds.isEmpty()) {
            return new ContentSelection(List.of(), List.of());
        }

        if (availableNewIds.isEmpty()) {
            List<UUID> reviewIds = availableReviewIds.stream()
                    .limit(requestedCount)
                    .toList();
            return new ContentSelection(List.of(), reviewIds);
        }

        if (availableReviewIds.isEmpty()) {
            List<UUID> newIds = availableNewIds.stream()
                    .limit(requestedCount)
                    .toList();
            return new ContentSelection(newIds, List.of());
        }

        int newCount = (int) Math.ceil(requestedCount * NEW_CONTENT_RATIO);
        int reviewCount = requestedCount - newCount;

        if (newCount > availableNewIds.size()) {
            newCount = availableNewIds.size();
            reviewCount = Math.min(requestedCount - newCount, availableReviewIds.size());
        } else if (reviewCount > availableReviewIds.size()) {
            reviewCount = availableReviewIds.size();
            newCount = Math.min(requestedCount - reviewCount, availableNewIds.size());
        }

        List<UUID> selectedNew = availableNewIds.stream().limit(newCount).toList();
        List<UUID> selectedReview = availableReviewIds.stream().limit(reviewCount).toList();

        return new ContentSelection(selectedNew, selectedReview);
    }

    public static Set<UUID> findRecentlyPracticed(List<UnitContent> contents, Instant cutoff) {
        return contents.stream()
                .filter(c -> c.practiced() && c.lastPracticedAt() != null && c.lastPracticedAt().isAfter(cutoff))
                .map(UnitContent::contentId)
                .collect(Collectors.toSet());
    }
}
