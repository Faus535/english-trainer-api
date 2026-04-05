package com.faus535.englishtrainer.immerse.infrastructure.controller;

import com.faus535.englishtrainer.immerse.application.SubmitImmerseContentUseCase;
import com.faus535.englishtrainer.immerse.domain.ImmerseContent;
import com.faus535.englishtrainer.immerse.domain.error.ImmerseAiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
class SubmitImmerseContentController {

    private final SubmitImmerseContentUseCase useCase;

    SubmitImmerseContentController(SubmitImmerseContentUseCase useCase) { this.useCase = useCase; }

    record SubmitContentRequest(
            String sourceUrl,
            String title,
            @NotBlank @Size(max = 10000) String rawText,
            String level
    ) {}

    @PostMapping("/api/immerse/content")
    ResponseEntity<ImmerseContentResponse> handle(@Valid @RequestBody SubmitContentRequest request,
                                                    Authentication authentication) throws ImmerseAiException {
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication.getDetails();
        UUID userId = UUID.fromString(details.get("profileId"));
        String title = request.title() != null ? request.title() : "Untitled";
        ImmerseContent content = useCase.execute(userId, request.sourceUrl(), title, request.rawText(), request.level());
        return ResponseEntity.status(HttpStatus.CREATED).body(ImmerseContentResponse.from(content));
    }
}
