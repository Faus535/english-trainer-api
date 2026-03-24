package com.faus535.englishtrainer.assessment.infrastructure.controller;

import com.faus535.englishtrainer.assessment.application.GetLevelTestQuestionsUseCase;
import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.error.ProfileOwnershipException;
import com.faus535.englishtrainer.user.infrastructure.controller.ProfileOwnershipChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
class GetLevelTestQuestionsController {

    private final GetLevelTestQuestionsUseCase useCase;

    GetLevelTestQuestionsController(GetLevelTestQuestionsUseCase useCase) {
        this.useCase = useCase;
    }

    record TestQuestionResponse(String id, String type, String question, List<String> options, String level,
                                Double audioSpeed) {
    }

    @GetMapping("/api/profiles/{userId}/assessments/level-test/questions")
    ResponseEntity<List<TestQuestionResponse>> handle(@PathVariable UUID userId, Authentication authentication)
            throws ProfileOwnershipException {
        ProfileOwnershipChecker.check(authentication, userId);
        List<TestQuestion> questions = useCase.execute(new UserProfileId(userId));
        List<TestQuestionResponse> response = questions.stream()
                .map(q -> new TestQuestionResponse(q.id().toString(), q.type(), q.question(), q.options(), q.level(), q.audioSpeed()))
                .toList();
        return ResponseEntity.ok(response);
    }
}
