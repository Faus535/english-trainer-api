package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.UUID;

final class ProfileOwnershipChecker {

    private ProfileOwnershipChecker() {}

    @SuppressWarnings("unchecked")
    static void check(Authentication authentication, UUID profileId) throws ProfileOwnershipException {
        if (authentication == null || authentication.getDetails() == null) {
            throw new ProfileOwnershipException(new UserProfileId(profileId));
        }

        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        String authenticatedProfileId = details.get("profileId");

        if (!profileId.toString().equals(authenticatedProfileId)) {
            throw new ProfileOwnershipException(new UserProfileId(profileId));
        }
    }
}
