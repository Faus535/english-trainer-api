package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.*;
import com.faus535.englishtrainer.article.infrastructure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmitAnswerUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private InMemoryArticleQuestionAnswerRepository answerRepository;
    private SubmitAnswerUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        answerRepository = new InMemoryArticleQuestionAnswerRepository();
        useCase = new SubmitAnswerUseCase(articleReadingRepository, questionRepository,
                answerRepository, new StubArticleAiPort());
    }

    @Test
    void happyPathReturnsGradedAnswer() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        ArticleQuestionAnswer result = useCase.execute(userId, article.id(), question.id(),
                ArticleQuestionAnswerMother.validAnswer());

        assertNotNull(result.id());
        assertTrue(result.isContentCorrect());
        assertEquals(1, answerRepository.count());
    }

    @Test
    void throwsAnswerTooShortExceptionBeforeCallingClaude() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        assertThrows(AnswerTooShortException.class,
                () -> useCase.execute(userId, article.id(), question.id(),
                        ArticleQuestionAnswerMother.tooShortAnswer()));
        assertEquals(0, answerRepository.count());
    }

    @Test
    void throwsArticleQuestionNotFoundWhenQuestionNotInArticle() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestionId unknownQuestionId = ArticleQuestionId.generate();

        assertThrows(ArticleQuestionNotFoundException.class,
                () -> useCase.execute(userId, article.id(), unknownQuestionId,
                        ArticleQuestionAnswerMother.validAnswer()));
    }

    @Test
    void throwsQuestionAlreadyAnsweredExceptionOnDuplicate() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        useCase.execute(userId, article.id(), question.id(), ArticleQuestionAnswerMother.validAnswer());

        assertThrows(QuestionAlreadyAnsweredException.class,
                () -> useCase.execute(userId, article.id(), question.id(),
                        ArticleQuestionAnswerMother.validAnswer()));
    }

    @Test
    void throwsArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);
        ArticleQuestion question = ArticleQuestionMother.withHint(article.id());
        questionRepository.save(question);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id(), question.id(),
                        ArticleQuestionAnswerMother.validAnswer()));
    }
}
