package com.faus535.englishtrainer.home.infrastructure.controller;

import com.faus535.englishtrainer.home.application.GetHomeUseCase;
import com.faus535.englishtrainer.home.application.HomeData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GetHomeByJwtController {

    private final GetHomeUseCase useCase;

    GetHomeByJwtController(GetHomeUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/home")
    ResponseEntity<HomeData> handle(Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID profileId = UUID.fromString(details.get("profileId"));
        return ResponseEntity.ok(useCase.execute(profileId));
    }
}
