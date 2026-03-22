package com.faus535.englishtrainer.conversation.domain;

import java.util.UUID;

public record ConversationTurnId(UUID value) {

    public ConversationTurnId {
        if (value == null) {
            throw new IllegalArgumentException("ConversationTurnId cannot be null");
        }
    }

    public static ConversationTurnId generate() {
        return new ConversationTurnId(UUID.randomUUID());
    }
}
