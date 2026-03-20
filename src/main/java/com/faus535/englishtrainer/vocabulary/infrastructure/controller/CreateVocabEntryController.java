package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.application.CreateVocabEntryUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class CreateVocabEntryController {

    private final CreateVocabEntryUseCase useCase;

    CreateVocabEntryController(CreateVocabEntryUseCase useCase) {
        this.useCase = useCase;
    }

    record CreateVocabRequest(@NotBlank String en, String ipa, @NotBlank String es, String type, String example, @NotBlank String level) {}

    record VocabResponse(String id, String en, String ipa, String es, String type, String example, String level) {}

    @PostMapping("/api/vocab")
    ResponseEntity<VocabResponse> handle(@Valid @RequestBody CreateVocabRequest request) {
        VocabEntry entry = useCase.execute(
                request.en(),
                request.ipa(),
                request.es(),
                request.type(),
                request.example(),
                new VocabLevel(request.level())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(entry));
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
