package com.faus535.englishtrainer.article.domain;

import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.event.ArticleReadingCompletedEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArticleReadingTest {

    @Test
    void createInitializesPendingWithEmptyParagraphs() {
        UUID userId = UUID.randomUUID();
        ArticleReading reading = ArticleReading.create(userId, new ArticleTopic("Climate"), ArticleLevel.B2);

        assertEquals(ArticleStatus.PENDING, reading.status());
        assertEquals(userId, reading.userId());
        assertEquals("Climate", reading.topic().value());
        assertEquals(ArticleLevel.B2, reading.level());
        assertTrue(reading.paragraphs().isEmpty());
        assertEquals(0, reading.xpEarned());
        assertNotNull(reading.id());
        assertNotNull(reading.createdAt());
    }

    @Test
    void completeTransitionsToCompletedAndRegistersEvent() throws ArticleAlreadyCompletedException {
        ArticleReading reading = ArticleReadingMother.inProgress();
        ArticleReading completed = reading.complete(41);

        assertEquals(ArticleStatus.COMPLETED, completed.status());
        assertEquals(41, completed.xpEarned());
        var events = completed.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(ArticleReadingCompletedEvent.class, events.get(0));
        ArticleReadingCompletedEvent event = (ArticleReadingCompletedEvent) events.get(0);
        assertEquals(reading.id().value(), event.articleReadingId());
        assertEquals(reading.userId(), event.userId());
        assertEquals(41, event.xpEarned());
    }

    @Test
    void completeAgainThrowsArticleAlreadyCompletedException() throws ArticleAlreadyCompletedException {
        ArticleReading reading = ArticleReadingMother.inProgress();
        ArticleReading completed = reading.complete(25);

        assertThrows(ArticleAlreadyCompletedException.class, () -> completed.complete(25));
    }

    @Test
    void withTitleAndParagraphsReturnsNewInstanceWithUpdatedFields() {
        ArticleReading reading = ArticleReading.create(UUID.randomUUID(), new ArticleTopic("Tech"), ArticleLevel.B1);
        var paragraphs = java.util.List.of(
                ArticleParagraph.create(reading.id(), "AI paragraph content here.", 0, ArticleSpeaker.AI),
                ArticleParagraph.create(reading.id(), "USER paragraph content here.", 1, ArticleSpeaker.USER)
        );

        ArticleReading updated = reading.withTitleAndParagraphs("Tech Advances", paragraphs);

        assertEquals("Tech Advances", updated.title());
        assertEquals(2, updated.paragraphs().size());
        assertEquals(ArticleStatus.PENDING, updated.status());
    }
}
