package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetSuggestedImmerseContentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetSuggestedImmerseContentController {

    private final GetSuggestedImmerseContentUseCase useCase;

    GetSuggestedImmerseContentController(GetSuggestedImmerseContentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/immerse/content/suggested")
    ResponseEntity<ImmerseContentResponse> handle(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return useCase.execute(userId)
                .map(content -> ResponseEntity.ok(ImmerseContentResponse.from(content)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
