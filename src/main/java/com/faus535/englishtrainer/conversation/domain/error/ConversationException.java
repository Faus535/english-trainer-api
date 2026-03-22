package com.faus535.englishtrainer.conversation.domain.error;

public class ConversationException extends Exception {

    public ConversationException(String message) {
        super(message);
    }

    public ConversationException(String message, Throwable cause) {
        super(message, cause);
    }
}
