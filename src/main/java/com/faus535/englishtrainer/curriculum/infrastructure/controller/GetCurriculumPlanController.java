package com.faus535.englishtrainer.curriculum.infrastructure.controller;

import com.faus535.englishtrainer.curriculum.application.GetCurriculumPlanUseCase;
import com.faus535.englishtrainer.curriculum.domain.CurriculumBlock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetCurriculumPlanController {

    private final GetCurriculumPlanUseCase useCase;

    GetCurriculumPlanController(GetCurriculumPlanUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/curriculum/plan")
    ResponseEntity<?> handle(@RequestParam(required = false) Integer block) {
        if (block != null) {
            return useCase.execute(block)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        List<CurriculumBlock> plan = useCase.execute();
        return ResponseEntity.ok(plan);
    }
}
