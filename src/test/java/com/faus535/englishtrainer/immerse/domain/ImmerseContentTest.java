package com.faus535.englishtrainer.immerse.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImmerseContentTest {

    @Test
    void submitCreatesPendingContent() {
        ImmerseContent content = ImmerseContent.submit(UUID.randomUUID(), null, "Test", "Some text");

        assertEquals(ImmerseContentStatus.PENDING, content.status());
        assertNull(content.processedText());
        assertTrue(content.extractedVocabulary().isEmpty());
    }

    @Test
    void markProcessedSetsStatusAndVocabulary() {
        ImmerseContent content = ImmerseContentMother.pending();
        List<VocabularyItem> vocab = List.of(
                new VocabularyItem("fox", "An animal", "The fox ran.", "a2"));

        ImmerseContent processed = content.markProcessed("Annotated text", "b1", vocab);

        assertEquals(ImmerseContentStatus.PROCESSED, processed.status());
        assertEquals("Annotated text", processed.processedText());
        assertEquals("b1", processed.cefrLevel());
        assertEquals(1, processed.extractedVocabulary().size());
    }

    @Test
    void markFailedSetsFailedStatus() {
        ImmerseContent content = ImmerseContentMother.pending();

        ImmerseContent failed = content.markFailed();

        assertEquals(ImmerseContentStatus.FAILED, failed.status());
    }

    @Test
    void generateCreatesProcessedContentWithContentType() {
        ImmerseContent content = ImmerseContent.generate(
                UUID.randomUUID(), ContentType.AUDIO, "Podcast Episode",
                "Host: Welcome!", "Host: Welcome!", "b1",
                List.of(new VocabularyItem("welcome", "A greeting", "Welcome to the show.", "a1")));

        assertEquals(ImmerseContentStatus.PROCESSED, content.status());
        assertEquals(ContentType.AUDIO, content.contentType());
        assertEquals("Podcast Episode", content.title());
        assertEquals(1, content.extractedVocabulary().size());
        assertNull(content.sourceUrl());
    }

    @Test
    void submitDefaultsToTextContentType() {
        ImmerseContent content = ImmerseContent.submit(UUID.randomUUID(), null, "Test", "Some text");

        assertEquals(ContentType.TEXT, content.contentType());
    }
}
