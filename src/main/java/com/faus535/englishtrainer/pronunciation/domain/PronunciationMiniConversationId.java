package com.faus535.englishtrainer.pronunciation.domain;

import java.util.UUID;

public record PronunciationMiniConversationId(UUID value) {

    public PronunciationMiniConversationId {
        if (value == null) {
            throw new IllegalArgumentException("PronunciationMiniConversationId cannot be null");
        }
    }

    public static PronunciationMiniConversationId generate() {
        return new PronunciationMiniConversationId(UUID.randomUUID());
    }
}
