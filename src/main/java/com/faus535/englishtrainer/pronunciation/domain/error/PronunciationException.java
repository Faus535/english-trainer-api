package com.faus535.englishtrainer.pronunciation.domain.error;

public class PronunciationException extends Exception {

    public PronunciationException(String message) {
        super(message);
    }

    public PronunciationException(String message, Throwable cause) {
        super(message, cause);
    }
}
