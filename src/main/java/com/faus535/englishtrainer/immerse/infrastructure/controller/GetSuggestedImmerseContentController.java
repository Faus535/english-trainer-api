package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetSuggestedImmerseContentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GetSuggestedImmerseContentController {

    private final GetSuggestedImmerseContentUseCase useCase;

    GetSuggestedImmerseContentController(GetSuggestedImmerseContentUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/immerse/content/suggested")
    ResponseEntity<ImmerseContentResponse> handle(Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        return useCase.execute(userId)
                .map(content -> ResponseEntity.ok(ImmerseContentResponse.from(content)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
