package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingMother;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleCannotBeDeletedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteArticleUseCaseTest {

    private InMemoryArticleReadingRepository repository;
    private DeleteArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleReadingRepository();
        useCase = new DeleteArticleUseCase(repository);
    }

    @Test
    void shouldDeleteInProgressArticle() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        repository.save(article);

        useCase.execute(userId, article.id());

        assertTrue(repository.findById(article.id()).isEmpty());
        assertEquals(0, repository.count());
    }

    @Test
    void shouldThrowArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId unknownId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class,
                () -> useCase.execute(userId, unknownId));
    }

    @Test
    void shouldThrowArticleAccessDeniedForDifferentUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        repository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id()));
        assertEquals(1, repository.count());
    }

    @Test
    void shouldThrowArticleCannotBeDeletedWhenCompleted() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.completed(userId);
        repository.save(article);

        assertThrows(ArticleCannotBeDeletedException.class,
                () -> useCase.execute(userId, article.id()));
        assertEquals(1, repository.count());
    }
}
