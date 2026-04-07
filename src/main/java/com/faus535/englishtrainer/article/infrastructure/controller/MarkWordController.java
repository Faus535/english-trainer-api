package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.MarkWordUseCase;
import com.faus535.englishtrainer.article.domain.ArticleMarkedWord;
import com.faus535.englishtrainer.article.domain.ArticleReadingId;
import com.faus535.englishtrainer.article.domain.error.ArticleAccessDeniedException;
import com.faus535.englishtrainer.article.domain.error.ArticleAiException;
import com.faus535.englishtrainer.article.domain.error.ArticleNotFoundException;
import com.faus535.englishtrainer.article.domain.error.DuplicateMarkedWordException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
class MarkWordController {

    private final MarkWordUseCase useCase;

    MarkWordController(MarkWordUseCase useCase) {
        this.useCase = useCase;
    }

    record MarkWordRequest(
            @NotBlank @Size(max = 200) String wordOrPhrase,
            @Size(max = 1000) String contextSentence
    ) {}

    record MarkedWordResponse(UUID id, String wordOrPhrase, String translation,
                               String contextSentence, Instant createdAt) {
        static MarkedWordResponse from(ArticleMarkedWord word) {
            return new MarkedWordResponse(word.id().value(), word.wordOrPhrase(),
                    word.translation(), word.contextSentence(), word.createdAt());
        }
    }

    @PostMapping("/api/article/{id}/words")
    ResponseEntity<MarkedWordResponse> handle(@PathVariable UUID id,
                                               @Valid @RequestBody MarkWordRequest request,
                                               Authentication authentication)
            throws ArticleNotFoundException, ArticleAccessDeniedException,
            ArticleAiException, DuplicateMarkedWordException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        ArticleMarkedWord marked = useCase.execute(userId, new ArticleReadingId(id),
                request.wordOrPhrase(), request.contextSentence());

        return ResponseEntity.status(HttpStatus.CREATED).body(MarkedWordResponse.from(marked));
    }
}
