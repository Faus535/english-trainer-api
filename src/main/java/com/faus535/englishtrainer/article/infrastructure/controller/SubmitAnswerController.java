package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.SubmitAnswerUseCase;
import com.faus535.englishtrainer.article.domain.ArticleQuestionAnswer;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
class SubmitAnswerController {

    private final SubmitAnswerUseCase useCase;

    SubmitAnswerController(SubmitAnswerUseCase useCase) {
        this.useCase = useCase;
    }

    record SubmitAnswerRequest(@NotBlank @Size(max = 5000) String answer) {}

    record AnswerResponse(UUID id, boolean isContentCorrect, String grammarFeedback,
                           String styleFeedback, String correctionSummary) {
        static AnswerResponse from(ArticleQuestionAnswer a) {
            return new AnswerResponse(a.id().value(), a.isContentCorrect(),
                    a.grammarFeedback(), a.styleFeedback(), a.correctionSummary());
        }
    }

    @PostMapping("/api/article/{id}/questions/{qId}/answer")
    ResponseEntity<AnswerResponse> handle(@PathVariable UUID id, @PathVariable UUID qId,
                                           @Valid @RequestBody SubmitAnswerRequest request,
                                           Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleQuestionNotFoundException, QuestionAlreadyAnsweredException,
            AnswerTooShortException, ArticleAiException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        ArticleQuestionAnswer answer = useCase.execute(userId, new ArticleReadingId(id),
                new ArticleQuestionId(qId), request.answer());

        return ResponseEntity.ok(AnswerResponse.from(answer));
    }
}
