package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GetArticleMarkedWordsUseCase;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.infrastructure.controller.MarkWordController.MarkedWordResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetMarkedWordsController {

    private final GetArticleMarkedWordsUseCase useCase;

    GetMarkedWordsController(GetArticleMarkedWordsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/article/{id}/words")
    ResponseEntity<List<MarkedWordResponse>> handle(@PathVariable UUID id, Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        List<MarkedWordResponse> words = useCase.execute(userId, new ArticleReadingId(id))
                .stream().map(MarkedWordResponse::from).toList();

        return ResponseEntity.ok(words);
    }
}
