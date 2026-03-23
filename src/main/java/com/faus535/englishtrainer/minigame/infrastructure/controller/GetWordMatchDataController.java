package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.GetWordMatchDataUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetWordMatchDataController {

    private final GetWordMatchDataUseCase useCase;

    GetWordMatchDataController(GetWordMatchDataUseCase useCase) {
        this.useCase = useCase;
    }

    record WordMatchPairResponse(String en, String es) {}

    @GetMapping("/api/minigames/word-match")
    ResponseEntity<List<WordMatchPairResponse>> handle(@RequestParam String level) {
        VocabLevel vocabLevel = new VocabLevel(level);
        List<GetWordMatchDataUseCase.WordMatchPair> pairs = useCase.execute(vocabLevel);
        List<WordMatchPairResponse> response = pairs.stream()
                .map(pair -> new WordMatchPairResponse(pair.en(), pair.es()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
