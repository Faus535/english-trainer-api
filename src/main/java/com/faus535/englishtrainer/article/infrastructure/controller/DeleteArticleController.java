package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.DeleteArticleUseCase;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleCannotBeDeletedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class DeleteArticleController {

    private final DeleteArticleUseCase useCase;

    DeleteArticleController(DeleteArticleUseCase useCase) {
        this.useCase = useCase;
    }

    @DeleteMapping("/api/article/{id}")
    ResponseEntity<Void> handle(@PathVariable UUID id, Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleCannotBeDeletedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        useCase.execute(userId, new ArticleReadingId(id));
        return ResponseEntity.noContent().build();
    }
}
