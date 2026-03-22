package com.faus535.englishtrainer.conversation.domain.error;

import com.faus535.englishtrainer.conversation.domain.ConversationId;

public final class ConversationAlreadyEndedException extends ConversationException {

    public ConversationAlreadyEndedException(ConversationId id) {
        super("Conversation already ended: " + id.value());
    }
}
