package com.faus535.englishtrainer.auth.infrastructure.controller;

import com.faus535.englishtrainer.auth.application.GetCurrentUserUseCase;
import com.faus535.englishtrainer.auth.domain.AuthUser;
import com.faus535.englishtrainer.auth.domain.AuthUserId;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetCurrentUserController {

    private final GetCurrentUserUseCase useCase;

    GetCurrentUserController(GetCurrentUserUseCase useCase) {
        this.useCase = useCase;
    }

    record CurrentUserResponse(String userId, String email, String profileId, String role,
                               String provider, String createdAt) {}

    @GetMapping("/api/auth/me")
    ResponseEntity<CurrentUserResponse> handle(Authentication authentication) throws NotFoundException {
        String userId = (String) authentication.getPrincipal();
        AuthUserId authUserId = AuthUserId.fromString(userId);

        AuthUser user = useCase.execute(authUserId);

        return ResponseEntity.ok(new CurrentUserResponse(
                user.id().value().toString(),
                user.email(),
                user.userProfileId().value().toString(),
                user.role(),
                user.authProvider().toLowerCase(),
                user.createdAt().toString()));
    }
}
