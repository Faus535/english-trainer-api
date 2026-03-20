package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.application.GetRandomVocabUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetRandomVocabController {

    private final GetRandomVocabUseCase useCase;

    GetRandomVocabController(GetRandomVocabUseCase useCase) {
        this.useCase = useCase;
    }

    record VocabResponse(String id, String en, String ipa, String es, String type, String example, String level) {}

    @GetMapping("/api/vocab/random")
    ResponseEntity<List<VocabResponse>> handle(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) String level) {
        VocabLevel vocabLevel = (level != null) ? new VocabLevel(level) : null;
        List<VocabResponse> response = useCase.execute(count, vocabLevel).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private VocabResponse toResponse(VocabEntry entry) {
        return new VocabResponse(
                entry.id().value().toString(),
                entry.en(),
                entry.ipa(),
                entry.es(),
                entry.type(),
                entry.example(),
                entry.level().value()
        );
    }
}
