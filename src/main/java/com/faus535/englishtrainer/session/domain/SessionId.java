package com.faus535.englishtrainer.session.domain;

import java.util.UUID;

public record SessionId(UUID value) {

    public SessionId {
        if (value == null) {
            throw new IllegalArgumentException("SessionId cannot be null");
        }
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId fromString(String id) {
        return new SessionId(UUID.fromString(id));
    }
}
