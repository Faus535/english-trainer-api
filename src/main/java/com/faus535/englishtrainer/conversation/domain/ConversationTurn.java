package com.faus535.englishtrainer.conversation.domain;

import java.time.Instant;

public record ConversationTurn(
        ConversationTurnId id,
        String role,
        String content,
        TutorFeedback feedback,
        Float confidence,
        Instant createdAt
) {

    public static ConversationTurn userTurn(String content, Float confidence) {
        return new ConversationTurn(
                ConversationTurnId.generate(), "user", content, null, confidence, Instant.now());
    }

    public static ConversationTurn assistantTurn(String content, TutorFeedback feedback) {
        return new ConversationTurn(
                ConversationTurnId.generate(), "assistant", content, feedback, null, Instant.now());
    }

    public static ConversationTurn reconstitute(ConversationTurnId id, String role, String content,
                                                 TutorFeedback feedback, Float confidence, Instant createdAt) {
        return new ConversationTurn(id, role, content, feedback, confidence, createdAt);
    }
}
