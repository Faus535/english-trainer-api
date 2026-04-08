package com.faus535.englishtrainer.review.application;

import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateReviewItemFromArticleUseCaseTest {

    private InMemoryReviewItemRepository reviewItemRepository;
    private CreateReviewItemFromArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        reviewItemRepository = new InMemoryReviewItemRepository();
        useCase = new CreateReviewItemFromArticleUseCase(reviewItemRepository);
    }

    @Test
    void shouldCreateReviewItemFromEvent() {
        UUID userId = UUID.randomUUID();
        UUID markedWordId = UUID.randomUUID();
        ArticleWordMarkedEvent event = new ArticleWordMarkedEvent(
                UUID.randomUUID(), userId, markedWordId,
                "spark debate", "traducción", "The policies spark debate.");

        useCase.execute(event);

        var item = reviewItemRepository.findByUserIdSourceTypeAndSourceId(
                userId, ReviewSourceType.ARTICLE, markedWordId);
        assertTrue(item.isPresent());
        assertEquals("spark debate", item.get().frontContent());
        assertEquals("traducción", item.get().backContent());
    }

    @Test
    void shouldNotCreateDuplicateReviewItem() {
        UUID userId = UUID.randomUUID();
        UUID markedWordId = UUID.randomUUID();
        ArticleWordMarkedEvent event = new ArticleWordMarkedEvent(
                UUID.randomUUID(), userId, markedWordId,
                "spark debate", "traducción", "Context.");

        useCase.execute(event);
        useCase.execute(event);

        assertEquals(1, reviewItemRepository.countByUserId(userId));
    }
}
