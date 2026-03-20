package com.faus535.englishtrainer.auth.domain;

import java.util.UUID;

public record AuthUserId(UUID value) {

    public AuthUserId {
        if (value == null) {
            throw new IllegalArgumentException("AuthUserId cannot be null");
        }
    }

    public static AuthUserId generate() {
        return new AuthUserId(UUID.randomUUID());
    }

    public static AuthUserId fromString(String id) {
        return new AuthUserId(UUID.fromString(id));
    }
}
