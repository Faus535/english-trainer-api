package com.faus535.englishtrainer.writing.infrastructure.controller;

import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import com.faus535.englishtrainer.writing.application.SubmitWritingUseCase;
import com.faus535.englishtrainer.writing.domain.WritingFeedback;
import com.faus535.englishtrainer.writing.domain.WritingSubmission;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SubmitWritingController {

    private final SubmitWritingUseCase useCase;

    SubmitWritingController(SubmitWritingUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitRequest(@NotNull UUID exerciseId, @NotBlank String text) {}

    record SubmitResponse(String id, int wordCount, WritingFeedback feedback) {}

    @PostMapping("/api/profiles/{userId}/writing/submit")
    ResponseEntity<SubmitResponse> handle(@PathVariable UUID userId,
                                           @Valid @RequestBody SubmitRequest request) throws NotFoundException {

        WritingSubmission submission = useCase.execute(userId, request.exerciseId(), request.text());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubmitResponse(submission.id().toString(), submission.wordCount(), submission.feedback()));
    }
}
