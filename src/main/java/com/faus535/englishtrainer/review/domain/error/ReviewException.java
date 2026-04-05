package com.faus535.englishtrainer.review.domain.error;

public abstract class ReviewException extends Exception {

    protected ReviewException(String message) {
        super(message);
    }

    protected ReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
