package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.ArticleQuestion;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateQuestionsUseCaseTest {

    private InMemoryArticleQuestionRepository questionRepository;
    private GenerateQuestionsUseCase useCase;

    @BeforeEach
    void setUp() {
        questionRepository = new InMemoryArticleQuestionRepository();
        useCase = new GenerateQuestionsUseCase(new StubArticleAiPort(), questionRepository);
    }

    @Test
    void happyPathSavesQuestionsWithHints() throws ArticleAiException {
        ArticleReadingId articleId = ArticleReadingId.generate();

        List<ArticleQuestion> questions = useCase.execute(articleId, "Article text here.", "B2");

        assertEquals(2, questions.size());
        assertEquals(2, questionRepository.count());
        assertNotNull(questions.get(0).hintText());
        assertFalse(questions.get(0).hintText().isBlank());
    }

    @Test
    void aiFailurePropagatesAsArticleAiException() {
        ArticleReadingId articleId = ArticleReadingId.generate();
        useCase = new GenerateQuestionsUseCase(new FailingStubArticleAiPort(), questionRepository);

        assertThrows(ArticleAiException.class, () -> useCase.execute(articleId, "text", "B1"));
        assertEquals(0, questionRepository.count());
    }
}
