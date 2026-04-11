package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewResultRepository;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@UseCase
public class GetReviewStatsUseCase {

    private final ReviewItemRepository itemRepository;
    private final ReviewResultRepository resultRepository;

    public GetReviewStatsUseCase(ReviewItemRepository itemRepository,
                                  ReviewResultRepository resultRepository) {
        this.itemRepository = itemRepository;
        this.resultRepository = resultRepository;
    }

    public ReviewStats execute(UUID userId) {
        int totalItems = itemRepository.countByUserId(userId);
        int dueToday = itemRepository.countDueByUserId(userId, Instant.now());

        Instant startOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
        int completedToday = resultRepository.countByUserIdAndReviewedAtAfter(userId, startOfDay);

        // Streak calculation: count consecutive days with at least one review
        // For now, return completedToday > 0 ? 1 : 0 as a simple streak
        // A proper streak would require querying review_results grouped by day
        int streak = completedToday > 0 ? 1 : 0;

        long totalMastered = itemRepository.countMasteredByUserId(userId);

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long weeklyReviewed = resultRepository.countByUserIdAndReviewedAtAfter(userId, sevenDaysAgo);

        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        long totalLast30 = resultRepository.countByUserIdAndReviewedAtAfter(userId, thirtyDaysAgo);
        long correctLast30 = resultRepository.countCorrectByUserIdSince(userId, thirtyDaysAgo);
        double accuracyRate = totalLast30 > 0 ? (double) correctLast30 / totalLast30 : 0.0;

        return new ReviewStats(totalItems, dueToday, completedToday, streak, totalMastered, weeklyReviewed, accuracyRate);
    }
}
