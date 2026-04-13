package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.PronunciationMiniConversationDto;
import com.faus535.englishtrainer.pronunciation.application.StartMiniConversationUseCase;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
class StartMiniConversationController {

    private final StartMiniConversationUseCase useCase;

    StartMiniConversationController(StartMiniConversationUseCase useCase) {
        this.useCase = useCase;
    }

    record StartMiniConversationRequest(
            @NotBlank String focus,
            @NotBlank @Pattern(regexp = "(?i)(a1|a2|b1|b2|c1|c2)") String level) {}

    record MiniConversationResponse(UUID id, String prompt, String targetPhrase) {
        static MiniConversationResponse fromDto(PronunciationMiniConversationDto dto) {
            return new MiniConversationResponse(dto.id(), dto.prompt(), dto.targetPhrase());
        }
    }

    @PostMapping("/api/pronunciation/mini-conversation")
    ResponseEntity<MiniConversationResponse> handle(@Valid @RequestBody StartMiniConversationRequest request,
            Authentication authentication) throws PronunciationAiException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        PronunciationMiniConversationDto dto = useCase.execute(userId, request.focus(), request.level());
        return ResponseEntity.status(HttpStatus.CREATED).body(MiniConversationResponse.fromDto(dto));
    }
}
