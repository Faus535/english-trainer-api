package com.faus535.englishtrainer.conversation.domain.error;

public final class MaxConversationsExceededException extends ConversationException {

    public MaxConversationsExceededException(int max) {
        super("Maximum concurrent conversations exceeded. Limit: " + max);
    }
}
