package com.faus535.englishtrainer.session.domain.error;

import com.faus535.englishtrainer.session.domain.SessionId;

public final class SessionNotFoundException extends SessionException {

    public SessionNotFoundException(SessionId id) {
        super("Session not found: " + id.value());
    }
}
