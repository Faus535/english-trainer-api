package com.faus535.englishtrainer.user.domain.error;

import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class UserProfileNotFoundException extends NotFoundException {

    public UserProfileNotFoundException(UserProfileId id) {
        super("User profile not found: " + id.value());
    }
}
