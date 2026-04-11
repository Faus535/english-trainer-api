package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewResultRepository;
import com.faus535.englishtrainer.review.domain.ReviewStats;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public ReviewStats execute(UUID userId) {
        int totalItems = itemRepository.countByUserId(userId);
        int dueToday = itemRepository.countDueByUserId(userId, LocalDate.now(ZoneOffset.UTC));

        Instant startOfDay = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        int completedToday = resultRepository.countByUserIdAndReviewedAtAfter(userId, startOfDay);

        int streak = completedToday > 0 ? 1 : 0;

        long totalMastered = itemRepository.countMasteredByUserId(userId);

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long weeklyReviewed = resultRepository.countByUserIdAndReviewedAtAfter(userId, sevenDaysAgo);

        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        long totalLast30 = resultRepository.countByUserIdAndReviewedAtAfter(userId, thirtyDaysAgo);
        long correctLast30 = resultRepository.countCorrectByUserIdSince(userId, thirtyDaysAgo);
        double retentionRate = totalLast30 > 0 ? (double) correctLast30 / totalLast30 : 0.0;

        double averageInterval = itemRepository.averageIntervalByUserId(userId);

        return new ReviewStats(totalItems, dueToday, completedToday, streak, totalMastered, weeklyReviewed,
                retentionRate, averageInterval);
    }
}
