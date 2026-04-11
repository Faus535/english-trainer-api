package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetPreReadingUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private GetPreReadingUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        useCase = new GetPreReadingUseCase(articleReadingRepository, new StubArticleAiPort());
    }

    @Test
    void shouldReturnPreReadingDataWhenArticleExistsAndUserIsOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        PreReadingData result = useCase.execute(userId, article.id());

        assertNotNull(result);
        assertFalse(result.keyWords().isEmpty());
        assertNotNull(result.predictiveQuestion());
        assertFalse(result.predictiveQuestion().isBlank());
    }

    @Test
    void shouldThrowArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId nonExistentId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class,
                () -> useCase.execute(userId, nonExistentId));
    }

    @Test
    void shouldThrowArticleAccessDeniedWhenUserIsNotOwner() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id()));
    }

    @Test
    void shouldThrowArticleAiExceptionWhenAiPortFails() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        GetPreReadingUseCase failingUseCase = new GetPreReadingUseCase(
                articleReadingRepository, new FailingStubArticleAiPort());

        assertThrows(ArticleAiException.class,
                () -> failingUseCase.execute(userId, article.id()));
    }
}
