package com.faus535.englishtrainer.curriculum.infrastructure.controller;

import com.faus535.englishtrainer.curriculum.application.GetModuleDefinitionsUseCase;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetModuleDefinitionsController {

    private final GetModuleDefinitionsUseCase useCase;

    GetModuleDefinitionsController(GetModuleDefinitionsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/curriculum/modules")
    ResponseEntity<List<ModuleDefinition>> handle() {
        return ResponseEntity.ok(useCase.execute());
    }
}
