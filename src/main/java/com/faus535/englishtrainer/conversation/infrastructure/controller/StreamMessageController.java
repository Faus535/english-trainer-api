package com.faus535.englishtrainer.conversation.infrastructure.controller;

import com.faus535.englishtrainer.conversation.application.StreamMessageUseCase;
import com.faus535.englishtrainer.conversation.domain.error.AiTutorException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationAlreadyEndedException;
import com.faus535.englishtrainer.conversation.domain.error.ConversationNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
class StreamMessageController {

    private final StreamMessageUseCase useCase;

    StreamMessageController(StreamMessageUseCase useCase) {
        this.useCase = useCase;
    }

    record StreamMessageRequest(
            @NotBlank String transcript,
            Float confidence
    ) {}

    @PostMapping(value = "/api/conversations/{id}/messages/stream",
                 produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> handle(@PathVariable UUID id,
                        @Valid @RequestBody StreamMessageRequest request)
            throws ConversationNotFoundException, ConversationAlreadyEndedException, AiTutorException {

        return useCase.execute(id, request.transcript(), request.confidence());
    }
}
