package com.faus535.englishtrainer.session.domain.error;

import com.faus535.englishtrainer.session.domain.SessionId;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;

public final class SessionNotFoundException extends NotFoundException {

    public SessionNotFoundException(SessionId id) {
        super("Session not found: " + id.value());
    }
}
