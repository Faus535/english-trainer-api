package com.faus535.englishtrainer.curriculum.infrastructure.controller;

import com.faus535.englishtrainer.curriculum.application.GetIntegratorSessionsUseCase;
import com.faus535.englishtrainer.curriculum.domain.IntegratorDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetIntegratorsController {

    private final GetIntegratorSessionsUseCase useCase;

    GetIntegratorsController(GetIntegratorSessionsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/curriculum/integrators")
    ResponseEntity<List<IntegratorDefinition>> handle(@RequestParam(required = false) String level) {
        if (level != null) {
            return ResponseEntity.ok(useCase.execute(level));
        }

        return ResponseEntity.ok(useCase.execute());
    }
}
