package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.EvaluateMiniConversationTurnUseCase;
import com.faus535.englishtrainer.pronunciation.application.MiniConversationTurnResultDto;
import com.faus535.englishtrainer.pronunciation.application.WordConfidenceDto;
import com.faus535.englishtrainer.pronunciation.application.WordFeedbackDto;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationMiniConversationNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class EvaluateMiniConversationTurnController {

    private final EvaluateMiniConversationTurnUseCase useCase;

    EvaluateMiniConversationTurnController(EvaluateMiniConversationTurnUseCase useCase) {
        this.useCase = useCase;
    }

    record WordConfidenceRequest(String word, double confidence) {}

    record EvaluateTurnRequest(
            @NotBlank String recognizedText,
            @NotNull List<WordConfidenceRequest> wordConfidences) {}

    record WordFeedbackResponse(String word, String recognized, String tip, int score) {}

    record TurnResultResponse(int score, List<WordFeedbackResponse> wordFeedback,
            String nextPrompt, String nextTargetPhrase, boolean isComplete) {
        static TurnResultResponse fromDto(MiniConversationTurnResultDto dto) {
            List<WordFeedbackResponse> wordFeedback = dto.wordFeedback().stream()
                    .map(wf -> new WordFeedbackResponse(wf.word(), wf.recognized(), wf.tip(), wf.score()))
                    .toList();
            return new TurnResultResponse(dto.score(), wordFeedback,
                    dto.nextPrompt(), dto.nextTargetPhrase(), dto.isComplete());
        }
    }

    @PostMapping("/api/pronunciation/mini-conversation/{id}/evaluate")
    ResponseEntity<TurnResultResponse> handle(@PathVariable UUID id,
            @Valid @RequestBody EvaluateTurnRequest request)
            throws PronunciationMiniConversationNotFoundException, PronunciationAiException {
        List<WordConfidenceDto> wordConfidences = request.wordConfidences().stream()
                .map(wc -> new WordConfidenceDto(wc.word(), wc.confidence()))
                .toList();
        MiniConversationTurnResultDto dto = useCase.execute(id, request.recognizedText(), wordConfidences);
        return ResponseEntity.ok(TurnResultResponse.fromDto(dto));
    }
}
