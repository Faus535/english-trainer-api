package com.faus535.englishtrainer.article.application;

import com.faus535.englishtrainer.article.domain.*;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import com.faus535.englishtrainer.article.domain.event.ArticleWordMarkedEvent;
import com.faus535.englishtrainer.article.infrastructure.FailingStubArticleAiPort;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleMarkedWordRepository;
import com.faus535.englishtrainer.article.infrastructure.InMemoryArticleReadingRepository;
import com.faus535.englishtrainer.article.infrastructure.StubArticleAiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MarkWordUseCaseTest {

    private InMemoryArticleReadingRepository articleReadingRepository;
    private InMemoryArticleMarkedWordRepository markedWordRepository;
    private List<Object> publishedEvents;
    private MarkWordUseCase useCase;

    @BeforeEach
    void setUp() {
        articleReadingRepository = new InMemoryArticleReadingRepository();
        markedWordRepository = new InMemoryArticleMarkedWordRepository();
        publishedEvents = new ArrayList<>();
        ApplicationEventPublisher publisher = publishedEvents::add;
        useCase = new MarkWordUseCase(articleReadingRepository, markedWordRepository,
                new StubArticleAiPort(), publisher);
    }

    @Test
    void happyPathCreatesMarkedWordAndPublishesEvent() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        ArticleMarkedWord result = useCase.execute(userId, article.id(), "spark debate", "The policies spark debate.");

        assertNotNull(result.id());
        assertEquals("spark debate", result.wordOrPhrase());
        assertEquals("traducción de prueba", result.translation());
        assertEquals(1, markedWordRepository.count());

        assertEquals(1, publishedEvents.size());
        assertInstanceOf(ArticleWordMarkedEvent.class, publishedEvents.get(0));
        ArticleWordMarkedEvent event = (ArticleWordMarkedEvent) publishedEvents.get(0);
        assertEquals(article.id().value(), event.articleReadingId());
        assertEquals(userId, event.userId());
        assertEquals(result.id().value(), event.markedWordId());
        assertEquals("spark debate", event.wordOrPhrase());
        assertEquals("traducción de prueba", event.translation());
    }

    @Test
    void throwsArticleNotFoundWhenArticleDoesNotExist() {
        UUID userId = UUID.randomUUID();
        ArticleReadingId unknownId = ArticleReadingId.generate();

        assertThrows(ArticleNotFoundException.class,
                () -> useCase.execute(userId, unknownId, "word", null));
    }

    @Test
    void throwsArticleAccessDeniedForWrongUser() {
        UUID ownerUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(ownerUserId);
        articleReadingRepository.save(article);

        assertThrows(ArticleAccessDeniedException.class,
                () -> useCase.execute(otherUserId, article.id(), "word", null));
    }

    @Test
    void throwsDuplicateMarkedWordExceptionForSameWord() throws Exception {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);

        useCase.execute(userId, article.id(), "spark debate", "Context.");

        assertThrows(DuplicateMarkedWordException.class,
                () -> useCase.execute(userId, article.id(), "spark debate", "Other context."));
    }

    @Test
    void throwsArticleAiExceptionWhenAiFails() {
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.inProgress(userId);
        articleReadingRepository.save(article);
        useCase = new MarkWordUseCase(articleReadingRepository, markedWordRepository,
                new FailingStubArticleAiPort(), publishedEvents::add);

        assertThrows(ArticleAiException.class,
                () -> useCase.execute(userId, article.id(), "word", null));
        assertEquals(0, markedWordRepository.count());
        assertTrue(publishedEvents.isEmpty());
    }
}
