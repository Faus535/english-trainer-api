package com.faus535.englishtrainer.talk.domain.error;

public final class TalkAiException extends TalkException {

    public TalkAiException(String message) {
        super(message);
    }

    public TalkAiException(String message, Throwable cause) {
        super(message, cause);
    }
}
