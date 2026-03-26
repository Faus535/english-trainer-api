package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.learningpath.domain.ContentSelector.ContentSelection;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContentSelectorTest {

    private final ContentSelector selector = new ContentSelector();

    @Test
    void should_split_70_30_when_sufficient_content() {
        List<UnitContent> unpracticed = createUnpracticedContents(7);
        List<UUID> reviewIds = createReviewIds(3);
        int requestedCount = 10;

        ContentSelection result = selector.select(unpracticed, reviewIds, Set.of(), requestedCount);

        assertEquals(7, result.newContentIds().size());
        assertEquals(3, result.reviewContentIds().size());
        assertEquals(10, result.allIds().size());
    }

    @Test
    void should_return_100_percent_review_when_no_new_content() {
        List<UnitContent> unpracticed = List.of();
        List<UUID> reviewIds = createReviewIds(5);
        int requestedCount = 5;

        ContentSelection result = selector.select(unpracticed, reviewIds, Set.of(), requestedCount);

        assertTrue(result.newContentIds().isEmpty());
        assertEquals(5, result.reviewContentIds().size());
        assertFalse(result.isEmpty());
    }

    @Test
    void should_return_100_percent_new_when_no_review_items() {
        List<UnitContent> unpracticed = createUnpracticedContents(5);
        List<UUID> reviewIds = List.of();
        int requestedCount = 5;

        ContentSelection result = selector.select(unpracticed, reviewIds, Set.of(), requestedCount);

        assertEquals(5, result.newContentIds().size());
        assertTrue(result.reviewContentIds().isEmpty());
        assertFalse(result.isEmpty());
    }

    @Test
    void should_exclude_recently_practiced_ids() {
        UUID recentId1 = UUID.randomUUID();
        UUID recentId2 = UUID.randomUUID();
        UUID freshId = UUID.randomUUID();

        List<UnitContent> unpracticed = List.of(
                new UnitContent(ContentType.VOCAB, recentId1, false, null),
                new UnitContent(ContentType.VOCAB, freshId, false, null)
        );
        List<UUID> reviewIds = List.of(recentId2, UUID.randomUUID());
        Set<UUID> recentlyPracticed = Set.of(recentId1, recentId2);

        ContentSelection result = selector.select(unpracticed, reviewIds, recentlyPracticed, 10);

        assertFalse(result.allIds().contains(recentId1));
        assertFalse(result.allIds().contains(recentId2));
        assertTrue(result.allIds().contains(freshId));
    }

    @Test
    void should_return_empty_when_both_lists_empty() {
        ContentSelection result = selector.select(List.of(), List.of(), Set.of(), 10);

        assertTrue(result.isEmpty());
        assertTrue(result.allIds().isEmpty());
    }

    @Test
    void should_return_empty_when_requested_count_is_zero() {
        List<UnitContent> unpracticed = createUnpracticedContents(5);

        ContentSelection result = selector.select(unpracticed, List.of(), Set.of(), 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void should_adjust_split_when_insufficient_new_content() {
        List<UnitContent> unpracticed = createUnpracticedContents(2);
        List<UUID> reviewIds = createReviewIds(10);
        int requestedCount = 10;

        ContentSelection result = selector.select(unpracticed, reviewIds, Set.of(), requestedCount);

        assertEquals(2, result.newContentIds().size());
        assertEquals(8, result.reviewContentIds().size());
        assertEquals(10, result.allIds().size());
    }

    @Test
    void should_adjust_split_when_insufficient_review_content() {
        List<UnitContent> unpracticed = createUnpracticedContents(10);
        List<UUID> reviewIds = createReviewIds(1);
        int requestedCount = 10;

        ContentSelection result = selector.select(unpracticed, reviewIds, Set.of(), requestedCount);

        assertEquals(9, result.newContentIds().size());
        assertEquals(1, result.reviewContentIds().size());
        assertEquals(10, result.allIds().size());
    }

    @Test
    void findRecentlyPracticed_should_return_ids_within_cutoff() {
        Instant now = Instant.now();
        Instant twelvHoursAgo = now.minus(12, ChronoUnit.HOURS);
        Instant thirtyHoursAgo = now.minus(30, ChronoUnit.HOURS);
        Instant cutoff = now.minus(24, ChronoUnit.HOURS);

        UUID recentId = UUID.randomUUID();
        UUID oldId = UUID.randomUUID();
        UUID unpracticedId = UUID.randomUUID();

        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, recentId, true, twelvHoursAgo),
                new UnitContent(ContentType.VOCAB, oldId, true, thirtyHoursAgo),
                new UnitContent(ContentType.VOCAB, unpracticedId, false, null)
        );

        Set<UUID> result = ContentSelector.findRecentlyPracticed(contents, cutoff);

        assertTrue(result.contains(recentId));
        assertFalse(result.contains(oldId));
        assertFalse(result.contains(unpracticedId));
    }

    private List<UnitContent> createUnpracticedContents(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null))
                .toList();
    }

    private List<UUID> createReviewIds(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> UUID.randomUUID())
                .toList();
    }
}
