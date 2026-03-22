package com.faus535.englishtrainer.admin.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.application.CreateVocabEntryUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import com.faus535.englishtrainer.vocabulary.domain.VocabRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
class AdminVocabController {

    private final CreateVocabEntryUseCase createUseCase;
    private final VocabRepository vocabRepository;

    AdminVocabController(CreateVocabEntryUseCase createUseCase, VocabRepository vocabRepository) {
        this.createUseCase = createUseCase;
        this.vocabRepository = vocabRepository;
    }

    record VocabRequest(@NotBlank String en, String ipa, @NotBlank String es,
                        String type, String example, @NotBlank String level) {}

    record VocabResponse(String id, String en, String ipa, String es, String type, String example, String level) {}

    @GetMapping("/api/admin/vocab")
    ResponseEntity<List<VocabResponse>> getAll() {
        List<VocabResponse> response = vocabRepository.findAll().stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/admin/vocab")
    ResponseEntity<VocabResponse> create(@Valid @RequestBody VocabRequest request) {
        VocabEntry entry = createUseCase.execute(request.en(), request.ipa(), request.es(),
                request.type(), request.example(), new VocabLevel(request.level()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(entry));
    }

    private VocabResponse toResponse(VocabEntry entry) {
        return new VocabResponse(entry.id().value().toString(), entry.en(), entry.ipa(),
                entry.es(), entry.type(), entry.example(), entry.level().value());
    }
}
