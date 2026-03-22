package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.StartConversationUseCase;
import com.faus535.englishtrainer.conversation.domain.Conversation;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.MaxConversationsExceededException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class StartConversationController {

    private final StartConversationUseCase useCase;

    StartConversationController(StartConversationUseCase useCase) {
        this.useCase = useCase;
    }

    record StartRequest(
            @NotBlank @Pattern(regexp = "(?i)(a1|a2|b1|b2|c1|c2)") String level,
            String topic
    ) {}

    @PostMapping("/api/conversations")
    ResponseEntity<ConversationResponse> handle(@Valid @RequestBody StartRequest request,
                                                 Authentication authentication)
            throws MaxConversationsExceededException, AiTutorException {

        UUID userId = UUID.fromString(authentication.getName());
        Conversation conversation = useCase.execute(userId, request.level(), request.topic());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ConversationResponse.from(conversation));
    }
}
