package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import com.faus535.englishtrainer.review.domain.ReviewSourceType;
import com.faus535.englishtrainer.review.infrastructure.InMemoryReviewItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MarkWordUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private InMemoryReviewItemRepository reviewItemRepository;
    private MarkWordUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        reviewItemRepository = new InMemoryReviewItemRepository();
        useCase = new MarkWordUseCase(articleReadingRepository, markedWordRepository,
                new StubArticleAiPort(), reviewItemRepository);
    }

    @Test
    void happyPathCreatesMarkedWordAndReviewItemWithArticleSourceType() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        ArticleMarkedWord result = useCase.execute(userId, article.id(), "spark debate", "The policies spark debate.");

        assertNotNull(result.id());
        assertEquals("spark debate", result.wordOrPhrase());
        assertEquals("traducción de prueba", result.translation());
        assertEquals(1, markedWordRepository.count());

        var reviewItems = reviewItemRepository.findDueByUserId(userId, java.time.Instant.now().plusSeconds(1), 10);
        assertEquals(1, reviewItems.size());
        assertEquals(ReviewSourceType.ARTICLE, reviewItems.get(0).sourceType());
        assertEquals(result.id().value(), reviewItems.get(0).sourceId());
        assertEquals("spark debate", reviewItems.get(0).frontContent());
    }

    @Test
    void throwsArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId unknownId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class,
                () -> useCase.execute(userId, unknownId, "word", null));
    }

    @Test
    void throwsArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id(), "word", null));
    }

    @Test
    void throwsDuplicateMarkedWordExceptionForSameWord() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id(), "spark debate", "Context.");

        assertThrows(DuplicateMarkedWordException.class,
                () -> useCase.execute(userId, article.id(), "spark debate", "Other context."));
    }

    @Test
    void throwsArticleAiExceptionWhenAiFails() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        useCase = new MarkWordUseCase(articleReadingRepository, markedWordRepository,
                new FailingStubArticleAiPort(), reviewItemRepository);

        assertThrows(ArticleAiException.class,
                () -> useCase.execute(userId, article.id(), "word", null));
        assertEquals(0, markedWordRepository.count());
    }
}
