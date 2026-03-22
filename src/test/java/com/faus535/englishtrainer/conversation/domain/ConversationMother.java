package com.faus535.englishtrainer.conversation.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ConversationMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static Conversation active() {
        return Conversation.reconstitute(
                ConversationId.generate(), DEFAULT_USER_ID,
                new ConversationLevel("b1"), "Daily routines",
                ConversationStatus.ACTIVE, null, Instant.now(), null, List.of());
    }

    public static Conversation activeWithTurns() {
        return Conversation.reconstitute(
                ConversationId.generate(), DEFAULT_USER_ID,
                new ConversationLevel("b1"), "Travel",
                ConversationStatus.ACTIVE, null, Instant.now(), null,
                List.of(
                        ConversationTurn.assistantTurn("Hello! Let's talk about travel.", TutorFeedback.empty()),
                        ConversationTurn.userTurn("I like travel to Spain", 0.9f)
                ));
    }

    public static Conversation completed() {
        return Conversation.reconstitute(
                ConversationId.generate(), DEFAULT_USER_ID,
                new ConversationLevel("b1"), "Food",
                ConversationStatus.COMPLETED, "Good session about food vocabulary.",
                Instant.now().minusSeconds(3600), Instant.now(), List.of());
    }

    public static Conversation withUserId(UUID userId) {
        return Conversation.reconstitute(
                ConversationId.generate(), userId,
                new ConversationLevel("a2"), null,
                ConversationStatus.ACTIVE, null, Instant.now(), null, List.of());
    }
}
