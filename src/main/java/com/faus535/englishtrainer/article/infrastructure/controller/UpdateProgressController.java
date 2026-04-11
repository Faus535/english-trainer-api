package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.UpdateArticleProgressUseCase;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class UpdateProgressController {

    private final UpdateArticleProgressUseCase useCase;

    UpdateProgressController(UpdateArticleProgressUseCase useCase) {
        this.useCase = useCase;
    }

    record UpdateProgressRequest(
            @Min(0) int paragraphIndex,
            @Min(0) int questionIndex
    ) {}

    @PatchMapping("/api/article/{id}/progress")
    ResponseEntity<Void> handle(@PathVariable UUID id,
                                 @Valid @RequestBody UpdateProgressRequest request,
                                 Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        useCase.execute(userId, new ArticleReadingId(id), request.paragraphIndex(), request.questionIndex());
        return ResponseEntity.noContent().build();
    }
}
