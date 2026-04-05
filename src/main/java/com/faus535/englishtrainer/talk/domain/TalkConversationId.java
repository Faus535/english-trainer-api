package com.faus535.englishtrainer.talk.domain;

import java.util.UUID;

public record TalkConversationId(UUID value) {

    public TalkConversationId {
        if (value == null) {
            throw new IllegalArgumentException("TalkConversationId cannot be null");
        }
    }

    public static TalkConversationId generate() {
        return new TalkConversationId(UUID.randomUUID());
    }
}
