package com.faus535.englishtrainer.user.infrastructure.controller;

import com.faus535.englishtrainer.user.application.CreateUserProfileUseCase;
import com.faus535.englishtrainer.user.domain.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
class CreateUserProfileController {

    private final CreateUserProfileUseCase useCase;

    CreateUserProfileController(CreateUserProfileUseCase useCase) {
        this.useCase = useCase;
    }

    record UserProfileResponse(String id, int xp, Instant createdAt) {}

    @PostMapping("/api/profiles")
    ResponseEntity<UserProfileResponse> handle() {
        UserProfile profile = useCase.execute();
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserProfileResponse(
                profile.id().value().toString(),
                profile.xp(),
                profile.createdAt()
        ));
    }
}
