package com.faus535.englishtrainer.tutorerror.infrastructure.controller;

import com.faus535.englishtrainer.tutorerror.application.GetErrorTrendUseCase;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
class GetErrorTrendController {

    private final GetErrorTrendUseCase useCase;

    GetErrorTrendController(GetErrorTrendUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/api/profiles/{userId}/tutor/errors/trend")
    ResponseEntity<List<Map<String, Object>>> handle(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "8") int weeks) {

        List<Map<String, Object>> trend = useCase.execute(new UserProfileId(userId), weeks);
        return ResponseEntity.ok(trend);
    }
}
