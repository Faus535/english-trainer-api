package com.faus535.englishtrainer.home.infrastructure.controller;

import com.faus535.englishtrainer.home.application.GetHomeUseCase;
import com.faus535.englishtrainer.home.application.HomeData;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetHomeController {

    private final GetHomeUseCase useCase;

    GetHomeController(GetHomeUseCase useCase) {
        this.useCase = useCase;
    }

    @RequireProfileOwnership
    @GetMapping("/api/profiles/{userId}/home")
    ResponseEntity<HomeData> handle(@PathVariable UUID userId) {
        return ResponseEntity.ok(useCase.execute(userId));
    }
}
