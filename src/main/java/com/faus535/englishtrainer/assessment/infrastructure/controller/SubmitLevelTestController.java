package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.SubmitLevelTestUseCase;
import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class SubmitLevelTestController {

    private final SubmitLevelTestUseCase useCase;

    SubmitLevelTestController(SubmitLevelTestUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitTestRequest(@NotNull Map<String, String> answers) {
    }

    record LevelTestResultResponse(String id, String userId, int vocabularyScore, int grammarScore,
                                    int listeningScore, int pronunciationScore,
                                    Map<String, String> assignedLevels, String completedAt) {
    }

    @PostMapping("/api/profiles/{userId}/assessments/level-test")
    ResponseEntity<LevelTestResultResponse> handle(@PathVariable UUID userId,
                                                    @Valid @RequestBody SubmitTestRequest request) throws UserProfileNotFoundException, InvalidModuleException {
        LevelTestResult result = useCase.execute(new UserProfileId(userId), request.answers());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    private LevelTestResultResponse toResponse(LevelTestResult result) {
        return new LevelTestResultResponse(
                result.id().value().toString(),
                result.userId().value().toString(),
                result.vocabularyScore(),
                result.grammarScore(),
                result.listeningScore(),
                result.pronunciationScore(),
                result.assignedLevels(),
                result.completedAt().toString()
        );
    }
}
