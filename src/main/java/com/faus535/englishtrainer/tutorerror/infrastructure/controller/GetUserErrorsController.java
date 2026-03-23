package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.application.GetUserErrorsUseCase;
import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
class GetUserErrorsController {

    private final GetUserErrorsUseCase useCase;

    GetUserErrorsController(GetUserErrorsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/tutor/errors")
    ResponseEntity<Map<String, List<TutorErrorResponse>>> handle(
            @PathVariable UUID userId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, List<TutorError>> grouped = useCase.execute(
                new UserProfileId(userId), type, limit);

        Map<String, List<TutorErrorResponse>> response = grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(TutorErrorResponse::from)
                                .toList()));

        return ResponseEntity.ok(response);
    }
}
