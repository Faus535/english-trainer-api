package com.faus535.englishtrainer.admin.infrastructure.controller;

import com.faus535.englishtrainer.phrase.domain.Phrase;
import com.faus535.englishtrainer.phrase.domain.PhraseRepository;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
class AdminPhraseController {

    private final PhraseRepository phraseRepository;

    AdminPhraseController(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    record PhraseResponse(String id, String en, String es, String level) {}

    @GetMapping("/api/admin/phrases")
    ResponseEntity<List<PhraseResponse>> getByLevel(@RequestParam String level) {
        List<PhraseResponse> response = phraseRepository.findByLevel(new VocabLevel(level)).stream()
                .map(p -> new PhraseResponse(p.id().value().toString(), p.en(), p.es(), p.level().value()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
