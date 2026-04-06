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

        ImmerseContent processed = content.markProcessed("Test Article", "The quick brown fox.", "Annotated text", "b1", vocab);

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
    void generateCreatesPendingContent() {
        ImmerseContent content = ImmerseContent.generate(
                UUID.randomUUID(), ContentType.TEXT, "b1", "city life");

        assertEquals(ImmerseContentStatus.PENDING, content.status());
        assertEquals(ContentType.TEXT, content.contentType());
        assertEquals("TEXT content", content.title());
        assertNull(content.rawText());
        assertNull(content.processedText());
        assertTrue(content.extractedVocabulary().isEmpty());
        assertNull(content.sourceUrl());
    }

    @Test
    void markProcessedUpdatesAllFields() {
        ImmerseContent content = ImmerseContent.generate(
                UUID.randomUUID(), ContentType.TEXT, "b1", "city life");
        List<VocabularyItem> vocab = List.of(
                new VocabularyItem("rush", "To move quickly", "People rush to work.", "b1"));

        ImmerseContent processed = content.markProcessed(
                "A Day in the City", "The city wakes up early.",
                "The city wakes up early.", "b1", vocab);

        assertEquals(ImmerseContentStatus.PROCESSED, processed.status());
        assertEquals("A Day in the City", processed.title());
        assertEquals("The city wakes up early.", processed.rawText());
        assertEquals("The city wakes up early.", processed.processedText());
        assertEquals("b1", processed.cefrLevel());
        assertEquals(1, processed.extractedVocabulary().size());
    }

    @Test
    void submitDefaultsToTextContentType() {
        ImmerseContent content = ImmerseContent.submit(UUID.randomUUID(), null, "Test", "Some text");

        assertEquals(ContentType.TEXT, content.contentType());
    }
}
