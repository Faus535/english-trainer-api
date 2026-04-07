package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetArticleMarkedWordsUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private GetArticleMarkedWordsUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        useCase = new GetArticleMarkedWordsUseCase(articleReadingRepository, markedWordRepository);
    }

    @Test
    void returnsMarkedWordsForOwningUser() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "spark debate"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "policy makers"));

        List<ArticleMarkedWord> result = useCase.execute(userId, article.id());

        assertEquals(2, result.size());
    }

    @Test
    void throwsArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId unknownId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class, () -> useCase.execute(userId, unknownId));
    }

    @Test
    void throwsArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class, () -> useCase.execute(otherUserId, article.id()));
    }
}
