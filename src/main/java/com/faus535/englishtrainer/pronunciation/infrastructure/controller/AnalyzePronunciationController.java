package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.AnalyzePronunciationUseCase;
import com.faus535.englishtrainer.pronunciation.application.PronunciationAnalysisDto;
import com.faus535.englishtrainer.pronunciation.domain.error.PronunciationAiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class AnalyzePronunciationController {

    private final AnalyzePronunciationUseCase useCase;

    AnalyzePronunciationController(AnalyzePronunciationUseCase useCase) {
        this.useCase = useCase;
    }

    record AnalyzeRequest(
            @NotBlank String text,
            @NotBlank @Pattern(regexp = "(?i)(a1|a2|b1|b2|c1|c2)") String level) {}

    record AnalyzeResponse(
            String text,
            String ipa,
            String syllables,
            String stressPattern,
            List<String> tips,
            List<String> commonMistakes,
            List<String> minimalPairs,
            List<String> exampleSentences) {

        static AnalyzeResponse fromDto(PronunciationAnalysisDto dto) {
            return new AnalyzeResponse(dto.text(), dto.ipa(), dto.syllables(), dto.stressPattern(),
                    dto.tips(), dto.commonMistakes(), dto.minimalPairs(), dto.exampleSentences());
        }
    }

    @PostMapping("/api/pronunciation/analyze")
    ResponseEntity<AnalyzeResponse> handle(@Valid @RequestBody AnalyzeRequest request)
            throws PronunciationAiException {
        PronunciationAnalysisDto dto = useCase.execute(request.text(), request.level());
        return ResponseEntity.ok(AnalyzeResponse.fromDto(dto));
    }
}
