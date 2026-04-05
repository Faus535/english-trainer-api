package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.review.domain.*;
import com.faus535.englishtrainer.review.domain.error.ReviewItemNotFoundException;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class SubmitReviewResultUseCase {

    private final ReviewItemRepository itemRepository;
    private final ReviewResultRepository resultRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SubmitReviewResultUseCase(ReviewItemRepository itemRepository,
                                      ReviewResultRepository resultRepository,
                                      ApplicationEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.resultRepository = resultRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ReviewItem execute(UUID userId, UUID itemIdValue, int quality) throws ReviewItemNotFoundException {
        ReviewItemId itemId = new ReviewItemId(itemIdValue);
        ReviewItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ReviewItemNotFoundException(itemId));

        ReviewItem updated = item.review(quality);
        ReviewItem saved = itemRepository.save(updated);

        ReviewResult result = ReviewResult.create(itemId, userId, quality);
        resultRepository.save(result);

        updated.pullDomainEvents().forEach(eventPublisher::publishEvent);

        return saved;
    }
}
