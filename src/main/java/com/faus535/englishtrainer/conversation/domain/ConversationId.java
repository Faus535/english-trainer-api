package com.faus535.englishtrainer.conversation.domain;

import java.util.UUID;

public record ConversationId(UUID value) {

    public ConversationId {
        if (value == null) {
            throw new IllegalArgumentException("ConversationId cannot be null");
        }
    }

    public static ConversationId generate() {
        return new ConversationId(UUID.randomUUID());
    }
}
