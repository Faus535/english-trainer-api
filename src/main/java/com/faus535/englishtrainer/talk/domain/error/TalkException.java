package com.faus535.englishtrainer.talk.domain.error;

public abstract class TalkException extends Exception {

    protected TalkException(String message) {
        super(message);
    }

    protected TalkException(String message, Throwable cause) {
        super(message, cause);
    }
}
