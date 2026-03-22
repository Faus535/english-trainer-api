package com.faus535.englishtrainer.auth.domain.error;

public final class TooManyResetAttemptsException extends AuthException {

    public TooManyResetAttemptsException() {
        super("Too many password reset attempts. Please try again later.");
    }
}
