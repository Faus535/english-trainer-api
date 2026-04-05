package com.faus535.englishtrainer.immerse.domain.error;

public abstract class ImmerseException extends Exception {

    protected ImmerseException(String message) { super(message); }
    protected ImmerseException(String message, Throwable cause) { super(message, cause); }
}
