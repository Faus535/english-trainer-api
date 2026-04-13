package com.faus535.englishtrainer.pronunciation.domain.error;

public final class PronunciationAiException extends PronunciationException {

    public PronunciationAiException(String message) {
        super(message);
    }

    public PronunciationAiException(String message, Throwable cause) {
        super(message, cause);
    }
}
