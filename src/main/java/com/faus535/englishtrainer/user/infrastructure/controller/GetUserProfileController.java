package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.GetUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
class GetUserProfileController {

    private final GetUserProfileUseCase useCase;

    GetUserProfileController(GetUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    record UserProfileResponse(String id, int xp, Instant createdAt) {}

    @GetMapping("/api/profiles/{id}")
    ResponseEntity<UserProfileResponse> handle(@PathVariable UUID id, Authentication authentication)
            throws UserProfileNotFoundException, ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, id);
        UserProfile profile = useCase.execute(new UserProfileId(id));
        return ResponseEntity.ok(new UserProfileResponse(
                profile.id().value().toString(),
                profile.xp(),
                profile.createdAt()
        ));
    }
}
