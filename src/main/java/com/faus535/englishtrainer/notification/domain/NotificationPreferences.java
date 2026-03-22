package com.faus535.englishtrainer.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record NotificationPreferences(
        UUID id,
        UUID userId,
        boolean dailyReminder,
        boolean streakAlert,
        boolean reviewReminder,
        int reminderHour,
        Instant updatedAt
) {

    public static NotificationPreferences defaults(UUID userId) {
        return new NotificationPreferences(UUID.randomUUID(), userId, true, true, true, 20, Instant.now());
    }

    public NotificationPreferences withDailyReminder(boolean enabled) {
        return new NotificationPreferences(id, userId, enabled, streakAlert, reviewReminder, reminderHour, Instant.now());
    }

    public NotificationPreferences withStreakAlert(boolean enabled) {
        return new NotificationPreferences(id, userId, dailyReminder, enabled, reviewReminder, reminderHour, Instant.now());
    }

    public NotificationPreferences withReviewReminder(boolean enabled) {
        return new NotificationPreferences(id, userId, dailyReminder, streakAlert, enabled, reminderHour, Instant.now());
    }

    public NotificationPreferences withReminderHour(int hour) {
        return new NotificationPreferences(id, userId, dailyReminder, streakAlert, reviewReminder, hour, Instant.now());
    }
}
