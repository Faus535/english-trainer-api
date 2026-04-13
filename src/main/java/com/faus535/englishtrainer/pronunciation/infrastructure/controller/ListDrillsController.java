package com.faus535.englishtrainer.pronunciation.infrastructure.controller;

import com.faus535.englishtrainer.pronunciation.application.ListDrillsUseCase;
import com.faus535.englishtrainer.pronunciation.application.PronunciationDrillDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class ListDrillsController {

    private final ListDrillsUseCase useCase;

    ListDrillsController(ListDrillsUseCase useCase) {
        this.useCase = useCase;
    }

    record DrillResponse(UUID id, String phrase, String focus, String difficulty, String cefrLevel) {
        static DrillResponse fromDto(PronunciationDrillDto dto) {
            return new DrillResponse(dto.id(), dto.phrase(), dto.focus(), dto.difficulty(), dto.cefrLevel());
        }
    }

    @GetMapping("/api/pronunciation/drills")
    ResponseEntity<List<DrillResponse>> handle(
            @RequestParam String level,
            @RequestParam(required = false) String focus) {
        List<DrillResponse> response = useCase.execute(level, focus).stream()
                .map(DrillResponse::fromDto)
                .toList();
        return ResponseEntity.ok(response);
    }
}
