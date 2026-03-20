package com.faus535.englishtrainer.auth.domain.error;

import com.faus535.englishtrainer.shared.domain.error.AlreadyExistsException;

public final class EmailAlreadyExistsException extends AlreadyExistsException {

    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
