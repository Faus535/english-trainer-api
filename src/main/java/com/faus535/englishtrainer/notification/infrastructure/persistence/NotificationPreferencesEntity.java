package com.faus535.englishtrainer.notification.infrastructure.persistence;

import com.faus535.englishtrainer.notification.domain.NotificationPreferences;
import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
class NotificationPreferencesEntity implements Persistable<UUID> {

    @Id private UUID id;
    @Transient private boolean isNew;
    @Column(name = "user_id", nullable = false, unique = true) private UUID userId;
    @Column(name = "daily_reminder") private boolean dailyReminder;
    @Column(name = "streak_alert") private boolean streakAlert;
    @Column(name = "review_reminder") private boolean reviewReminder;
    @Column(name = "reminder_hour") private int reminderHour;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected NotificationPreferencesEntity() {}

    static NotificationPreferencesEntity fromDomain(NotificationPreferences prefs) {
        NotificationPreferencesEntity e = new NotificationPreferencesEntity();
        e.id = prefs.id(); e.isNew = true; e.userId = prefs.userId();
        e.dailyReminder = prefs.dailyReminder(); e.streakAlert = prefs.streakAlert();
        e.reviewReminder = prefs.reviewReminder(); e.reminderHour = prefs.reminderHour();
        e.updatedAt = prefs.updatedAt();
        return e;
    }

    NotificationPreferences toDomain() {
        return new NotificationPreferences(id, userId, dailyReminder, streakAlert, reviewReminder, reminderHour, updatedAt);
    }

    void markAsExisting() { this.isNew = false; }

    @Override public UUID getId() { return id; }
    @Override public boolean isNew() { return isNew; }
}
