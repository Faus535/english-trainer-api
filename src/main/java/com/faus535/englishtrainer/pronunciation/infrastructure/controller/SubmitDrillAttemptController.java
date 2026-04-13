package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.DrillAttemptResultDto;
import com.faus535.englishtrainer.pronunciation.application.SubmitDrillAttemptUseCase;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationDrillNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class SubmitDrillAttemptController {

    private final SubmitDrillAttemptUseCase useCase;

    SubmitDrillAttemptController(SubmitDrillAttemptUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitAttemptRequest(@NotBlank String recognizedText, double confidence) {}

    record AttemptResultResponse(int score, String feedback, int perfectStreak) {
        static AttemptResultResponse fromDto(DrillAttemptResultDto dto) {
            return new AttemptResultResponse(dto.score(), dto.feedback(), dto.perfectStreak());
        }
    }

    @PostMapping("/api/pronunciation/drills/{id}/submit")
    ResponseEntity<AttemptResultResponse> handle(@PathVariable UUID id,
            @Valid @RequestBody SubmitAttemptRequest request,
            Authentication authentication)
            throws PronunciationDrillNotFoundException, PronunciationAiException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        DrillAttemptResultDto dto = useCase.execute(id, userId, request.recognizedText(), request.confidence());
        return ResponseEntity.ok(AttemptResultResponse.fromDto(dto));
    }
}
