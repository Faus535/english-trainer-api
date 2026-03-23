package com.faus535.englishtrainer.reading.infrastructure.controller;

import com.faus535.englishtrainer.reading.application.GetPassageByIdUseCase;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetPassageQuestionsController {

    private final GetPassageByIdUseCase useCase;

    GetPassageQuestionsController(GetPassageByIdUseCase useCase) {
        this.useCase = useCase;
    }

    record QuestionResponse(String id, String question, List<String> options, int correctAnswer, String explanation) {}

    @GetMapping("/api/reading/passages/{id}/questions")
    ResponseEntity<List<QuestionResponse>> handle(@PathVariable UUID id) {
        return useCase.execute(id)
                .map(passage -> passage.questions().stream()
                        .map(q -> new QuestionResponse(q.id().toString(), q.question(),
                                q.options(), q.correctAnswer(), q.explanation()))
                        .toList())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
