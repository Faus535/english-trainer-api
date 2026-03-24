package com.faus535.englishtrainer.user.domain.error;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class ProfileOwnershipException extends UserProfileException {

    public ProfileOwnershipException(UserProfileId profileId) {
        super("Cannot modify profile: " + profileId.value());
    }
}
