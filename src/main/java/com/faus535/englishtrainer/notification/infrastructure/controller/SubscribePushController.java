package com.faus535.englishtrainer.notification.infrastructure.controller;

import com.faus535.englishtrainer.notification.application.SubscribePushUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class SubscribePushController {

    private final SubscribePushUseCase useCase;

    SubscribePushController(SubscribePushUseCase useCase) {
        this.useCase = useCase;
    }

    record SubscribeRequest(@NotBlank String endpoint, @NotBlank String p256dh, @NotBlank String auth) {}

    @PostMapping("/api/notifications/subscribe")
    ResponseEntity<Void> handle(@Valid @RequestBody SubscribeRequest request,
                                 Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        useCase.execute(userId, request.endpoint(), request.p256dh(), request.auth());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
