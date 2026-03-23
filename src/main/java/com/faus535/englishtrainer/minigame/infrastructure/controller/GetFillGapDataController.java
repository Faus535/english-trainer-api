package com.faus535.englishtrainer.minigame.infrastructure.controller;

import com.faus535.englishtrainer.minigame.application.GetFillGapDataUseCase;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetFillGapDataController {

    private final GetFillGapDataUseCase useCase;

    GetFillGapDataController(GetFillGapDataUseCase useCase) {
        this.useCase = useCase;
    }

    record FillGapItemResponse(String en, String es) {}

    @GetMapping("/api/minigames/fill-gap")
    ResponseEntity<List<FillGapItemResponse>> handle(@RequestParam String level) {
        VocabLevel vocabLevel = new VocabLevel(level);
        List<GetFillGapDataUseCase.FillGapItem> items = useCase.execute(vocabLevel);
        List<FillGapItemResponse> response = items.stream()
                .map(item -> new FillGapItemResponse(item.en(), item.es()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
