package com.faus535.englishtrainer.session.domain.error;

public class IncompleteSessionException extends SessionException {

    public IncompleteSessionException() {
        super("All blocks must be completed before finishing the session");
    }
}
