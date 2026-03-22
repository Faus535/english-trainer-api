package com.faus535.englishtrainer.admin.infrastructure.controller;

import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingPassageRepository;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
class AdminReadingController {

    private final ReadingPassageRepository repository;

    AdminReadingController(ReadingPassageRepository repository) {
        this.repository = repository;
    }

    record QuestionRequest(String question, List<String> options, int correctAnswer, String explanation) {}

    record CreatePassageRequest(@NotBlank String title, @NotBlank String content, @NotBlank String level,
                                String topic, @NotEmpty List<QuestionRequest> questions) {}

    record PassageResponse(String id, String title, String level, String topic, int wordCount) {}

    @PostMapping("/api/admin/reading/passages")
    ResponseEntity<PassageResponse> create(@Valid @RequestBody CreatePassageRequest request) {
        List<ReadingQuestion> questions = request.questions().stream()
                .map(q -> ReadingQuestion.create(q.question(), q.options(), q.correctAnswer(), q.explanation()))
                .toList();

        ReadingPassage passage = ReadingPassage.create(request.title(), request.content(),
                request.level(), request.topic(), questions);
        ReadingPassage saved = repository.save(passage);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PassageResponse(saved.id().value().toString(), saved.title(),
                        saved.level(), saved.topic(), saved.wordCount()));
    }
}
