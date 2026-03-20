package com.faus535.englishtrainer.auth.domain.error;

public final class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
