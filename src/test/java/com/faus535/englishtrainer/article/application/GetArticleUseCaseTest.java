package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.ArticleReadingMother;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetArticleUseCaseTest {

    private InMemoryArticleReadingRepository repository;
    private GetArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleReadingRepository();
        useCase = new GetArticleUseCase(repository);
    }

    @Test
    void returnsArticleForOwningUser() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading reading = ArticleReadingMother.inProgress(userId);
        repository.save(reading);

        ArticleReading result = useCase.execute(userId, reading.id());

        assertEquals(reading.id(), result.id());
    }

    @Test
    void throwsArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId unknownId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class, () -> useCase.execute(userId, unknownId));
    }

    @Test
    void throwsArticleAccessDeniedForDifferentUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading reading = ArticleReadingMother.inProgress(ownerUserId);
        repository.save(reading);

        assertThrows(ArticleAccessDeniedException.class, () -> useCase.execute(otherUserId, reading.id()));
    }
}
