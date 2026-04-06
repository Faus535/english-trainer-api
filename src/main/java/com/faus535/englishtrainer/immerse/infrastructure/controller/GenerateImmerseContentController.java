package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GenerateImmerseContentUseCase;
import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
class GenerateImmerseContentController {

    private final GenerateImmerseContentUseCase useCase;

    GenerateImmerseContentController(GenerateImmerseContentUseCase useCase) { this.useCase = useCase; }

    record GenerateContentRequest(
            @NotNull ContentType contentType,
            @Pattern(regexp = "(?i)a1|a2|b1|b2|c1|c2", message = "Level must be a valid CEFR level")
            String level,
            @Size(max = 200, message = "Topic must be at most 200 characters")
            String topic
    ) {}

    @PostMapping("/api/immerse/generate")
    ResponseEntity<ImmerseContentResponse> handle(@Valid @RequestBody GenerateContentRequest request,
                                                    Authentication authentication) throws UserProfileNotFoundException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        ImmerseContent content = useCase.execute(userId, request.contentType(), request.level(), request.topic());
        return ResponseEntity.status(HttpStatus.CREATED).body(ImmerseContentResponse.from(content));
    }
}
