package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetImmerseHistoryUseCase;
import com.faus535.englishtrainer.shared.infrastructure.security.RequireProfileOwnership;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
class GetImmerseHistoryController {

    private final GetImmerseHistoryUseCase useCase;

    GetImmerseHistoryController(GetImmerseHistoryUseCase useCase) { this.useCase = useCase; }

    @RequireProfileOwnership
    @GetMapping("/api/profiles/{userId}/immerse/history")
    ResponseEntity<List<ImmerseContentResponse>> handle(@PathVariable UUID userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        var history = useCase.execute(userId, page, size).stream()
                .map(ImmerseContentResponse::from)
                .toList();
        return ResponseEntity.ok(history);
    }
}
