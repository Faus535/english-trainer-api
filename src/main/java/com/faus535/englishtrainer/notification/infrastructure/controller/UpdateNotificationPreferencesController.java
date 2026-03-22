package com.faus535.englishtrainer.notification.infrastructure.controller;

import com.faus535.englishtrainer.notification.application.UpdateNotificationPreferencesUseCase;
import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class UpdateNotificationPreferencesController {

    private final UpdateNotificationPreferencesUseCase useCase;

    UpdateNotificationPreferencesController(UpdateNotificationPreferencesUseCase useCase) {
        this.useCase = useCase;
    }

    record UpdateRequest(Boolean dailyReminder, Boolean streakAlert, Boolean reviewReminder, Integer reminderHour) {}

    record PreferencesResponse(boolean dailyReminder, boolean streakAlert, boolean reviewReminder, int reminderHour) {}

    @PutMapping("/api/notifications/preferences")
    ResponseEntity<PreferencesResponse> handle(@RequestBody UpdateRequest request,
                                                Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        NotificationPreferences prefs = useCase.execute(userId, request.dailyReminder(),
                request.streakAlert(), request.reviewReminder(), request.reminderHour());
        return ResponseEntity.ok(new PreferencesResponse(
                prefs.dailyReminder(), prefs.streakAlert(), prefs.reviewReminder(), prefs.reminderHour()));
    }
}
