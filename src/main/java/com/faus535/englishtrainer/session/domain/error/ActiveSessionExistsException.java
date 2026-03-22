package com.faus535.englishtrainer.session.domain.error;

public final class ActiveSessionExistsException extends SessionException {

    public ActiveSessionExistsException() {
        super("User already has an active session");
    }
}
