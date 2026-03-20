package com.faus535.englishtrainer.phrase.infrastructure.controller;

import com.faus535.englishtrainer.phrase.application.GetRandomPhrasesUseCase;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetRandomPhrasesController {

    private final GetRandomPhrasesUseCase useCase;

    GetRandomPhrasesController(GetRandomPhrasesUseCase useCase) {
        this.useCase = useCase;
    }

    record PhraseResponse(String id, String en, String es, String level) {}

    @GetMapping("/api/phrases/random")
    ResponseEntity<List<PhraseResponse>> handle(
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(required = false) String level) {
        VocabLevel vocabLevel = (level != null) ? new VocabLevel(level) : null;
        List<PhraseResponse> response = useCase.execute(count, vocabLevel).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private PhraseResponse toResponse(Phrase phrase) {
        return new PhraseResponse(
                phrase.id().value().toString(),
                phrase.en(),
                phrase.es(),
                phrase.level().value()
        );
    }
}
