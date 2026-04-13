package com.faus535.englishtrainer.pronunciation.domain.error;

public abstract class PronunciationException extends Exception {

    protected PronunciationException(String message) {
        super(message);
    }

    protected PronunciationException(String message, Throwable cause) {
        super(message, cause);
    }
}
