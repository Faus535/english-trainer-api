package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleQuestionRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompleteArticleUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleQuestionRepository questionRepository;
    private List<Object> publishedEvents;
    private CompleteArticleUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        questionRepository = new InMemoryArticleQuestionRepository();
        publishedEvents = new ArrayList<>();
        ApplicationEventPublisher publisher = publishedEvents::add;
        GenerateQuestionsUseCase generateQuestionsUseCase =
                new GenerateQuestionsUseCase(new StubArticleAiPort(), questionRepository);
        useCase = new CompleteArticleUseCase(articleReadingRepository, generateQuestionsUseCase, publisher);
    }

    @Test
    void happyPathStatusBecomesCompletedAndQuestionsGeneratedAndEventPublished() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        ArticleReading result = useCase.execute(userId, article.id());

        assertEquals(ArticleStatus.COMPLETED, result.status());
        assertEquals(2, questionRepository.count());
        assertEquals(1, publishedEvents.size());
        assertInstanceOf(ArticleReadingCompletedEvent.class, publishedEvents.get(0));
        ArticleReadingCompletedEvent event = (ArticleReadingCompletedEvent) publishedEvents.get(0);
        assertEquals(article.id().value(), event.articleReadingId());
        assertEquals(userId, event.userId());
    }

    @Test
    void callingCompleteTwiceThrowsArticleAlreadyCompletedException() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id());

        assertThrows(ArticleAlreadyCompletedException.class,
                () -> useCase.execute(userId, article.id()));
    }

    @Test
    void throwsArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id()));
    }
}
