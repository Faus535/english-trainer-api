package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetImmerseVocabularyUseCase;
import com.faus535.englishtrainer.immerse.domain.VocabularyItem;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetImmerseVocabularyController {

    private final GetImmerseVocabularyUseCase useCase;

    GetImmerseVocabularyController(GetImmerseVocabularyUseCase useCase) { this.useCase = useCase; }

    @GetMapping("/api/immerse/content/{id}/vocabulary")
    ResponseEntity<List<VocabularyItem>> handle(@PathVariable UUID id) throws ImmerseContentNotFoundException {
        return ResponseEntity.ok(useCase.execute(id));
    }
}
