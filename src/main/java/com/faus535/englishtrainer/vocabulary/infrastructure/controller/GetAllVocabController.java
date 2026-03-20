package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.application.GetAllVocabUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetAllVocabController {

    private final GetAllVocabUseCase useCase;

    GetAllVocabController(GetAllVocabUseCase useCase) {
        this.useCase = useCase;
    }

    record VocabResponse(String id, String en, String ipa, String es, String type, String example, String level) {}

    @GetMapping("/api/vocab")
    ResponseEntity<List<VocabResponse>> handle() {
        List<VocabResponse> response = useCase.execute().stream()
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
