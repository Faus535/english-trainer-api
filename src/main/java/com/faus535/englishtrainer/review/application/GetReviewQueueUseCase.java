package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@UseCase
public class GetReviewQueueUseCase {

    private static final int MAX_LIMIT = 50;

    private final ReviewItemRepository repository;

    public GetReviewQueueUseCase(ReviewItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ReviewItem> execute(UUID userId, int limit) {
        int cappedLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        return repository.findDueByUserId(userId, LocalDate.now(java.time.ZoneOffset.UTC), cappedLimit);
    }
}
