package com.faus535.englishtrainer.conversation.domain.error;

public final class AiTutorException extends ConversationException {

    public AiTutorException(String message) {
        super(message);
    }

    public AiTutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
