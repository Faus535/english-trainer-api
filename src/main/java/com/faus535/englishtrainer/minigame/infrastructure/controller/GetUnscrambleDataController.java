package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.GetUnscrambleDataUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetUnscrambleDataController {

    private final GetUnscrambleDataUseCase useCase;

    GetUnscrambleDataController(GetUnscrambleDataUseCase useCase) {
        this.useCase = useCase;
    }

    record UnscrambleItemResponse(String en, String es) {}

    @GetMapping("/api/minigames/unscramble")
    ResponseEntity<List<UnscrambleItemResponse>> handle(@RequestParam String level) {
        VocabLevel vocabLevel = new VocabLevel(level);
        List<GetUnscrambleDataUseCase.UnscrambleItem> items = useCase.execute(vocabLevel);
        List<UnscrambleItemResponse> response = items.stream()
                .map(item -> new UnscrambleItemResponse(item.en(), item.es()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
