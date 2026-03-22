package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.SendMessageUseCase;
import com.faus535.englishtrainer.conversation.domain.TutorFeedback;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SendMessageController {

    private final SendMessageUseCase useCase;

    SendMessageController(SendMessageUseCase useCase) {
        this.useCase = useCase;
    }

    record SendMessageRequest(
            @NotBlank String transcript,
            Float confidence
    ) {}

    record MessageResponse(String content, TutorFeedback feedback, boolean suggestEnd) {}

    @PostMapping("/api/conversations/{id}/messages")
    ResponseEntity<MessageResponse> handle(@PathVariable UUID id,
                                            @Valid @RequestBody SendMessageRequest request)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        SendMessageUseCase.SendMessageResult result = useCase.execute(
                id, request.transcript(), request.confidence());

        return ResponseEntity.ok(new MessageResponse(result.content(), result.feedback(), result.suggestEnd()));
    }
}
