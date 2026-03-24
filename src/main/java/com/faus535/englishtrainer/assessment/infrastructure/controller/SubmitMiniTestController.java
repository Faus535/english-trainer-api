package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.SubmitMiniTestUseCase;
import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.infrastructure.controller.ProfileOwnershipChecker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class SubmitMiniTestController {

    private final SubmitMiniTestUseCase useCase;

    SubmitMiniTestController(SubmitMiniTestUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitMiniTestRequest(@NotBlank String module, @NotBlank String level,
                                  @NotNull Map<String, String> answers) {
    }

    record MiniTestResultResponse(String id, String userId, String moduleName, String level,
                                   int score, int totalQuestions, int correctAnswers,
                                   String recommendation, String completedAt) {
    }

    @PostMapping("/api/profiles/{userId}/assessments/mini-test")
    ResponseEntity<MiniTestResultResponse> handle(@PathVariable UUID userId,
                                                   @Valid @RequestBody SubmitMiniTestRequest request,
                                                   Authentication authentication)
            throws ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, userId);
        MiniTestResult result = useCase.execute(
                new UserProfileId(userId),
                request.module(),
                request.level(),
                request.answers()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    private MiniTestResultResponse toResponse(MiniTestResult result) {
        return new MiniTestResultResponse(
                result.id().value().toString(),
                result.userId().value().toString(),
                result.moduleName(),
                result.level(),
                result.score(),
                result.totalQuestions(),
                result.correctAnswers(),
                result.recommendation(),
                result.completedAt().toString()
        );
    }
}
