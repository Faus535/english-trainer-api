package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.application.RecordTutorErrorUseCase;
import com.faus535.englishtrainer.tutorerror.domain.TutorError;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class RecordTutorErrorController {

    private final RecordTutorErrorUseCase useCase;

    RecordTutorErrorController(RecordTutorErrorUseCase useCase) {
        this.useCase = useCase;
    }

    record RecordErrorRequest(
            @NotBlank @Pattern(regexp = "(?i)(GRAMMAR|VOCABULARY|PRONUNCIATION)") String errorType,
            @NotBlank String originalText,
            @NotBlank String correctedText,
            String rule
    ) {}

    @PostMapping("/api/profiles/{userId}/tutor/errors")
    ResponseEntity<TutorErrorResponse> handle(
            @PathVariable UUID userId,
            @Valid @RequestBody RecordErrorRequest request) {

        TutorError error = useCase.execute(
                new UserProfileId(userId),
                request.errorType(),
                request.originalText(),
                request.correctedText(),
                request.rule());

        return ResponseEntity.status(HttpStatus.CREATED).body(TutorErrorResponse.from(error));
    }
}
