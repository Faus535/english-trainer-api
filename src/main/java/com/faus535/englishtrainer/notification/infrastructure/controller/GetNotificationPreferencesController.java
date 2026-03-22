package com.faus535.englishtrainer.notification.infrastructure.controller;

import com.faus535.englishtrainer.notification.application.GetNotificationPreferencesUseCase;
import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class GetNotificationPreferencesController {

    private final GetNotificationPreferencesUseCase useCase;

    GetNotificationPreferencesController(GetNotificationPreferencesUseCase useCase) {
        this.useCase = useCase;
    }

    record PreferencesResponse(boolean dailyReminder, boolean streakAlert, boolean reviewReminder, int reminderHour) {}

    @GetMapping("/api/notifications/preferences")
    ResponseEntity<PreferencesResponse> handle(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        NotificationPreferences prefs = useCase.execute(userId);
        return ResponseEntity.ok(new PreferencesResponse(
                prefs.dailyReminder(), prefs.streakAlert(), prefs.reviewReminder(), prefs.reminderHour()));
    }
}
