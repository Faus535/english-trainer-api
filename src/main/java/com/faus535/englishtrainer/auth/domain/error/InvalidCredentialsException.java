package com.faus535.englishtrainer.auth.domain.error;

public final class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
