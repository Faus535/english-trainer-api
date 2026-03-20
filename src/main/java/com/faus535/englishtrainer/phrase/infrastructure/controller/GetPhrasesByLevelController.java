package com.faus535.englishtrainer.phrase.infrastructure.controller;

import com.faus535.englishtrainer.phrase.application.GetPhrasesByLevelUseCase;
import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetPhrasesByLevelController {

    private final GetPhrasesByLevelUseCase useCase;

    GetPhrasesByLevelController(GetPhrasesByLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record PhraseResponse(String id, String en, String es, String level) {}

    @GetMapping("/api/phrases")
    ResponseEntity<List<PhraseResponse>> handle(@RequestParam String level) {
        List<PhraseResponse> response = useCase.execute(new VocabLevel(level)).stream()
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
