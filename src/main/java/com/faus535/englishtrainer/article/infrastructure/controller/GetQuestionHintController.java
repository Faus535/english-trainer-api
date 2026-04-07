package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GetQuestionHintUseCase;
import com.faus535.englishtrainer.article.domain.ArticleQuestionId;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.ArticleQuestionNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GetQuestionHintController {

    private final GetQuestionHintUseCase useCase;

    GetQuestionHintController(GetQuestionHintUseCase useCase) {
        this.useCase = useCase;
    }

    record HintResponse(String hint) {}

    @GetMapping("/api/article/{id}/questions/{qId}/hint")
    ResponseEntity<HintResponse> handle(@PathVariable UUID id, @PathVariable UUID qId,
                                         Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleQuestionNotFoundException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        String hint = useCase.execute(userId, new ArticleReadingId(id), new ArticleQuestionId(qId));
        return ResponseEntity.ok(new HintResponse(hint));
    }
}
