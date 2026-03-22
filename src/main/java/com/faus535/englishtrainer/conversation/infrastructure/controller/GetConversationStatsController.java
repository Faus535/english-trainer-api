package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.GetConversationStatsUseCase;
import com.faus535.englishtrainer.conversation.domain.ConversationStats;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetConversationStatsController {

    private final GetConversationStatsUseCase useCase;

    GetConversationStatsController(GetConversationStatsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/conversations/stats")
    ResponseEntity<ConversationStats> handle(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ConversationStats stats = useCase.execute(userId);
        return ResponseEntity.ok(stats);
    }
}
