package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleQuestionNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetQuestionHintUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private GetQuestionHintUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        useCase = new GetQuestionHintUseCase(articleReadingRepository, questionRepository);
    }

    @Test
    void returnsStoredHintTextWithoutCallingClaude() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        String hint = useCase.execute(userId, article.id(), question.id());

        assertEquals(question.hintText(), hint);
        assertFalse(hint.isBlank());
    }

    @Test
    void throwsArticleQuestionNotFoundWhenQuestionNotInArticle() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestionId unknownId = ArticleQuestionId.generate();

        assertThrows(ArticleQuestionNotFoundException.class,
                () -> useCase.execute(userId, article.id(), unknownId));
    }
}
