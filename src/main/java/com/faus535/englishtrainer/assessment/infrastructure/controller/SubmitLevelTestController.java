package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.SubmitLevelTestUseCase;
import com.faus535.englishtrainer.assessment.application.TestCooldownException;
import com.faus535.englishtrainer.assessment.domain.LevelTestResult;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.InvalidModuleException;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import com.faus535.englishtrainer.user.infrastructure.controller.ProfileOwnershipChecker;
import jakarta.validation.Valid;
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
class SubmitLevelTestController {

    private final SubmitLevelTestUseCase useCase;

    SubmitLevelTestController(SubmitLevelTestUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitTestRequest(@NotNull Map<String, String> answers) {
    }

    record LevelTestResultResponse(String id, String userId, int vocabularyScore, int grammarScore,
                                    int listeningScore, int pronunciationScore,
                                    Map<String, String> assignedLevels, String completedAt,
                                    ProfileResponse profile) {
    }

    record ProfileResponse(String id, boolean testCompleted, String levelListening, String levelVocabulary,
                           String levelGrammar, String levelPhrases, String levelPronunciation,
                           String lastTestAt) {
    }

    @PostMapping("/api/profiles/{userId}/assessments/level-test")
    ResponseEntity<LevelTestResultResponse> handle(@PathVariable UUID userId,
                                                    @Valid @RequestBody SubmitTestRequest request,
                                                    Authentication authentication)
            throws UserProfileNotFoundException, InvalidModuleException, TestCooldownException, ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, userId);
        SubmitLevelTestUseCase.SubmitResult result = useCase.execute(new UserProfileId(userId), request.answers());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    private LevelTestResultResponse toResponse(SubmitLevelTestUseCase.SubmitResult result) {
        LevelTestResult test = result.testResult();
        UserProfile profile = result.profile();
        return new LevelTestResultResponse(
                test.id().value().toString(),
                test.userId().value().toString(),
                test.vocabularyScore(),
                test.grammarScore(),
                test.listeningScore(),
                test.pronunciationScore(),
                test.assignedLevels(),
                test.completedAt().toString(),
                new ProfileResponse(
                        profile.id().value().toString(),
                        profile.testCompleted(),
                        profile.levelListening().value(),
                        profile.levelVocabulary().value(),
                        profile.levelGrammar().value(),
                        profile.levelPhrases().value(),
                        profile.levelPronunciation().value(),
                        profile.lastTestAt() != null ? profile.lastTestAt().toString() : null
                )
        );
    }
}
