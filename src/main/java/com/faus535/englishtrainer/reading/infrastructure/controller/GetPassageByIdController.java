package com.faus535.englishtrainer.reading.infrastructure.controller;

import com.faus535.englishtrainer.reading.application.GetPassageByIdUseCase;
import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetPassageByIdController {

    private final GetPassageByIdUseCase useCase;

    GetPassageByIdController(GetPassageByIdUseCase useCase) {
        this.useCase = useCase;
    }

    record QuestionResponse(String id, String question, List<String> options, int correctAnswer, String explanation) {}

    record PassageResponse(String id, String title, String content, String level, String topic,
                           int wordCount, List<QuestionResponse> questions) {}

    @GetMapping("/api/reading/passages/{id}")
    ResponseEntity<PassageResponse> handle(@PathVariable UUID id) {
        return useCase.execute(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PassageResponse toResponse(ReadingPassage passage) {
        List<QuestionResponse> questions = passage.questions().stream()
                .map(q -> new QuestionResponse(q.id().toString(), q.question(),
                        q.options(), q.correctAnswer(), q.explanation()))
                .toList();
        return new PassageResponse(passage.id().value().toString(), passage.title(), passage.content(),
                passage.level(), passage.topic(), passage.wordCount(), questions);
    }
}
