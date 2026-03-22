package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.GetConversationUseCase;
import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetConversationController {

    private final GetConversationUseCase useCase;

    GetConversationController(GetConversationUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/conversations/{id}")
    ResponseEntity<ConversationResponse> handle(@PathVariable UUID id) throws ConversationNotFoundException {
        Conversation conversation = useCase.execute(id);
        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }
}
