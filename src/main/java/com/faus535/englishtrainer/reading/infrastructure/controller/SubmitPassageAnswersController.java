package com.faus535.englishtrainer.reading.infrastructure.controller;

import com.faus535.englishtrainer.reading.application.GetPassageByIdUseCase;
import com.faus535.englishtrainer.reading.application.SubmitReadingAnswersUseCase;
import com.faus535.englishtrainer.reading.domain.ReadingPassage;
import com.faus535.englishtrainer.reading.domain.ReadingQuestion;
import com.faus535.englishtrainer.shared.domain.error.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class SubmitPassageAnswersController {

    private final SubmitReadingAnswersUseCase submitUseCase;
    private final GetPassageByIdUseCase getPassageUseCase;

    SubmitPassageAnswersController(SubmitReadingAnswersUseCase submitUseCase,
                                   GetPassageByIdUseCase getPassageUseCase) {
        this.submitUseCase = submitUseCase;
        this.getPassageUseCase = getPassageUseCase;
    }

    record PassageAnswersRequest(@NotEmpty Map<String, Integer> answers) {}

    record PassageAnswersResponse(String textId, double score, int totalQuestions, int correctAnswers) {}

    @PostMapping("/api/reading/passages/{textId}/answers")
    ResponseEntity<PassageAnswersResponse> handle(@PathVariable UUID textId,
                                                   @Valid @RequestBody PassageAnswersRequest request,
                                                   Authentication authentication) throws NotFoundException {

        String userId = (String) authentication.getPrincipal();

        ReadingPassage passage = getPassageUseCase.execute(textId)
                .orElseThrow(() -> new NotFoundException("Reading passage not found: " + textId));

        List<ReadingQuestion> questions = passage.questions();
        List<Integer> orderedAnswers = new ArrayList<>();
        for (ReadingQuestion question : questions) {
            Integer answer = request.answers().get(question.id().toString());
            orderedAnswers.add(answer != null ? answer : -1);
        }

        SubmitReadingAnswersUseCase.SubmitResult result = submitUseCase.execute(
                UUID.fromString(userId), textId, orderedAnswers);

        return ResponseEntity.ok(new PassageAnswersResponse(
                textId.toString(), result.score(), result.totalQuestions(), result.correctAnswers()));
    }
}
