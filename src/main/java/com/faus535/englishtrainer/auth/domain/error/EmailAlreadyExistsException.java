package com.faus535.englishtrainer.auth.domain.error;

public final class EmailAlreadyExistsException extends AuthException {

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
