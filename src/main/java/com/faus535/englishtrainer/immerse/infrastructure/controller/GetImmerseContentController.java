package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GetImmerseContentUseCase;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseContentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetImmerseContentController {

    private final GetImmerseContentUseCase useCase;

    GetImmerseContentController(GetImmerseContentUseCase useCase) { this.useCase = useCase; }

    @GetMapping("/api/immerse/content/{id}")
    ResponseEntity<ImmerseContentResponse> handle(@PathVariable UUID id) throws ImmerseContentNotFoundException {
        return ResponseEntity.ok(ImmerseContentResponse.from(useCase.execute(id)));
    }
}
