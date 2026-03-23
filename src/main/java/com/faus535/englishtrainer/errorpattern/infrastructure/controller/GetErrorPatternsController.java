package com.faus535.englishtrainer.errorpattern.infrastructure.controller;

import com.faus535.englishtrainer.errorpattern.application.GetErrorPatternsUseCase;
import com.faus535.englishtrainer.errorpattern.domain.ErrorPattern;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<List<ErrorPattern>> handle(@PathVariable UUID userId) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
