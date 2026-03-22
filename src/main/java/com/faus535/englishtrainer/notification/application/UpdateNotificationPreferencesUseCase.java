package com.faus535.englishtrainer.notification.application;

import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import com.faus535.englishtrainer.notification.domain.NotificationPreferencesRepository;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@UseCase
public class UpdateNotificationPreferencesUseCase {

    private final NotificationPreferencesRepository repository;

    public UpdateNotificationPreferencesUseCase(NotificationPreferencesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificationPreferences execute(UUID userId, Boolean dailyReminder, Boolean streakAlert,
                                            Boolean reviewReminder, Integer reminderHour) {
        NotificationPreferences prefs = repository.findByUserId(userId)
                .orElse(NotificationPreferences.defaults(userId));

        if (dailyReminder != null) prefs = prefs.withDailyReminder(dailyReminder);
        if (streakAlert != null) prefs = prefs.withStreakAlert(streakAlert);
        if (reviewReminder != null) prefs = prefs.withReviewReminder(reviewReminder);
        if (reminderHour != null) prefs = prefs.withReminderHour(reminderHour);

        return repository.save(prefs);
    }
}
