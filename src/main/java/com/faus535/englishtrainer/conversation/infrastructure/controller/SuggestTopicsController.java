package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.SuggestTopicsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class SuggestTopicsController {

    private final SuggestTopicsUseCase useCase;

    SuggestTopicsController(SuggestTopicsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/conversations/suggested-topics")
    ResponseEntity<List<String>> handle(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<String> topics = useCase.execute(userId);
        return ResponseEntity.ok(topics);
    }
}
