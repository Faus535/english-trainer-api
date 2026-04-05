package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.GenerateImmerseContentUseCase;
import com.faus535.englishtrainer.immerse.domain.ContentType;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import com.faus535.englishtrainer.user.domain.error.UserProfileNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
            String level,
            String topic
    ) {}

    @PostMapping("/api/immerse/generate")
    ResponseEntity<ImmerseContentResponse> handle(@Valid @RequestBody GenerateContentRequest request,
                                                    Authentication authentication) throws ImmerseAiException, UserProfileNotFoundException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        ImmerseContent content = useCase.execute(userId, request.contentType(), request.level(), request.topic());
        return ResponseEntity.status(HttpStatus.CREATED).body(ImmerseContentResponse.from(content));
    }
}
