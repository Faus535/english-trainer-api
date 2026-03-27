package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GetCurrentSessionUseCase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
class GetCurrentSessionController {

    private final GetCurrentSessionUseCase useCase;

    GetCurrentSessionController(GetCurrentSessionUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/sessions/current")
    @RequireProfileOwnership
    ResponseEntity<SessionResponse> handle(@PathVariable String userId,
                                            Authentication authentication) {
        Optional<Session> session = useCase.execute(UserProfileId.fromString(userId));

        return session
                .map(s -> ResponseEntity.ok(SessionResponseMapper.toResponse(s)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
