package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.GetLevelTestQuestionsUseCase;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class GetLevelTestQuestionsController {

    private final GetLevelTestQuestionsUseCase useCase;

    GetLevelTestQuestionsController(GetLevelTestQuestionsUseCase useCase) {
        this.useCase = useCase;
    }

    record TestQuestionResponse(String id, String type, String question, List<String> options, String level) {
    }

    @GetMapping("/api/assessments/level-test")
    ResponseEntity<List<TestQuestionResponse>> handle() {
        List<TestQuestion> questions = useCase.execute();
        List<TestQuestionResponse> response = questions.stream()
                .map(q -> new TestQuestionResponse(q.id(), q.type(), q.question(), q.options(), q.level()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
