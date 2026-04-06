package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetImmerseContentUseCase;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentAccessDeniedException;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GetImmerseContentController {

    private final GetImmerseContentUseCase useCase;

    GetImmerseContentController(GetImmerseContentUseCase useCase) { this.useCase = useCase; }

    @GetMapping("/api/immerse/content/{id}")
    ResponseEntity<ImmerseContentResponse> handle(@PathVariable UUID id, Authentication authentication)
            throws ImmerseContentNotFoundException, ImmerseContentAccessDeniedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        return ResponseEntity.ok(ImmerseContentResponse.from(useCase.execute(id, userId)));
    }
}
