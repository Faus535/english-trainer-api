package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateArticleProgressUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private UpdateArticleProgressUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        useCase = new UpdateArticleProgressUseCase(articleReadingRepository);
    }

    @Test
    void shouldUpdateProgressWhenArticleExistsAndUserIsOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id(), 3, 1);

        ArticleReading updated = articleReadingRepository.findById(article.id()).orElseThrow();
        assertEquals(3, updated.currentParagraphIndex());
        assertEquals(1, updated.currentQuestionIndex());
    }

    @Test
    void shouldThrowArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId nonExistentId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class,
                () -> useCase.execute(userId, nonExistentId, 0, 0));
    }

    @Test
    void shouldThrowArticleAccessDeniedWhenUserIsNotOwner() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id(), 0, 0));
    }

    @Test
    void shouldSilentlyIgnoreProgressUpdateForCompletedArticle() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.completed(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id(), 5, 2);

        ArticleReading unchanged = articleReadingRepository.findById(article.id()).orElseThrow();
        assertEquals(0, unchanged.currentParagraphIndex());
        assertEquals(0, unchanged.currentQuestionIndex());
    }
}
