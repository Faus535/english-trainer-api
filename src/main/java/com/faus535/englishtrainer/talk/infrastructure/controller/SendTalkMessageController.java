package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.SendTalkMessageUseCase;
import com.faus535.englishtrainer.talk.domain.TalkCorrection;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationAlreadyEndedException;
import com.faus535.englishtrainer.talk.domain.error.TalkConversationNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SendTalkMessageController {

    private final SendTalkMessageUseCase useCase;

    SendTalkMessageController(SendTalkMessageUseCase useCase) {
        this.useCase = useCase;
    }

    record SendMessageRequest(
            @NotBlank @Size(max = 5000) String content,
            Float confidence
    ) {}

    record SendMessageResponse(String content, TalkCorrection correction, boolean suggestEnd) {}

    @PostMapping("/api/talk/conversations/{id}/messages")
    ResponseEntity<SendMessageResponse> handle(@PathVariable UUID id,
                                                @Valid @RequestBody SendMessageRequest request)
            throws TalkConversationNotFoundException, TalkConversationAlreadyEndedException, TalkAiException {

        var result = useCase.execute(id, request.content(), request.confidence());

        return ResponseEntity.ok(new SendMessageResponse(
                result.content(), result.correction(), result.suggestEnd()));
    }
}
