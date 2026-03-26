package com.faus535.englishtrainer.errorpattern.infrastructure.controller;

import com.faus535.englishtrainer.errorpattern.application.GetErrorPatternsUseCase;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetErrorPatternsController {

    private final GetErrorPatternsUseCase useCase;

    GetErrorPatternsController(GetErrorPatternsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/error-patterns")
    @RequireProfileOwnership
    ResponseEntity<List<ErrorPattern>> handle(@PathVariable UUID userId,
                                               Authentication authentication) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
