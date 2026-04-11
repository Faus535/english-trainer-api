package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.immerse.domain.event.ImmerseExerciseAnsweredEvent;
import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CreateReviewItemFromImmerseUseCase {

    private final ReviewItemRepository repository;

    public CreateReviewItemFromImmerseUseCase(ReviewItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(ImmerseExerciseAnsweredEvent event) {
        repository.findByUserIdSourceTypeAndSourceId(
                        event.userId(), ReviewSourceType.IMMERSE_VOCAB, event.exerciseId().value())
                .ifPresentOrElse(
                        existing -> {},
                        () -> repository.save(ReviewItem.create(
                                event.userId(), ReviewSourceType.IMMERSE_VOCAB,
                                event.exerciseId().value(), event.question(), event.correctAnswer(),
                                event.question(), null, event.correctAnswer(), null))
                );
    }
}
