package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.GetMiniTestQuestionsUseCase;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class GetMiniTestQuestionsController {

    private final GetMiniTestQuestionsUseCase useCase;

    GetMiniTestQuestionsController(GetMiniTestQuestionsUseCase useCase) {
        this.useCase = useCase;
    }

    record TestQuestionResponse(String id, String type, String question, List<String> options, String level) {
    }

    @GetMapping("/api/assessments/mini-test")
    ResponseEntity<List<TestQuestionResponse>> handle(@RequestParam String module, @RequestParam String level) {
        List<TestQuestion> questions = useCase.execute(module, level);
        List<TestQuestionResponse> response = questions.stream()
                .map(q -> new TestQuestionResponse(q.id(), q.type(), q.question(), q.options(), q.level()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
