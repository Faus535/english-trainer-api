package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.application.GetSessionHistoryUseCase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetSessionHistoryController {

    private final GetSessionHistoryUseCase useCase;

    GetSessionHistoryController(GetSessionHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/sessions")
    @RequireProfileOwnership
    ResponseEntity<List<SessionSummaryResponse>> handle(@PathVariable String userId,
                                                         Authentication authentication) {
        List<Session> sessions = useCase.execute(UserProfileId.fromString(userId));

        List<SessionSummaryResponse> response = sessions.stream()
                .map(s -> new SessionSummaryResponse(
                        s.id().value().toString(),
                        s.mode().value(),
                        s.sessionType().value(),
                        s.completed(),
                        s.startedAt().toString(),
                        s.durationMinutes()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
