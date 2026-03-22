package com.faus535.englishtrainer.conversation.domain.error;

import com.faus535.englishtrainer.conversation.domain.ConversationId;

public final class ConversationNotFoundException extends ConversationException {

    public ConversationNotFoundException(ConversationId id) {
        super("Conversation not found: " + id.value());
    }
}
