package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GetPreReadingUseCase;
import com.faus535.englishtrainer.article.domain.PreReadingData;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetPreReadingController {

    private final GetPreReadingUseCase useCase;

    GetPreReadingController(GetPreReadingUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/article/{id}/pre-reading")
    ResponseEntity<PreReadingResponse> handle(@PathVariable UUID id, Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleAiException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        PreReadingData data = useCase.execute(userId, new ArticleReadingId(id));
        return ResponseEntity.ok(PreReadingResponse.from(data));
    }

    record KeyWordDto(String word, String translation, String definition) {}

    record PreReadingResponse(List<KeyWordDto> keyWords, String predictiveQuestion) {
        static PreReadingResponse from(PreReadingData data) {
            List<KeyWordDto> keyWords = data.keyWords().stream()
                    .map(k -> new KeyWordDto(k.word(), k.translation(), k.definition()))
                    .toList();
            return new PreReadingResponse(keyWords, data.predictiveQuestion());
        }
    }
}
