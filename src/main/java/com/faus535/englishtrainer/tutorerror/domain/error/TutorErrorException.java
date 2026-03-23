package com.faus535.englishtrainer.tutorerror.domain.error;

public class TutorErrorException extends Exception {

    public TutorErrorException(String message) {
        super(message);
    }

    public TutorErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
