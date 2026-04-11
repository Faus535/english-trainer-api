package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import com.faus535.englishtrainer.review.domain.ReviewItem;
import com.faus535.englishtrainer.review.domain.ReviewItemRepository;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

@UseCase
public class CreateReviewItemFromArticleUseCase {

    private final ReviewItemRepository repository;

    public CreateReviewItemFromArticleUseCase(ReviewItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(ArticleWordMarkedEvent event) {
        repository.findByUserIdSourceTypeAndSourceId(
                        event.userId(), ReviewSourceType.ARTICLE, event.markedWordId())
                .ifPresentOrElse(
                        existing -> {},
                        () -> repository.save(ReviewItem.create(
                                event.userId(), ReviewSourceType.ARTICLE,
                                event.markedWordId(), event.wordOrPhrase(), event.translation(),
                                event.contextSentence(), null, event.wordOrPhrase(), event.translation()))
                );
    }
}
