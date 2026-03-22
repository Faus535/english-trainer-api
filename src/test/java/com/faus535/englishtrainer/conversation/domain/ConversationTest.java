package com.faus535.englishtrainer.conversation.domain;

import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    @Test
    void shouldCreateActiveConversation() {
        Conversation conversation = Conversation.start(UUID.randomUUID(), new ConversationLevel("b1"), "Travel");

        assertEquals(ConversationStatus.ACTIVE, conversation.status());
        assertNotNull(conversation.id());
        assertEquals("b1", conversation.level().value());
        assertEquals("Travel", conversation.topic());
        assertTrue(conversation.turns().isEmpty());
        assertNull(conversation.endedAt());
    }

    @Test
    void shouldAddTurnToActiveConversation() throws ConversationAlreadyEndedException {
        Conversation conversation = ConversationMother.active();
        ConversationTurn turn = ConversationTurn.userTurn("Hello!", 0.95f);

        Conversation updated = conversation.addTurn(turn);

        assertEquals(1, updated.turns().size());
        assertEquals("user", updated.turns().getFirst().role());
    }

    @Test
    void shouldNotAddTurnToCompletedConversation() {
        Conversation conversation = ConversationMother.completed();

        assertThrows(ConversationAlreadyEndedException.class, () ->
                conversation.addTurn(ConversationTurn.userTurn("Hello!", 0.9f)));
    }

    @Test
    void shouldEndConversation() throws ConversationAlreadyEndedException {
        Conversation conversation = ConversationMother.active();

        Conversation ended = conversation.end("Good session.");

        assertEquals(ConversationStatus.COMPLETED, ended.status());
        assertEquals("Good session.", ended.summary());
        assertNotNull(ended.endedAt());
    }

    @Test
    void shouldNotEndAlreadyCompletedConversation() {
        Conversation conversation = ConversationMother.completed();

        assertThrows(ConversationAlreadyEndedException.class, () ->
                conversation.end("Summary"));
    }

    @Test
    void shouldReturnRecentTurnsWhenUnderLimit() throws ConversationAlreadyEndedException {
        Conversation conversation = ConversationMother.activeWithTurns();

        assertEquals(2, conversation.recentTurns(30).size());
    }

    @Test
    void shouldRegisterStartedEvent() {
        Conversation conversation = Conversation.start(UUID.randomUUID(), new ConversationLevel("a1"), null);

        assertEquals(1, conversation.pullDomainEvents().size());
    }

    @Test
    void shouldRegisterCompletedEvent() throws ConversationAlreadyEndedException {
        Conversation conversation = ConversationMother.active();

        Conversation ended = conversation.end("Summary");

        assertEquals(1, ended.pullDomainEvents().size());
    }
}
