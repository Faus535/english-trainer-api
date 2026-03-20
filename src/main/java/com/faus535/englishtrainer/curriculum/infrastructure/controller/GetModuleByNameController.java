package com.faus535.englishtrainer.curriculum.infrastructure.controller;

import com.faus535.englishtrainer.curriculum.application.GetModuleDefinitionsUseCase;
import com.faus535.englishtrainer.curriculum.domain.ModuleDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GetModuleByNameController {

    private final GetModuleDefinitionsUseCase useCase;

    GetModuleByNameController(GetModuleDefinitionsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/curriculum/modules/{name}")
    ResponseEntity<ModuleDefinition> handle(@PathVariable String name) {
        return useCase.execute(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
