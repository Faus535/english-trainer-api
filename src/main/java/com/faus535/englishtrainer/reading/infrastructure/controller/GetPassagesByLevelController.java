package com.faus535.englishtrainer.reading.infrastructure.controller;

import com.faus535.englishtrainer.reading.application.GetPassagesByLevelUseCase;
import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GetPassagesByLevelController {

    private final GetPassagesByLevelUseCase useCase;

    GetPassagesByLevelController(GetPassagesByLevelUseCase useCase) {
        this.useCase = useCase;
    }

    record QuestionResponse(String id, String question, List<String> options, String explanation) {}

    record PassageResponse(String id, String title, String content, String level, String topic,
                           int wordCount, List<QuestionResponse> questions) {}

    @GetMapping("/api/reading/passages")
    ResponseEntity<List<PassageResponse>> handle(@RequestParam String level) {
        List<PassageResponse> response = useCase.execute(level).stream()
                .map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    private PassageResponse toResponse(ReadingPassage passage) {
        List<QuestionResponse> questions = passage.questions().stream()
                .map(q -> new QuestionResponse(q.id().toString(), q.question(), q.options(), q.explanation()))
                .toList();
        return new PassageResponse(passage.id().value().toString(), passage.title(), passage.content(),
                passage.level(), passage.topic(), passage.wordCount(), questions);
    }
}
