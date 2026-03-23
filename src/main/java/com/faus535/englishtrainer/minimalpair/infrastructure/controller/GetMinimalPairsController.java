package com.faus535.englishtrainer.minimalpair.infrastructure.controller;

import com.faus535.englishtrainer.minimalpair.application.GetMinimalPairsByLevelUseCase;
import com.faus535.englishtrainer.minimalpair.domain.MinimalPair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetMinimalPairsController {

    private final GetMinimalPairsByLevelUseCase useCase;

    GetMinimalPairsController(GetMinimalPairsByLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record MinimalPairResponse(String id, String word1, String word2,
                                String ipa1, String ipa2, String soundCategory, String level) {}

    @GetMapping("/api/pronunciation/minimal-pairs")
    ResponseEntity<List<MinimalPairResponse>> handle(
            @RequestParam String level,
            @RequestParam(required = false) String sound,
            @RequestParam(defaultValue = "10") int limit) {

        List<MinimalPairResponse> response = useCase.execute(level, sound, limit).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private MinimalPairResponse toResponse(MinimalPair pair) {
        return new MinimalPairResponse(
                pair.id().value().toString(),
                pair.word1(),
                pair.word2(),
                pair.ipa1(),
                pair.ipa2(),
                pair.soundCategory(),
                pair.level()
        );
    }
}
