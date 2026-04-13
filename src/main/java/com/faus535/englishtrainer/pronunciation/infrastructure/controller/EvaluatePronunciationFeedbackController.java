package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.EvaluatePronunciationFeedbackUseCase;
import com.faus535.englishtrainer.pronunciation.application.PronunciationFeedbackDto;
import com.faus535.englishtrainer.pronunciation.application.WordConfidenceDto;
import com.faus535.englishtrainer.pronunciation.application.WordFeedbackDto;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class EvaluatePronunciationFeedbackController {

    private final EvaluatePronunciationFeedbackUseCase useCase;

    EvaluatePronunciationFeedbackController(EvaluatePronunciationFeedbackUseCase useCase) {
        this.useCase = useCase;
    }

    record WordConfidenceRequest(String word, double confidence) {}

    record FeedbackRequest(
            @NotBlank String targetText,
            @NotBlank String recognizedText,
            @NotNull List<WordConfidenceRequest> wordConfidences) {}

    record WordFeedbackResponse(String word, String recognized, String tip, int score) {}

    record FeedbackResponse(int score, List<WordFeedbackResponse> wordFeedback, String overallTip) {
        static FeedbackResponse fromDto(PronunciationFeedbackDto dto) {
            List<WordFeedbackResponse> wordFeedback = dto.wordFeedback().stream()
                    .map(wf -> new WordFeedbackResponse(wf.word(), wf.recognized(), wf.tip(), wf.score()))
                    .toList();
            return new FeedbackResponse(dto.score(), wordFeedback, dto.overallTip());
        }
    }

    @PostMapping("/api/pronunciation/feedback")
    ResponseEntity<FeedbackResponse> handle(@Valid @RequestBody FeedbackRequest request)
            throws PronunciationAiException {
        List<WordConfidenceDto> wordConfidences = request.wordConfidences().stream()
                .map(wc -> new WordConfidenceDto(wc.word(), wc.confidence()))
                .toList();
        PronunciationFeedbackDto dto = useCase.execute(request.targetText(), request.recognizedText(), wordConfidences);
        return ResponseEntity.ok(FeedbackResponse.fromDto(dto));
    }
}
