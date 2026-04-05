package com.faus535.englishtrainer.talk.infrastructure.controller;

import com.faus535.englishtrainer.talk.application.StartTalkConversationUseCase;
import com.faus535.englishtrainer.talk.domain.TalkConversation;
import com.faus535.englishtrainer.talk.domain.error.TalkAiException;
import com.faus535.englishtrainer.talk.domain.error.TalkMaxConversationsExceededException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class StartTalkConversationController {

    private final StartTalkConversationUseCase useCase;

    StartTalkConversationController(StartTalkConversationUseCase useCase) {
        this.useCase = useCase;
    }

    record StartTalkRequest(
            @NotNull UUID scenarioId,
            @NotBlank @Pattern(regexp = "(?i)(a1|a2|b1|b2|c1|c2)") String level
    ) {}

    @PostMapping("/api/talk/conversations")
    ResponseEntity<TalkConversationResponse> handle(@Valid @RequestBody StartTalkRequest request,
                                                     Authentication authentication)
            throws TalkMaxConversationsExceededException, TalkAiException {

        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        TalkConversation conversation = useCase.execute(userId, request.scenarioId(), request.level());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TalkConversationResponse.from(conversation));
    }
}
