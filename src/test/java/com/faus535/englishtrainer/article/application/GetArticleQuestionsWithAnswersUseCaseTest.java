package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionAnswerRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetArticleQuestionsWithAnswersUseCaseTest {

    private InMemoryArticleReadingRepository readingRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private InMemoryArticleQuestionAnswerRepository answerRepository;
    private GetArticleQuestionsWithAnswersUseCase useCase;

    @BeforeEach
    void setUp() {
        readingRepository = new InMemoryArticleReadingRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        answerRepository = new InMemoryArticleQuestionAnswerRepository();
        useCase = new GetArticleQuestionsWithAnswersUseCase(readingRepository, questionRepository, answerRepository);
    }

    @Test
    void shouldReturnQuestionsWithAnswers() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        readingRepository.save(article);

        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);
        answerRepository.save(ArticleQuestionAnswerMother.valid(question.id()));

        List<QuestionWithAnswer> result = useCase.execute(userId, article.id());

        assertEquals(1, result.size());
        QuestionWithAnswer qwa = result.get(0);
        assertEquals(question.id().value(), qwa.questionId());
        assertEquals(question.questionText(), qwa.questionText());
        assertTrue(qwa.answered());
        assertNotNull(qwa.answer());
    }

    @Test
    void shouldReturnQuestionsWithAnsweredFalseWhenNoAnswers() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        readingRepository.save(article);

        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        List<QuestionWithAnswer> result = useCase.execute(userId, article.id());

        assertEquals(1, result.size());
        assertFalse(result.get(0).answered());
        assertNull(result.get(0).answer());
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
        readingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id()));
    }
}
