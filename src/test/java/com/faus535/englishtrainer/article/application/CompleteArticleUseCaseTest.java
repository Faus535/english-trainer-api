package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionAnswerRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompleteArticleUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private InMemoryArticleQuestionAnswerRepository answerRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private List<Object> publishedEvents;
    private CompleteArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        answerRepository = new InMemoryArticleQuestionAnswerRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        publishedEvents = new ArrayList<>();
        ApplicationEventPublisher publisher = publishedEvents::add;
        useCase = new CompleteArticleUseCase(articleReadingRepository, markedWordRepository,
                answerRepository, questionRepository, publisher);
    }

    @Test
    void shouldCompleteArticleWithBaseXpAndPublishEvent() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        ArticleReading result = useCase.execute(userId, article.id());

        assertEquals(ArticleStatus.COMPLETED, result.status());
        assertEquals(25, result.xpEarned());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(ArticleReadingCompletedEvent.class, publishedEvents.get(0));
        ArticleReadingCompletedEvent event = (ArticleReadingCompletedEvent) publishedEvents.get(0);
        assertEquals(25, event.xpEarned());
    }

    @Test
    void shouldCalculateXpWithCorrectAnswersAndMarkedWords() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "debate"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "policy"));
        markedWordRepository.save(ArticleMarkedWordMother.withWord(article.id(), userId, "climate"));

        ArticleQuestion q1 = ArticleQuestionMother.withHint(article.id());
        ArticleQuestion q2 = ArticleQuestionMother.ordered(article.id(), 1);
        questionRepository.save(q1);
        questionRepository.save(q2);
        answerRepository.save(ArticleQuestionAnswerMother.valid(q1.id()));
        answerRepository.save(ArticleQuestionAnswerMother.withGrading(q2.id()));

        ArticleReading result = useCase.execute(userId, article.id());

        // XP = 25 base + 5*1 (1 correct) + 2*3 (3 words) = 36
        assertEquals(36, result.xpEarned());
    }

    @Test
    void shouldThrowArticleAlreadyCompletedOnSecondCall() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id());

        assertThrows(ArticleAlreadyCompletedException.class,
                () -> useCase.execute(userId, article.id()));
    }

    @Test
    void shouldThrowArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id()));
    }
}
