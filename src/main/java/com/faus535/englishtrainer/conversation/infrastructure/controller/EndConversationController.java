package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.EndConversationUseCase;
import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class EndConversationController {

    private final EndConversationUseCase useCase;

    EndConversationController(EndConversationUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/api/conversations/{id}/end")
    ResponseEntity<ConversationResponse> handle(@PathVariable UUID id)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        Conversation conversation = useCase.execute(id);
        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }
}
