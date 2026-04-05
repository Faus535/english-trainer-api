package com.faus535.englishtrainer.talk.domain;

import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TalkConversationTest {

    @Test
    void startCreatesActiveConversation() {
        TalkConversation conversation = TalkConversation.start(
                UUID.randomUUID(), UUID.randomUUID(), new TalkLevel("b1"));

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
}
