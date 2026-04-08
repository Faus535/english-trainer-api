package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.CompleteArticleUseCase;
import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAlreadyCompletedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class CompleteArticleController {

    private final CompleteArticleUseCase useCase;

    CompleteArticleController(CompleteArticleUseCase useCase) {
        this.useCase = useCase;
    }

    record CompleteArticleResponse(UUID id, String status, int xpEarned) {}

    @PostMapping("/api/article/{id}/complete")
    ResponseEntity<CompleteArticleResponse> handle(@PathVariable UUID id, Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleAlreadyCompletedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        ArticleReading completed = useCase.execute(userId, new ArticleReadingId(id));
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new CompleteArticleResponse(
                        completed.id().value(), completed.status().value(), completed.xpEarned()));
    }
}
