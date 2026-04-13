package com.faus535.englishtrainer.pronunciation.domain.error;

import java.util.UUID;

public final class PronunciationMiniConversationNotFoundException extends PronunciationException {

    public PronunciationMiniConversationNotFoundException(UUID conversationId) {
        super("Pronunciation mini-conversation not found: " + conversationId);
    }
}
