package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.ListConversationsUseCase;
import com.faus535.englishtrainer.conversation.domain.Conversation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class ListConversationsController {

    private final ListConversationsUseCase useCase;

    ListConversationsController(ListConversationsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/conversations")
    ResponseEntity<List<ConversationResponse>> handle(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<Conversation> conversations = useCase.execute(userId);
        List<ConversationResponse> response = conversations.stream()
                .map(ConversationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
