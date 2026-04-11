package com.faus535.englishtrainer.talk.domain;

import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TalkConversationTest {

    @Test
    void startCreatesActiveConversation() {
        TalkConversation conversation = TalkConversation.start(
                UUID.randomUUID(), UUID.randomUUID(), new TalkLevel("b1"), ConversationMode.FULL);

        assertEquals(TalkStatus.ACTIVE, conversation.status());
        assertNotNull(conversation.id());
        assertNotNull(conversation.startedAt());
        assertNull(conversation.endedAt());
        assertTrue(conversation.messages().isEmpty());
    }

    @Test
    void addMessageAddsToList() throws TalkConversationAlreadyEndedException {
        TalkConversation conversation = TalkConversationMother.active();
        TalkMessage message = TalkMessage.userMessage("Hello");

        TalkConversation updated = conversation.addMessage(message);

        assertEquals(1, updated.messages().size());
        assertEquals("Hello", updated.messages().getFirst().content());
    }

    @Test
    void addMessageToCompletedThrows() {
        TalkConversation conversation = TalkConversationMother.completed();

        assertThrows(TalkConversationAlreadyEndedException.class,
                () -> conversation.addMessage(TalkMessage.userMessage("Hello")));
    }

    @Test
    void endSetsCompletedAndRegistersEvent() throws TalkConversationAlreadyEndedException {
        TalkConversation conversation = TalkConversationMother.withMessages(4);
        TalkEvaluation evaluation = new TalkEvaluation(80, 70, 75, 85, 78, "b1",
                List.of("Good"), List.of("More practice"));

        TalkConversation ended = conversation.end("Good session", evaluation);

        assertEquals(TalkStatus.COMPLETED, ended.status());
        assertNotNull(ended.endedAt());
        assertEquals("Good session", ended.summary());
        assertFalse(ended.pullDomainEvents().isEmpty());
    }

    @Test
    void endCompletedConversationThrows() {
        TalkConversation conversation = TalkConversationMother.completed();

        assertThrows(TalkConversationAlreadyEndedException.class,
                () -> conversation.end("Summary", TalkEvaluation.empty()));
    }

    @Test
    void recentMessagesLimitsCorrectly() {
        TalkConversation conversation = TalkConversationMother.withMessages(10);

        List<TalkMessage> recent = conversation.recentMessages(4);

        assertEquals(4, recent.size());
    }

    @Test
    void recentMessagesReturnsAllWhenFewer() {
        TalkConversation conversation = TalkConversationMother.withMessages(2);

        List<TalkMessage> recent = conversation.recentMessages(10);

        assertEquals(2, recent.size());
    }

    @Test
    void errorCountCountsCorrections() {
        TalkConversation conversation = TalkConversationMother.withCorrections(3);

        assertEquals(3, conversation.errorCount());
    }

    @Test
    void isAtMaxTurnsDetectsLimit() {
        TalkConversation conversation = TalkConversationMother.withMessages(30);

        assertTrue(conversation.isAtMaxTurns());
    }

    @Test
    void isAtQuickLimit_returnsFalse_whenFullMode() {
        TalkConversation conversation = TalkConversationMother.quickModeWithUserMessages(3);
        TalkConversation fullMode = TalkConversation.reconstitute(
                conversation.id(), conversation.userId(), conversation.scenarioId(),
                conversation.level(), ConversationMode.FULL, conversation.status(),
                conversation.summary(), conversation.evaluation(),
                null, null,
                conversation.startedAt(), conversation.endedAt(), conversation.messages());

        assertFalse(fullMode.isAtQuickLimit());
    }

    @Test
    void isAtQuickLimit_returnsFalse_whenQuickModeUnder3UserMessages() {
        TalkConversation conversation = TalkConversationMother.quickModeWithUserMessages(2);

        assertFalse(conversation.isAtQuickLimit());
    }

    @Test
    void isAtQuickLimit_returnsTrue_whenQuickModeWith3UserMessages() {
        TalkConversation conversation = TalkConversationMother.quickModeWithUserMessages(3);

        assertTrue(conversation.isAtQuickLimit());
    }

    @Test
    void startConversation_hasNoGrammarFeedback() {
        TalkConversation conversation = TalkConversation.start(
                UUID.randomUUID(), UUID.randomUUID(), new TalkLevel("b1"), ConversationMode.FULL);

        assertFalse(conversation.hasGrammarFeedback());
        assertNull(conversation.grammarNotes());
        assertNull(conversation.newVocabulary());
    }

    @Test
    void withGrammarFeedback_returnsNewInstanceWithFeedback() {
        TalkConversation conversation = TalkConversationMother.completed();
        List<GrammarNote> notes = List.of(
                new GrammarNote("I goed", "I went", "Irregular verb"));
        List<VocabItem> vocab = List.of(
                new VocabItem("negotiate", "To reach an agreement", "They negotiated."));

        TalkConversation updated = conversation.withGrammarFeedback(notes, vocab);

        assertTrue(updated.hasGrammarFeedback());
        assertEquals(1, updated.grammarNotes().size());
        assertEquals("I goed", updated.grammarNotes().getFirst().originalText());
        assertEquals(1, updated.newVocabulary().size());
        assertEquals("negotiate", updated.newVocabulary().getFirst().word());
    }

    @Test
    void withGrammarFeedback_returnsUnmodifiableLists() {
        TalkConversation conversation = TalkConversationMother.completed();
        List<GrammarNote> notes = List.of(new GrammarNote("I goed", "I went", "Irregular"));
        List<VocabItem> vocab = List.of(new VocabItem("negotiate", "To agree", "They negotiated."));

        TalkConversation updated = conversation.withGrammarFeedback(notes, vocab);

        assertThrows(UnsupportedOperationException.class,
                () -> updated.grammarNotes().add(new GrammarNote("x", "y", "z")));
        assertThrows(UnsupportedOperationException.class,
                () -> updated.newVocabulary().add(new VocabItem("a", "b", "c")));
    }

    @Test
    void completedWithGrammarFeedback_hasGrammarFeedback() {
        TalkConversation conversation = TalkConversationMother.completedWithGrammarFeedback();

        assertTrue(conversation.hasGrammarFeedback());
        assertFalse(conversation.grammarNotes().isEmpty());
        assertFalse(conversation.newVocabulary().isEmpty());
    }

    @Test
    void end_pairsCorrectionsWithPrecedingUserMessages() throws TalkConversationAlreadyEndedException {
        TalkConversation conversation = TalkConversationMother.withCorrections(2);

        TalkConversation ended = conversation.end("Good session", TalkEvaluation.empty());

        var event = (com.faus535.englishtrainer.talk.domain.event.TalkConversationCompletedEvent)
                ended.pullDomainEvents().get(0);
        assertEquals(2, event.corrections().size());
        assertEquals("Test message 0", event.corrections().get(0).originalUserMessage());
        assertEquals("Test message 1", event.corrections().get(1).originalUserMessage());
    }
}
