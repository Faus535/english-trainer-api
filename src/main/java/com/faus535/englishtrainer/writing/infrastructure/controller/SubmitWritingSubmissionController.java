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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class SubmitWritingSubmissionController {

    private final SubmitWritingUseCase useCase;

    SubmitWritingSubmissionController(SubmitWritingUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitRequest(@NotNull String exerciseId, @NotBlank String content) {}

    record CorrectionResponse(String original, String corrected, String type, String explanation) {}

    record SubmitResponse(String id, String exerciseId, double score, List<CorrectionResponse> corrections,
                          List<String> suggestions, String summary, String submittedAt) {}

    @PostMapping("/api/writing/submissions")
    ResponseEntity<SubmitResponse> handle(@Valid @RequestBody SubmitRequest request,
                                          Authentication authentication) throws NotFoundException {
        UUID userId = UUID.fromString(authentication.getName());
        WritingSubmission submission = useCase.execute(userId, UUID.fromString(request.exerciseId()), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(submission));
    }

    private SubmitResponse toResponse(WritingSubmission submission) {
        WritingFeedback feedback = submission.feedback();

        List<CorrectionResponse> corrections = feedback.corrections().stream()
                .map(c -> new CorrectionResponse(c, "", "grammar", c))
                .toList();

        return new SubmitResponse(
                submission.id().toString(),
                submission.exerciseId().value().toString(),
                feedback.overallScore(),
                corrections,
                List.of(feedback.generalFeedback()),
                feedback.levelAssessment(),
                submission.submittedAt().toString()
        );
    }
}
