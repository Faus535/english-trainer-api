package com.faus535.englishtrainer.vocabulary.infrastructure.controller;

import com.faus535.englishtrainer.vocabulary.application.GetVocabByLevelAndBlockUseCase;
import com.faus535.englishtrainer.vocabulary.application.GetVocabByLevelUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabEntry;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetVocabByLevelController {

    private final GetVocabByLevelUseCase getByLevelUseCase;
    private final GetVocabByLevelAndBlockUseCase getByLevelAndBlockUseCase;

    GetVocabByLevelController(GetVocabByLevelUseCase getByLevelUseCase,
                               GetVocabByLevelAndBlockUseCase getByLevelAndBlockUseCase) {
        this.getByLevelUseCase = getByLevelUseCase;
        this.getByLevelAndBlockUseCase = getByLevelAndBlockUseCase;
    }

    record VocabResponse(String id, String en, String ipa, String es, String type, String example, String level,
                         String category, Integer block, String blockTitle) {}

    @GetMapping("/api/vocab/level/{level}")
    ResponseEntity<List<VocabResponse>> handle(@PathVariable String level,
                                                @RequestParam(required = false) Integer block) {
        VocabLevel vocabLevel = new VocabLevel(level);
        List<VocabEntry> entries = (block != null)
                ? getByLevelAndBlockUseCase.execute(vocabLevel, block)
                : getByLevelUseCase.execute(vocabLevel);

        List<VocabResponse> response = entries.stream()
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
                entry.level().value(),
                entry.category(),
                entry.block(),
                entry.blockTitle()
        );
    }
}
