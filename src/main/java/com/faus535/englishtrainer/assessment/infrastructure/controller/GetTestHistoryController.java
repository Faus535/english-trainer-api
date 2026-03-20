package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.GetTestHistoryUseCase;
import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.assessment.domain.MiniTestResult;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
class GetTestHistoryController {

    private final GetTestHistoryUseCase useCase;

    GetTestHistoryController(GetTestHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    record LevelTestResultResponse(String id, String userId, int vocabularyScore, int grammarScore,
                                    int listeningScore, int pronunciationScore,
                                    Map<String, String> assignedLevels, String completedAt) {
    }

    record MiniTestResultResponse(String id, String userId, String moduleName, String level,
                                   int score, int totalQuestions, int correctAnswers,
                                   String recommendation, String completedAt) {
    }

    record TestHistoryResponse(List<LevelTestResultResponse> levelTests, List<MiniTestResultResponse> miniTests) {
    }

    @GetMapping("/api/profiles/{userId}/assessments/history")
    ResponseEntity<TestHistoryResponse> handle(@PathVariable UUID userId) {
        GetTestHistoryUseCase.TestHistory history = useCase.execute(new UserProfileId(userId));

        List<LevelTestResultResponse> levelTestResponses = history.levelTests().stream()
                .map(this::toLevelTestResponse)
                .collect(Collectors.toList());

        List<MiniTestResultResponse> miniTestResponses = history.miniTests().stream()
                .map(this::toMiniTestResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new TestHistoryResponse(levelTestResponses, miniTestResponses));
    }

    private LevelTestResultResponse toLevelTestResponse(LevelTestResult result) {
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

    private MiniTestResultResponse toMiniTestResponse(MiniTestResult result) {
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
