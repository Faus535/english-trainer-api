package com.faus535.englishtrainer.article.infrastructure.controller;

import com.faus535.englishtrainer.article.application.GenerateArticleUseCase;
import com.faus535.englishtrainer.article.domain.ArticleLevel;
import com.faus535.englishtrainer.article.domain.ArticleReading;
import com.faus535.englishtrainer.article.domain.ArticleTopic;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
class GenerateArticleController {

    private final GenerateArticleUseCase useCase;

    GenerateArticleController(GenerateArticleUseCase useCase) {
        this.useCase = useCase;
    }

    record GenerateArticleRequest(
            @NotBlank @Size(max = 100) String topic,
            @NotBlank @Pattern(regexp = "^(B1|B2|C1)$") String level
    ) {}

    record GenerateArticleAcceptedResponse(UUID id, String status, Instant createdAt) {}

    @PostMapping("/api/article/generate")
    ResponseEntity<GenerateArticleAcceptedResponse> handle(@Valid @RequestBody GenerateArticleRequest request,
                                                            Authentication authentication)
            throws UserProfileNotFoundException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));

        ArticleReading reading = useCase.execute(userId, new ArticleTopic(request.topic()),
                ArticleLevel.fromString(request.level()));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new GenerateArticleAcceptedResponse(
                        reading.id().value(), reading.status().value(), reading.createdAt()));
    }
}
