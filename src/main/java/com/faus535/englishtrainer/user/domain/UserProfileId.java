package com.faus535.englishtrainer.user.domain;

import java.util.UUID;

public record UserProfileId(UUID value) {

    public UserProfileId {
        if (value == null) {
            throw new IllegalArgumentException("UserProfileId cannot be null");
        }
    }

    public static UserProfileId generate() {
        return new UserProfileId(UUID.randomUUID());
    }

    public static UserProfileId fromString(String id) {
        return new UserProfileId(UUID.fromString(id));
    }
}
