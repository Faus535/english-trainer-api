package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GetArticleHistoryUseCase;
import com.faus535.englishtrainer.article.domain.ArticleReadingSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetArticleHistoryController {

    private final GetArticleHistoryUseCase useCase;

    GetArticleHistoryController(GetArticleHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/article/history")
    ResponseEntity<List<ArticleReadingSummary>> handle(Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        return ResponseEntity.ok(useCase.execute(userId));
    }
}
